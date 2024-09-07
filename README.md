Javaコースミニゲーム開発編　宝探しゲーム「キツネを探せ」

■ゲーム概要
制限時間内にキツネを探して捕まえるゲーム。

ゲーム開始：/treasurehunt コマンドを入力で開始
制限時間：１分
５秒以内　１００点
１０秒以内　８０点
２０秒以内　５０点
１分以内　３０点

■ゲーム詳細
1./treasurehuntコマンドを入力することにより制限時間１分のゲーム開始

2.キツネを探して右クリックするとゲーム終了。ウサギを先に捕まえるとボーナスでスコアが２倍に

3.ゲームが終了し合計スコアが画面表示され、DBにプレイヤー名、スコア、日時が保存される

■ゲームスコアの表示
/treasurehunt listコマンドを入力すると、DBに保存されているスコア一覧が表示

■MySQLの設定
ユーザー名:（コードを確認してください）
パスワード: （コードを確認してください）
データベース名: treasure_hunt
URL: jdbc:mysql://localhost:3306/treasure_hunt

■対応バージョン
Minecraft：1.20.6
Spigot：1.20.6
※動作確認はWindowsのみ実施

■追記
ウサギをスペシャルエンティティとして登場させました。
しかしあまりにも素早いため、スポーンした場所に留まってもらう設定にしました。

<div id="top"></div>

## 使用技術一覧

<!-- シールド一覧 -->
<!-- 該当するプロジェクトの中から任意のものを選ぶ-->
<p style="display: inline">
  <!-- バックエンドの言語一覧 -->
  <img src="https://img.shields.io/badge/-Python-F2C63C.svg?logo=python&style=for-the-badge">
  <!-- ミドルウェア一覧 -->
  <img src="https://img.shields.io/badge/-MySQL-4479A1.svg?logo=mysql&style=for-the-badge&logoColor=white">
  <!-- インフラ一覧 -->
  <img src="https://img.shields.io/badge/-githubactions-FFFFFF.svg?logo=github-actions&style=for-the-badge">
</p>
