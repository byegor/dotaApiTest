-- --------------------------------------------------------
-- Хост:                         127.0.0.1
-- Версия сервера:               5.6.24-log - MySQL Community Server (GPL)
-- ОС Сервера:                   Win64
-- HeidiSQL Версия:              9.2.0.4947
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Дамп структуры базы данных schedule
DROP DATABASE IF EXISTS `schedule`;
CREATE DATABASE IF NOT EXISTS `schedule` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
USE `schedule`;


-- Дамп структуры для таблица schedule.league
DROP TABLE IF EXISTS `league`;
CREATE TABLE IF NOT EXISTS `league` (
  `id` int(11) NOT NULL,
  `name` varchar(250) COLLATE utf8_bin NOT NULL,
  `description` varchar(400) COLLATE utf8_bin DEFAULT NULL,
  `url` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Экспортируемые данные не выделены.


-- Дамп структуры для таблица schedule.live_games
DROP TABLE IF EXISTS `live_games`;
CREATE TABLE IF NOT EXISTS `live_games` (
  `match_id` bigint(20) unsigned NOT NULL,
  `radiant` int(10) unsigned NOT NULL,
  `dire` int(10) unsigned NOT NULL,
  `league_id` int(11) NOT NULL,
  `series_type` tinyint(2) NOT NULL,
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `radiant_win` tinyint(2) unsigned NOT NULL,
  `game` tinyint(2) unsigned NOT NULL,
  PRIMARY KEY (`match_id`),
  KEY `FK3_league` (`league_id`),
  KEY `FK1_team1` (`radiant`),
  KEY `FK2_team2` (`dire`),
  CONSTRAINT `FK1_team1` FOREIGN KEY (`radiant`) REFERENCES `team` (`id`),
  CONSTRAINT `FK2_team2` FOREIGN KEY (`dire`) REFERENCES `team` (`id`),
  CONSTRAINT `FK3_league` FOREIGN KEY (`league_id`) REFERENCES `league` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Экспортируемые данные не выделены.


-- Дамп структуры для таблица schedule.match_series
DROP TABLE IF EXISTS `match_series`;
CREATE TABLE IF NOT EXISTS `match_series` (
  `match_id` bigint(20) unsigned NOT NULL,
  `radiant` int(10) unsigned NOT NULL,
  `dire` int(10) unsigned NOT NULL,
  `league_id` int(10) NOT NULL,
  `radiant_win` tinyint(1) unsigned NOT NULL,
  `parent` bigint(20) unsigned DEFAULT NULL,
  `series_type` tinyint(3) unsigned NOT NULL,
  `radiant_score` tinyint(3) unsigned NOT NULL,
  `dire_score` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`match_id`),
  KEY `FK_radiant_team_match` (`radiant`),
  KEY `FK_dire_team_match` (`dire`),
  KEY `FK_league_match` (`league_id`),
  CONSTRAINT `FK_dire_team_match` FOREIGN KEY (`dire`) REFERENCES `team` (`id`),
  CONSTRAINT `FK_league_match` FOREIGN KEY (`league_id`) REFERENCES `league` (`id`),
  CONSTRAINT `FK_radiant_team_match` FOREIGN KEY (`radiant`) REFERENCES `team` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Экспортируемые данные не выделены.


-- Дамп структуры для таблица schedule.picks
DROP TABLE IF EXISTS `picks`;
CREATE TABLE IF NOT EXISTS `picks` (
  `match_id` bigint(20) unsigned NOT NULL,
  `hero_id` smallint(5) unsigned NOT NULL,
  `radiant` tinyint(1) NOT NULL,
  `pick` tinyint(1) NOT NULL,
  PRIMARY KEY (`match_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Экспортируемые данные не выделены.


-- Дамп структуры для таблица schedule.scheduled_games
DROP TABLE IF EXISTS `scheduled_games`;
CREATE TABLE IF NOT EXISTS `scheduled_games` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `match_id` bigint(20) unsigned DEFAULT NULL,
  `radiant` int(11) unsigned NOT NULL,
  `dire` int(11) unsigned NOT NULL,
  `league_id` int(11) NOT NULL,
  `status` tinyint(3) NOT NULL DEFAULT '0',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `radiant_score` tinyint(3) NOT NULL DEFAULT '0',
  `dire_score` tinyint(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FK1_radiant_sched` (`radiant`),
  KEY `FK2_dire_sched` (`dire`),
  KEY `FK3_league_sched` (`league_id`),
  KEY `match_id` (`match_id`),
  CONSTRAINT `FK1_radiant_sched` FOREIGN KEY (`radiant`) REFERENCES `team` (`id`),
  CONSTRAINT `FK2_dire_sched` FOREIGN KEY (`dire`) REFERENCES `team` (`id`),
  CONSTRAINT `FK3_league_sched` FOREIGN KEY (`league_id`) REFERENCES `league` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Экспортируемые данные не выделены.


-- Дамп структуры для таблица schedule.team
DROP TABLE IF EXISTS `team`;
CREATE TABLE IF NOT EXISTS `team` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(50) COLLATE utf8_bin NOT NULL,
  `tag` varchar(20) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Экспортируемые данные не выделены.


-- Дамп структуры для таблица schedule.update_task
DROP TABLE IF EXISTS `update_task`;
CREATE TABLE IF NOT EXISTS `update_task` (
  `id` bigint(20) NOT NULL,
  `classname` varchar(150) COLLATE utf8_bin NOT NULL,
  `result` tinyint(2) NOT NULL DEFAULT '0',
  KEY `id_classname_select` (`id`,`classname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Экспортируемые данные не выделены.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
