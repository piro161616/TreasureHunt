<B>Javaコースミニゲーム開発編　宝探しゲーム「キツネを探せ」</B>

<B>■ ゲーム概要</B>
制限時間内にキツネを探して捕まえるゲーム。<BR>

ゲーム開始：/treasurehunt コマンドを入力で開始<BR>
制限時間：１分<BR>
５秒以内　１００点<BR>
１０秒以内　８０点<BR>
２０秒以内　５０点<BR>
１分以内　３０点<BR>
ウサギボーナス スコア×２倍<BR>

<B>■ゲーム詳細</B><BR>
1./treasurehuntコマンドを入力すると、制限時間１分のゲームが開始<BR>

2.キツネを探して右クリックするとゲーム終了。ウサギを先に捕まえるとボーナスでスコアが２倍に<BR>

3.ゲームが終了し合計スコアが画面表示され、DBにプレイヤー名、スコア、日時が保存される<BR>

<B>■ゲームスコアの表示</B><BR>
/treasurehunt listコマンドを入力すると、DBに保存されているスコア一覧が表示<BR>

<B>■ MySQLの設定</B><BR>
ユーザー名:（コードを確認してください）<BR>
パスワード: （コードを確認してください）<BR>
データベース名: treasure_hunt<BR>
URL: jdbc:mysql://localhost:3306/treasure_hunt<BR>
CREATE TABLE文：<BR>
  Table: treasure_hunt<BR>
Create Table: CREATE TABLE `treasure_hunt` (<BR>
  `id` int NOT NULL AUTO_INCREMENT,<BR>
  `player_name` varchar(100) DEFAULT NULL,<BR>
  `score` int DEFAULT NULL,<BR>
  `registered_at` datetime DEFAULT NULL,<BR>
  PRIMARY KEY (`id`)<BR>
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb3<BR>

<B>■コンテナの起動</B></BR>
このゲームのスコアデータ保存にDockerを使用しています。<BR>
プロジェクトのルートディレクトリに移動し、以下のコマンドでコンテナを起動してください。<BR>
docker-compose up -d<BR>

<B>■ 対応バージョン</B><BR>
Minecraft：1.20.6<BR>
Spigot：1.20.6<BR>
※動作確認はWindowsのみ実施<BR>

<B>■プレイ動画とURL</B><BR>
![Description of GIF](https://private-user-images.githubusercontent.com/173275523/366932321-0cc0d00d-ce06-452e-ad89-5856f2531ccd.gif?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MjYxNTMwMDcsIm5iZiI6MTcyNjE1MjcwNywicGF0aCI6Ii8xNzMyNzU1MjMvMzY2OTMyMzIxLTBjYzBkMDBkLWNlMDYtNDUyZS1hZDg5LTU4NTZmMjUzMWNjZC5naWY_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwOTEyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDkxMlQxNDUxNDdaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT03ZjczN2I1ODkxZjRmZDEyNWYyYTJiYzQxODAyNzIzNjBmMGMxZTk0NjA5ZGUzZWJiNDhhYzg2YmY4YzlmYjQ2JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.LvAf-oCsdDqd7KwxwXd92Y1se3p9WglLQFpLoi1AFYs)<BR>

https://github.com/user-attachments/assets/59e2a20d-611d-44cb-aa1d-26cdc5a21042<BR>

<B>■ 追記</B><BR>
ウサギをスペシャルエンティティとして登場させました。<BR>
しかしあまりにも素早いため、スポーンした場所に留まってもらう設定にしました。<BR>

<B>■ 使用技術一覧</B>
<!-- シールド一覧 -->
<!-- 該当するプロジェクトの中から任意のものを選ぶ-->
<p style="display: inline">
  <!-- バックエンドの言語一覧 -->
  <img src="https://img.shields.io/badge/-Java-F2C63C.svg?logo=java&style=for-the-badge">
  <!-- ミドルウェア一覧 -->
  <img src="https://img.shields.io/badge/-MySQL-4479A1.svg?logo=mysql&style=for-the-badge&logoColor=white">
  <!-- インフラ一覧 -->
  <img src="https://img.shields.io/badge/-githubactions-FFFFFF.svg?logo=github-actions&style=for-the-badge">
</p>
