-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Client: localhost
-- Généré le: Mar 14 Juillet 2015 à 16:33
-- Version du serveur: 5.5.24-log
-- Version de PHP: 5.4.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données: `cosync`
--

-- --------------------------------------------------------

--
-- Structure de la table `systems`
--

CREATE TABLE IF NOT EXISTS `systems` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(45) NOT NULL,
  `user_id` int(11) NOT NULL,
  `last_ip` varchar(45) DEFAULT NULL,
  `is_master` tinyint(1) NOT NULL,
  `is_register` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`,`user_id`),
  KEY `fk_systems_users_idx` (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

--
-- Contenu de la table `systems`
--

INSERT INTO `systems` (`id`, `key`, `user_id`, `last_ip`, `is_master`, `is_register`) VALUES
(1, '1', 1, '127.0.0.1', 1, 0),
(1, 'ABC', 9, '255.255.255.255', 1, 0),
(3, 'ABC', 9, '255.255.255.255', 1, 0),
(4, 'ABD', 9, '1.1.1.1', 0, 1),
(5, 'BLA', 1, '1.1.1.1', 0, 1);

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=11 ;

--
-- Contenu de la table `user`
--

INSERT INTO `user` (`id`, `name`, `password`) VALUES
(1, 'elie', 'password'),
(9, 'a', 'a'),
(10, 'z', '0');

--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `systems`
--
ALTER TABLE `systems`
  ADD CONSTRAINT `fk_systems_users` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;