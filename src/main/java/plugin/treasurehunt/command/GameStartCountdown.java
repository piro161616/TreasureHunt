package plugin.treasurehunt.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameStartCountdown extends BukkitRunnable {

    private int nowTime;
    private final Player player;
    private final TreasureHuntCommand treasureHuntCommand;

    public GameStartCountdown(TreasureHuntCommand treasureHuntCommand, Player player) {
      this.treasureHuntCommand = treasureHuntCommand;
      this.nowTime = 10;  // タイトル表示５秒＋カウントダウン５秒
      this.player = player;
    }


    @Override
    public void run() {
      if (nowTime <= 0) {
        player.sendTitle(ChatColor.GOLD + "START!!", "", 0, TreasureHuntCommand.SECONDS_TO_TICKS, 0);
        cancel();
        treasureHuntCommand.gameStart(player);
        return;
      } else if (nowTime == 10) {
        player.sendTitle("キツネを探せ！",
            "1分以内にキツネを右クリックで捕まえて。",
            0, 5 * TreasureHuntCommand.SECONDS_TO_TICKS, TreasureHuntCommand.SECONDS_TO_TICKS);
      } else if(nowTime <= 5) {
        player.sendTitle(ChatColor.GOLD + "開始まであと" + nowTime + "秒",
            "ウサギを右クリックでボーナススコア！",
            0, TreasureHuntCommand.SECONDS_TO_TICKS,0);
      }
      nowTime--;
    }
}
