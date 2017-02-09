CREATE DATABASE  IF NOT EXISTS `conversaciones` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
USE `conversaciones`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win32 (x86)
--
-- Host: bd.qa11.ncubo.com    Database: conversaciones
-- ------------------------------------------------------
-- Server version	5.5.44-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bitacora_de_conversaciones`
--

DROP TABLE IF EXISTS `bitacora_de_conversaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bitacora_de_conversaciones` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sesion` varchar(100) CHARACTER SET latin1 NOT NULL,
  `id_usuario` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `fecha` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `conversacion` longblob NOT NULL,
  `haSidoVerificado` bit(1) DEFAULT b'0',
  `idCliente` varchar(45) CHARACTER SET latin1 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=hp8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detalle_de_la_conversacion`
--

DROP TABLE IF EXISTS `detalle_de_la_conversacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `detalle_de_la_conversacion` (
  `fechaHora` timestamp NULL DEFAULT NULL,
  `usuario` varchar(45) DEFAULT NULL,
  `frase` varchar(500) DEFAULT NULL,
  `fraseId` int(11) DEFAULT NULL,
  `intencion` varchar(45) DEFAULT NULL,
  `entidad` varchar(45) DEFAULT NULL,
  `idConversacion` int(11) DEFAULT NULL,
  `idCliente` varchar(45) CHARACTER SET latin1 NOT NULL,
  UNIQUE KEY `FechaYFrase` (`fechaHora`,`frase`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `frases`
--

DROP TABLE IF EXISTS `frases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `frases` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idfrase` varchar(32) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `frase` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'conversaciones'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-02-01 12:13:53
