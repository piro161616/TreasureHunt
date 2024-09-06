package plugin.treasurehunt.data;

import java.time.Instant;
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
public class ExecutingPlayer {
  private String playerName;
  private int score;
  private Instant startTime;

  // コンストラクタで初期値を設定
  public ExecutingPlayer(String playerName, int score, Instant startTime) {
    this.playerName = playerName;
    this.score = score;
    this.startTime = startTime;
  }

  private static List<ExecutingPlayer> executingPlayerList = new ArrayList<>();

}
