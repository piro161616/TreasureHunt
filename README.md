Javaコースミニゲーム開発編　宝探しゲーム「キツネを探せ」

<B>■ ゲーム概要</B>
制限時間内にキツネを探して捕まえるゲーム。<BR>

ゲーム開始：/treasurehunt コマンドを入力で開始<BR>
制限時間：１分<BR>
５秒以内　１００点<BR>
１０秒以内　８０点<BR>
２０秒以内　５０点<BR>
１分以内　３０点<BR>

<B>■ゲーム詳細</B>
1./treasurehuntコマンドを入力することにより制限時間１分のゲーム開始<BR>

2.キツネを探して右クリックするとゲーム終了。ウサギを先に捕まえるとボーナスでスコアが２倍に<BR>

3.ゲームが終了し合計スコアが画面表示され、DBにプレイヤー名、スコア、日時が保存される<BR>

<B>■ゲームスコアの表示</B><BR>
/treasurehunt listコマンドを入力すると、DBに保存されているスコア一覧が表示<BR>

<B>■ MySQLの設定</B><BR>
ユーザー名:（コードを確認してください）<BR>
パスワード: （コードを確認してください）<BR>
データベース名: treasure_hunt<BR>
URL: jdbc:mysql://localhost:3306/treasure_hunt<BR>

<B>■ 対応バージョン</B><BR>
Minecraft：1.20.6<BR>
Spigot：1.20.6<BR>
※動作確認はWindowsのみ実施<BR>

<B>■ 追記</B>
ウサギをスペシャルエンティティとして登場させました。<BR>
しかしあまりにも素早いため、スポーンした場所に留まってもらう設定にしました。<BR>

<B>■ 使用技術一覧</B>
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
