package plugin.treasurehunt.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitScheduler;
import plugin.treasurehunt.Main;
import plugin.treasurehunt.data.PlayerScore;

public class TreasureHuntCommand implements CommandExecutor, Listener {

  public static String LIST = "list";

  private Main main;
  private PlayerScore playerScore;
  private Map<EntityType, Integer> entitySpawnCounts = new HashMap<>();
  private List<Entity> spawnEntityList = new ArrayList<>();


  public TreasureHuntCommand(Main main) {
    this.main = main;
  }


  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("このコマンドはプレイヤーのみが使用できます。");
      return true;
    }

//
//    // プレイヤーの名前に基づいてPlayerScoreを取得
//    PlayerScore playerScore = PlayerScore.selectList().stream()
//        .filter(ps -> ps.getPlayerName().equals(player.getName()))
//        .findFirst()
//        .orElse(null);

    // もしスコアが見つからない場合は、新しいPlayerScoreを作成してリストに追加
    if (playerScore == null) {
      playerScore = new PlayerScore();
      playerScore.setPlayerName(player.getName());
      PlayerScore.addPlayerScore(playerScore);
    }

//
//    // デバッグ用: リセット前のスコアと開始時間を表示
//    System.out.println("Before reset: score=" + playerScore.getScore() + ", startGameTime=" + playerScore.getStartGameTime());

    playerScore.resetScore(); // ゲーム開始前にスコアをリセット
//
//    // デバッグ用: リセット後のスコアと開始時間を表示
//    System.out.println("resetScore called: score=" + playerScore.getScore() + ", startGameTime=" + playerScore.getStartGameTime());

    // 開始ゲーム時間を設定
    
    playerScore.setStartGameTime(player.getWorld().getGameTime());
//
//    // デバッグ用: 新しいゲーム時間を表示
//    System.out.println("New game time set: " + playerScore.getStartGameTime());


    if (args.length == 1 && LIST.equals(args[0])) {
      sendPlayerScoreList(player);
      return true;
    }

    // 経過時間を計算する際にティックから秒に換算
    long endGameTime = player.getWorld().getGameTime();
    long elapsedTicks = endGameTime - playerScore.getStartGameTime();
    long elapsedTimeInSeconds = elapsedTicks / 20;
//    System.out.println("End game time: " + endGameTime);  // デバッグ用の記述
//    System.out.println("Elapsed ticks: " + elapsedTicks); // デバッグ用の記述
//    System.out.println("Elapsed time in seconds: " + elapsedTimeInSeconds); // デバッグ用の記述

    EntitiesSpawn(player);

      player.sendTitle("キツネを探せ！",
          "1分以内にキツネを右クリックで捕まえて。",
          0,60,20);

      // 1分後にキツネを捕まえられていなければゲームオーバー
      BukkitScheduler scheduler = Bukkit.getScheduler();
      scheduler.runTaskLater(main,() -> {
        if (!spawnEntityList.isEmpty()) {
          player.sendMessage("時間切れ！ゲームオーバーです。");
          spawnEntityList.forEach(Entity::remove);
          spawnEntityList.clear();
        }
      }, 1200L);
      return true;
    }


  /**
   * 現在登録されているスコアの一覧をメッセージに出力する
   *
   * @param player  プレイヤー
   */
  private void sendPlayerScoreList(Player player) {
    List<PlayerScore> playerScoreList = PlayerScore.selectList();
    for (PlayerScore ps : playerScoreList) {
      player.sendMessage(ps.getPlayerName() + "  |  "
          + ps.getScore() + "  |  "
          + ps.getStartGameTime());
    }
  }


  @EventHandler
  public void PlayerGetEntityEvent(PlayerInteractEntityEvent e) {
    Player player = e.getPlayer();

    PlayerScore playerScore = PlayerScore.selectList().stream()
        .filter(ps -> ps.getPlayerName().equals(player.getName()))
        .findFirst()
        .orElse(null);

    if (playerScore == null) {
      player.sendMessage("スコア情報が見つかりませんでした。");
      return;
    }

    if (e.getRightClicked() instanceof Fox) {
      long endGameTime = player.getWorld().getGameTime();

      // 経過時間を計算
      long elapsedTime = endGameTime - playerScore.getStartGameTime();

      // デバッグ用の記述
      System.out.println("Game ended at time: " + endGameTime);
      System.out.println("Elapsed time in ticks: " + elapsedTime);
      System.out.println("Elapsed time in seconds: " + (elapsedTime / 20));

      // スコアを計算して設定
      int score;
      if (elapsedTime <= 100) {
          score = 100;
        } else if (elapsedTime <= 200) {
          score = 50;
        } else if (elapsedTime <= 400) {
          score = 30;
        } else if (elapsedTime <= 1200) {
          score = 10;
        } else {
          score = 0;
        }

        playerScore.setScore(score);

        //メッセージに表示（後ほどタイトル表示に）
        player.sendMessage("キツネ発見！ゲーム終了です");
        player.sendMessage("経過時間: " + elapsedTime / 20 + " 秒");
        player.sendMessage("得点は " + playerScore.getScore() + " 点！");

      // ゲーム用にスポーンしたエンティティを消す
      spawnEntityList.forEach(Entity::remove);
      spawnEntityList.clear();

      // スコアとゲーム時間をリセットする
      playerScore.resetScore();
    }
  }


  /**
   * コマンドが実行されたときに出現するエンティティを設定
   * 出現したエンティティ情報をListに保存
   *
   * @param player  コマンドを実行したプレイヤー
   */
  public void EntitiesSpawn(Player player) {
    entitySpawnCounts.clear();
    entitySpawnCounts.put(EntityType.PANDA, 20);
    entitySpawnCounts.put(EntityType.ARMADILLO, 20);
    entitySpawnCounts.put(EntityType.LLAMA, 30);
    entitySpawnCounts.put(EntityType.CAMEL, 20);
    entitySpawnCounts.put(EntityType.FOX, 1);

    for (Map.Entry<EntityType, Integer> entry : entitySpawnCounts.entrySet()) {
      EntityType entityType = entry.getKey();
      int count = entry.getValue();
      for (int i = 0; i < count; i++) {
        Location location = getEntitySpawnLocation(player);
        Entity entity = location.getWorld().spawnEntity(location,entityType);
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
