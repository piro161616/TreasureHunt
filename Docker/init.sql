-- init.sql

CREATE TABLE IF NOT EXISTS treasure_hunt (
    id int NOT NULL AUTO_INCREMENT,
    player_name varchar(100) DEFAULT NULL,
    score int DEFAULT NULL,
    registered_at datetime DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb3;