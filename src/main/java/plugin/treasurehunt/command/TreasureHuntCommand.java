package plugin.treasurehunt.command;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SplittableRandom;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import plugin.treasurehunt.Main;
import plugin.treasurehunt.data.ExecutingPlayer;
import plugin.treasurehunt.mapper.PlayerScoreMapper;
import plugin.treasurehunt.mapper.data.PlayerScore;

/**
 * １分以内にキツネ（宝）を探して捕まえる（右クリックする）ゲームを起動するコマンドです。
 * スコアは捕まえるまでの秒数で変わります。
 * １分以内に捕まえられなければゲームオーバー。
 * キツネを捕まえる前にボーナスウサギを捕まえると、スコアが2倍になります。
 * 結果はSQLにプレイヤー名、スコア、日時が保存されます。
 */

public class TreasureHuntCommand implements CommandExecutor, Listener {

  public static final int SECONDS_TO_TICKS = 20;
  public static final int GAME_DURATION = 60; // ゲーム時間（秒）
  public static String LIST = "list";

  private final Main main;
  private boolean isGameActive = false;
  private Instant gameStartTime;
  private ExecutingPlayer currentExecutingPlayer;
  private BukkitTask gameEndTask;
  private final Map<EntityType, Integer> entitySpawnCounts = new HashMap<>();
  private final List<Entity> spawnEntityList = new ArrayList<>();
  private boolean rabbitClicked = false;
  private BossBar timerBar;

  private final SqlSessionFactory sqlSessionFactory;

  public TreasureHuntCommand(Main main) {
    this.main = main;

    try {
      InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
      this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Player player = checkPlayerAndGameState(sender);

    if (player == null) {
      return true;
    }

    // 引数にListを入力するとスコア一覧がメッセージ画面に表示
    if (args.length == 1 && LIST.equals(args[0])) {
      sendPlayerScoreList(player);
      return false;
    }

    startCountdown(player);

    return true;
  }


  private Player checkPlayerAndGameState(CommandSender sender) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤー専用です。");
      return null;
    }

    //　重複実行の防止
    if (isGameActive) {
      player.sendMessage(ChatColor.RED + "ゲームはすでに進行中です。");
      return null;
    }
    return player;
  }

  public void startCountdown(Player player){
    new GameStartCountdown(this,player).runTaskTimer(main,0,SECONDS_TO_TICKS);
  }


  public void gameStart(Player player) {
    isGameActive = true;
    gameStartTime = Instant.now();
    currentExecutingPlayer = new ExecutingPlayer(player.getName(),0, gameStartTime);
    rabbitClicked = false;

    entitiesSpawn(player);

    player.sendMessage(ChatColor.GOLD +
        "ヒント: キツネの前にウサギをクリックするとスコア2倍！ウサギは１匹だけ！");

    bossVar(player);

    scheduleTimeoutGameOver(player);
  }


  @EventHandler
  public void PlayerGetEntityEvent(PlayerInteractEntityEvent e) {
    if (checkedGameActive())
      return;

    Player player = e.getPlayer();
    Entity clickedEntity = e.getRightClicked();

    if (clickedEntity instanceof Rabbit) {
      rabbitClicked = true;
      player.sendMessage(ChatColor.GREEN + "ウサギボーナス！キツネを捕まえられればスコアが2倍！");
      clickedEntity.remove();
      return;
    }

    if (clickedEntity instanceof Fox) {
      endGame(player, false);
    }
  }


  /**
   * ゲームを通常プレイ出来た場合の処理
   * ウサギを捕まえた時のボーナスも含む
   * @param player  プレイヤー
   * @param gameDuration  ゲームにかかった経過時間
   */
  private void normalEnd(Player player, Duration gameDuration) {
    int score = calculateScore(gameDuration);
    if (rabbitClicked) {
      score *= 2;
      player.sendMessage(ChatColor.GREEN + "ウサギボーナス：スコア２倍！");
    }
    currentExecutingPlayer.setScore(score);

    player.sendTitle("キツネ発見！ゲーム終了",
        "経過時間: " + gameDuration.getSeconds()  + " 秒！ 得点は " + score + " 点！",
        0,3 * SECONDS_TO_TICKS,SECONDS_TO_TICKS);
  }


    /**
   * ゲーム終了時のスコアと事後処理。
   * @param player  プレイヤー
   * @param isTimeout タイムアウト時の処理も含む
   */
  private void endGame(Player player, boolean isTimeout) {
    if (checkedGameActive())
      return;

    isGameActive = false;
    Instant endTime = Instant.now();
    Duration gameDuration = Duration.between(gameStartTime, endTime);

    cleanupBossBar();

    try {
      if (isTimeout) {
        currentExecutingPlayer.setScore(0);
        player.sendTitle("GAME OVER" , "時間切れです。",0,3 * SECONDS_TO_TICKS,SECONDS_TO_TICKS);

      } else {
        normalEnd(player, gameDuration);
      }

      try (SqlSession session = sqlSessionFactory.openSession(true)) {
          PlayerScoreMapper mapper = session.getMapper(PlayerScoreMapper.class);
          mapper.insert(
              new PlayerScore(currentExecutingPlayer.getPlayerName()
                  , currentExecutingPlayer.getScore()));
        }
    } catch (Exception e) {
      e.printStackTrace();

    } finally {
      // ゲーム用にスポーンしたエンティティ消去・スコアと状態をクリア
      spawnEntityList.forEach(Entity::remove);
      spawnEntityList.clear();
      gameStartTime = null;
      currentExecutingPlayer = null;
      rabbitClicked = false;

      if (gameEndTask != null) {
        gameEndTask.cancel();
        gameEndTask = null;
      }
    }
  }


  /**
   * BossVarに関するオブジェクト
   * （ゲーム終了時の設定クリアは除く）
   * @param player  プレイヤー
   */
  private void bossVar(Player player) {
    timerBar = Bukkit.createBossBar("残り時間", BarColor.PINK, BarStyle.SOLID);
    timerBar.addPlayer(player);
    timerBar.setVisible(true);

    new BukkitRunnable() {
      int timeLeft = GAME_DURATION;

      @Override
      public void run() {
        if (timeLeft > 0 && isGameActive) {
          timerBar.setProgress((double) timeLeft / GAME_DURATION);
          timerBar.setTitle("残り時間: " + timeLeft + "秒");
          timeLeft--;
        } else {
          this.cancel();
          timerBar.removeAll();
          timerBar.setVisible(false);
          if (isGameActive) {
            endGame(player, true);
          }
        }
      }
    }.runTaskTimer(main, 0, SECONDS_TO_TICKS);
  }


  /**
   * BossVarの設定をクリアするオブジェクト
   */
  private void cleanupBossBar() {
    if (timerBar != null) {
      timerBar.removeAll();
      timerBar.setVisible(false);
    }
  }


  /**
   * ゲームが進行中か否かをチェックするオブジェクト
   * @return  true or false
   */
  private boolean checkedGameActive() {
    return !isGameActive;
  }


  /**
   * 1分後にリストにキツネがいたら時間切れでゲームオーバーにするための
   * カウントダウンを始めるオブジェクト。
   * @param player  プレイヤー
   */
  private void scheduleTimeoutGameOver(Player player) {
    gameEndTask = Bukkit.getScheduler().runTaskLater(main, () -> {
      if (isGameActive && spawnEntityList.stream().anyMatch(entity ->
          entity.getType() == EntityType.FOX)) {
        endGame(player,true);
      }
    }, 60 * SECONDS_TO_TICKS);
  }


  /**
   *
   * ゲームのスコアを計算するオブジェクト
   * @param gameDuration  ゲームにかかった経過時間
   * @return  score
   */
  private int calculateScore(Duration gameDuration) {
    // スコアを計算して設定
    long seconds = gameDuration.getSeconds();
    if (seconds <= 5) {
      return 100;
    } else if (seconds <= 10) {
      return 80;
    } else if (seconds <= 20) {
      return 50;
    } else if (seconds <= 60) {
      return 30;
    }
    return 0;
  }


  /**
   * 現在登録されているスコアの一覧をメッセージに出力する
   *
   * @param player  プレイヤー
   */
  private void sendPlayerScoreList(Player player) {
    try (SqlSession session = sqlSessionFactory.openSession()) {
      PlayerScoreMapper mapper = session.getMapper(PlayerScoreMapper.class);
      List<PlayerScore> playerScoreList = mapper.selectList();

      for (PlayerScore playerScore : playerScoreList) {
        player.sendMessage(playerScore.getId() + "  |  "
            + playerScore.getPlayerName() + "  |  "
            + playerScore.getScore() + "  |  "
            + playerScore.getRegisteredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
      }
    }
  }


  /**
   * コマンドが実行されたときに出現するエンティティを設定
   * ボーナスウサギの難易度軽減のために動きを固定
   * 出現したエンティティ情報をListに保存
   *
   * @param player  コマンドを実行したプレイヤー
   */
  public void entitiesSpawn(Player player) {
    entitySpawnCounts.clear();
    entitySpawnCounts.put(EntityType.PANDA, 20);
    entitySpawnCounts.put(EntityType.ARMADILLO, 20);
    entitySpawnCounts.put(EntityType.LLAMA, 30);
    entitySpawnCounts.put(EntityType.CAMEL, 20);
    entitySpawnCounts.put(EntityType.FOX, 1);
    entitySpawnCounts.put(EntityType.RABBIT, 1);

    for (Map.Entry<EntityType, Integer> entry : entitySpawnCounts.entrySet()) {
      EntityType entityType = entry.getKey();
      int count = entry.getValue();
      for (int i = 0; i < count; i++) {
        Location location = getEntitySpawnLocation(player);
        Entity entity = Objects.requireNonNull(location.getWorld()).spawnEntity(location,entityType);

        if (entity instanceof Rabbit rabbit) {
          rabbit.setInvulnerable(true);
          rabbit.setAI(false);
          rabbit.setPersistent(true);
          rabbit.setRemoveWhenFarAway(false);
        }

        spawnEntityList.add(entity);
      }
    }
  }


  /**
   * エンティティの出現エリアを取得します
   * 出現エリアはX軸とZ軸は自分の位置からプラス、ランダムで-10～9の値が設定されます。
   * Y軸はプレイヤーと同じ位置になります
   *
   * @param player　コマンドを実行したプレイヤー
   * @return  エンティティの出現場所
   */
  private Location getEntitySpawnLocation(Player player) {
    Location playerLocation = player.getLocation();
    int randomX = new SplittableRandom().nextInt(20) -10;
    int randomZ = new SplittableRandom().nextInt(20) -10;

    double x = playerLocation.getX() + randomX;
    double y = playerLocation.getY();
    double z = playerLocation.getZ() + randomZ;

    return new Location(player.getWorld(), x, y, z);
  }


}
