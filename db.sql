-- --------------------------------------------------------
-- Хост:                         127.0.0.1
-- Версия сервера:               5.6.30-log - MySQL Community Server (GPL)
-- ОС Сервера:                   Win64
-- HeidiSQL Версия:              9.2.0.4947
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Дамп структуры базы данных schedule
CREATE DATABASE IF NOT EXISTS `schedule` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
USE `schedule`;


-- Дамп структуры для таблица schedule.hero
DROP TABLE IF EXISTS `hero`;
CREATE TABLE IF NOT EXISTS `hero` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.


-- Дамп структуры для таблица schedule.item
DROP TABLE IF EXISTS `item`;
CREATE TABLE IF NOT EXISTS `item` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.


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


-- Дамп структуры для таблица schedule.match_series
DROP TABLE IF EXISTS `match_series`;
CREATE TABLE IF NOT EXISTS `match_series` (
  `scheduled_game_id` int(11) NOT NULL,
  `match_id` bigint(20) unsigned NOT NULL,
  `game_number` tinyint(3) unsigned NOT NULL,
  `radiant_win` tinyint(1) DEFAULT NULL,
  `finished` tinyint(1) NOT NULL DEFAULT '0',
  `radiant_team` int(11) DEFAULT NULL,
  UNIQUE KEY `match_id` (`match_id`),
  KEY `FK_match_series_scheduled_games` (`scheduled_game_id`),
  CONSTRAINT `FK_match_series_scheduled_games` FOREIGN KEY (`scheduled_game_id`) REFERENCES `scheduled_games` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Экспортируемые данные не выделены.


-- Дамп структуры для таблица schedule.net_worth
DROP TABLE IF EXISTS `net_worth`;
CREATE TABLE IF NOT EXISTS `net_worth` (
  `match_id` bigint(20) unsigned NOT NULL,
  `net_worth` varchar(2000) COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Экспортируемые данные не выделены.


-- Дамп структуры для таблица schedule.scheduled_games
DROP TABLE IF EXISTS `scheduled_games`;
CREATE TABLE IF NOT EXISTS `scheduled_games` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `radiant` int(11) unsigned NOT NULL,
  `dire` int(11) unsigned NOT NULL,
  `series_type` tinyint(3) unsigned NOT NULL,
  `league_id` int(11) NOT NULL,
  `status` tinyint(3) NOT NULL DEFAULT '0',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK1_radiant_sched` (`radiant`),
  KEY `FK2_dire_sched` (`dire`),
  KEY `FK3_league_sched` (`league_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Экспортируемые данные не выделены.


-- Дамп структуры для таблица schedule.team
DROP TABLE IF EXISTS `team`;
CREATE TABLE IF NOT EXISTS `team` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(50) COLLATE utf8_bin NOT NULL,
  `tag` varchar(20) COLLATE utf8_bin NOT NULL,
  `logo` bigint(20) DEFAULT NULL,
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
