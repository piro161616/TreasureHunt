package plugin.treasurehunt.data;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * ゲームを実行する際のスコア情報を扱うオブジェクト
 * プレイヤー名、点数、日時の情報を持つ。
 */
@Getter
@Setter
public class PlayerScore {
  private String playerName;
  private int score;
  private long startGameTime;

  private static List<PlayerScore> playerScoreList = new ArrayList<>();

  public static void addPlayerScore(PlayerScore playerScore) {
    playerScoreList.add(playerScore);
    System.out.println("PlayerScore added: " + playerScore.getPlayerName() + ", score=" + playerScore.getScore()); // デバッグ用の記述
  }

  public static List<PlayerScore> selectList() {
    return new ArrayList<>(playerScoreList);
  }

  // ゲーム時間とスコアをリセット
  public void resetScore() {
    this.score = 0;
//    this.startGameTime = 0;
    System.out.println("resetScore called: score=" + this.score + ",　startGameTime=" + this.startGameTime); // デバッグ用の記述
  }

}
