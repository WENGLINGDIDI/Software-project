-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: leasingdb
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `book`
--

DROP TABLE IF EXISTS `book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book` (
  `id` int NOT NULL AUTO_INCREMENT,
  `end_time` datetime(6) DEFAULT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  `status` int DEFAULT NULL,
  `total_cost` double DEFAULT NULL,
  `hire_id` int NOT NULL,
  `scooter_id` int NOT NULL,
  `user_id` int NOT NULL,
  `payed` int DEFAULT NULL,
  `extend_time` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb0jjs4o9994thg7c2214wb6dv` (`hire_id`),
  KEY `FK6dq1yhrwwpsmga0srr1b9n1q1` (`scooter_id`),
  KEY `FK1wxwagv6cm3vjrxqhmv884hir` (`user_id`),
  CONSTRAINT `FK1wxwagv6cm3vjrxqhmv884hir` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK6dq1yhrwwpsmga0srr1b9n1q1` FOREIGN KEY (`scooter_id`) REFERENCES `scooter` (`id`),
  CONSTRAINT `FKb0jjs4o9994thg7c2214wb6dv` FOREIGN KEY (`hire_id`) REFERENCES `hire_option` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book`
--

LOCK TABLES `book` WRITE;
/*!40000 ALTER TABLE `book` DISABLE KEYS */;
INSERT INTO `book` VALUES (1,'2025-03-13 14:04:41.605087','2025-03-13 03:04:41.605087',3,60,1,1,1,1,10),(2,'2025-03-14 04:04:41.647095','2025-03-14 03:04:41.647095',3,10,1,2,4,1,0),(3,'2025-03-16 03:04:41.657149','2025-03-15 03:04:41.657149',3,100,3,3,6,1,0),(4,'2025-03-18 07:48:59.018382','2025-03-18 06:48:59.018382',3,10,1,1,1,1,0),(5,'2025-03-19 07:48:59.074385','2025-03-19 06:48:59.074385',3,10,1,2,4,1,0),(6,'2025-03-21 06:48:59.087385','2025-03-20 06:48:59.087385',3,100,3,3,6,1,0),(16,'2025-04-13 10:00:55.978551','2025-04-12 19:04:41.605087',3,80,3,3,1,0,0),(17,'2025-04-16 05:04:41.605087','2025-04-14 19:04:41.605087',3,150,3,1,4,0,10),(18,'2025-04-18 05:57:01.654121','2025-04-18 04:57:01.654121',3,8,1,1,1,0,0),(19,'2025-04-19 05:57:01.674128','2025-04-19 04:57:01.674128',3,10,1,2,4,0,0),(20,'2025-04-21 04:57:01.689124','2025-04-20 04:57:01.689124',3,100,3,3,6,0,0),(21,'2025-04-18 05:58:33.309124','2025-04-18 04:58:33.309124',3,8,1,1,1,0,0),(22,'2025-04-19 05:58:33.325121','2025-04-19 04:58:33.325121',3,10,1,2,4,0,0),(23,'2025-04-21 04:58:33.337122','2025-04-20 04:58:33.337122',3,100,3,3,10,1,5),(24,'2025-04-20 08:49:49.929222','2025-04-20 07:49:49.929222',3,10,1,2,10,1,0),(26,'2025-05-04 07:52:38.301029','2025-04-22 06:04:23.966821',3,10,1,3,10,1,0),(28,'2025-05-04 07:52:50.738164','2025-04-26 01:00:00.000000',3,100,3,2,10,1,0),(29,'2025-04-29 08:36:20.125782','2025-04-27 01:00:00.000000',3,100,3,19,10,1,0),(30,'2025-04-28 10:18:21.180231','2025-04-28 09:18:21.180231',1,3,1,17,15,1,0),(31,'2025-04-29 13:18:21.223229','2025-04-29 09:18:21.223229',1,10,3,18,16,1,0),(32,'2025-05-01 09:18:21.232232','2025-04-30 09:18:21.232232',1,30,5,19,17,1,0),(33,'2025-05-08 09:18:21.241224','2025-05-01 09:18:21.241224',1,80,6,16,1,1,0),(34,'2025-04-28 13:05:41.503576','2025-04-28 12:05:41.503576',3,3,1,16,26,0,0),(35,'2025-04-28 12:05:53.369188','2025-04-28 11:05:53.369188',3,3,1,4,13,0,0),(36,'2025-04-28 11:06:04.387905','2025-04-28 10:06:04.387905',3,3,1,13,20,0,0),(37,'2025-04-28 10:06:16.256189','2025-04-28 09:06:16.256189',3,3,1,15,22,0,0),(38,'2025-04-28 09:06:27.180001','2025-04-28 08:06:27.180001',3,3,1,2,15,0,0),(39,'2025-04-28 16:07:56.106768','2025-04-28 12:07:56.106768',3,10,3,14,3,0,0),(40,'2025-04-28 15:08:07.480978','2025-04-28 11:08:07.480978',3,10,3,14,27,0,0),(41,'2025-05-06 14:08:19.049000','2025-04-28 10:08:19.049501',3,10,3,5,4,0,0),(42,'2025-04-29 12:09:09.419416','2025-04-28 12:09:09.419416',3,30,5,1,30,0,0),(43,'2025-04-29 11:09:09.491957','2025-04-28 11:09:09.491957',3,30,5,1,30,0,0),(44,'2025-04-29 10:09:09.506971','2025-04-28 10:09:09.506971',3,30,5,1,17,0,0),(45,'2025-04-29 09:09:09.521968','2025-04-28 09:09:09.521968',3,30,5,1,2,0,0),(46,'2025-05-05 12:09:37.499498','2025-04-28 12:09:37.499498',3,100,6,30,9,0,0),(47,'2025-05-05 11:09:37.572497','2025-04-28 11:09:37.572497',3,100,6,30,21,0,0),(48,'2025-04-28 13:13:52.303577','2025-04-28 12:13:52.303577',3,3,1,19,21,0,0),(49,'2025-04-28 12:13:52.398573','2025-04-28 11:13:52.398573',3,3,1,36,8,0,0),(50,'2025-04-28 11:13:52.424575','2025-04-28 10:13:52.424575',3,3,1,35,4,0,0),(51,'2025-04-28 10:13:52.447574','2025-04-28 09:13:52.447574',3,3,1,38,8,0,0),(52,'2025-04-28 09:13:52.471573','2025-04-28 08:13:52.471573',3,3,1,38,15,0,0),(53,'2025-04-28 08:13:52.490578','2025-04-28 07:13:52.490578',3,3,1,38,32,0,0),(54,'2025-04-28 07:13:52.507586','2025-04-28 06:13:52.507586',3,3,1,31,14,0,0),(55,'2025-04-28 06:13:52.529575','2025-04-28 05:13:52.529575',3,3,1,34,27,0,0),(56,'2025-04-28 05:13:52.556581','2025-04-28 04:13:52.556581',3,3,1,31,29,0,0),(57,'2025-04-28 04:13:52.576577','2025-04-28 03:13:52.576577',3,3,1,19,9,0,0),(58,'2025-04-29 20:00:00.000000','2025-04-29 16:00:00.000000',3,10,3,32,21,0,0),(59,'2025-04-30 17:00:00.000000','2025-04-29 17:00:00.000000',3,30,5,46,21,0,0),(75,'2025-05-04 10:54:35.813348','2025-05-04 10:00:00.000000',3,18,1,45,10,1,3),(76,'2025-05-04 10:57:17.053384','2025-05-04 10:00:00.000000',3,18,1,39,10,1,3),(77,'2025-05-04 02:00:00.000000','2025-05-04 01:00:00.000000',3,3,1,1,10,1,0),(78,'2025-05-04 11:05:06.491216','2025-05-04 11:00:00.000000',3,18,1,5,10,1,3),(79,'2025-05-07 17:00:00.000000','2025-05-07 13:00:00.000000',3,3,1,45,10,0,0),(80,'2025-05-07 09:03:09.863513','2025-05-07 09:00:00.000000',3,8,1,45,10,1,1),(81,'2025-05-07 02:00:00.000000','2025-05-07 01:00:00.000000',3,3,1,45,10,1,0),(82,'2025-05-07 10:08:07.628681','2025-05-07 09:08:07.628681',1,2.43,1,1,1,NULL,0),(83,'2025-05-08 13:08:07.670682','2025-05-08 09:08:07.670682',1,8.5,3,2,2,NULL,0),(84,'2025-05-10 09:08:07.680293','2025-05-09 09:08:07.680293',1,30,5,3,3,NULL,0),(85,'2025-05-17 09:08:07.688282','2025-05-10 09:08:07.688282',1,80,6,4,4,NULL,0),(86,'2025-05-06 16:00:00.605087','2025-05-06 16:00:00.605087',3,30,5,14,10,1,0),(87,'2025-05-06 16:00:00.605087','2025-05-06 16:00:00.605087',3,100,6,5,10,1,0),(88,'2025-05-06 16:00:00.605087','2025-05-06 16:00:00.605087',3,100,6,39,10,1,0),(89,'2025-05-06 16:00:00.605087','2025-05-06 16:00:00.605087',3,100,6,39,10,1,0),(90,'2025-05-07 16:00:00.605087','2025-05-06 16:00:00.605087',3,30,5,39,10,1,0),(91,'2025-05-13 16:00:00.605087','2025-05-06 16:00:00.605087',3,100,6,3,10,1,0),(92,'2025-05-07 02:00:00.605087','2025-05-07 01:00:00.605087',3,3,1,17,10,1,0),(93,'2025-05-14 01:00:00.605087','2025-05-07 01:00:00.605087',2,100,6,35,10,0,0),(94,'2025-05-09 01:00:00.605087','2025-05-08 01:00:00.605087',2,30,5,13,10,0,0),(95,'2025-05-07 11:21:35.700266','2025-05-07 01:00:00.605087',3,345,6,46,10,1,49),(96,'2025-05-14 02:00:00.605087','2025-05-07 01:00:00.605087',2,105,6,38,10,0,1),(97,'2025-05-07 11:27:25.080815','2025-05-07 01:00:00.605087',3,100,6,38,10,1,0),(98,'2025-05-08 02:00:00.605087','2025-05-08 01:00:00.605087',3,3,1,38,10,0,0),(99,'2025-05-09 05:00:00.605087','2025-05-09 01:00:00.605087',3,10,3,13,67,1,0),(100,'2025-05-15 01:00:00.605087','2025-05-08 01:00:00.605087',2,100,6,31,67,0,0),(101,'2025-05-15 02:00:00.605087','2025-05-08 01:00:00.605087',2,105,6,36,68,0,1),(102,'2025-05-15 01:00:00.605087','2025-05-08 01:00:00.605087',2,100,6,46,69,0,0),(103,'2025-05-14 17:00:00.000000','2025-05-14 16:00:00.000000',1,3,1,13,21,0,0),(104,'2025-05-14 20:00:00.000000','2025-05-14 16:00:00.000000',1,10,3,19,21,0,0),(105,'2025-05-08 13:43:35.567979','2025-05-08 12:43:35.567979',2,3,1,1,10,0,0);
/*!40000 ALTER TABLE `book` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
  `id` int NOT NULL AUTO_INCREMENT,
  `message` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `priority` int DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `book_id` int NOT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `result` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgclyi456gw0lcd6xcfj2l7r6s` (`book_id`),
  CONSTRAINT `FKgclyi456gw0lcd6xcfj2l7r6s` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES (1,'This is a test feedback. The system is operating normally.',3,'已处理',1,'2025-04-02 15:19:38.000000','\n'),(2,'The brake is damaged.',1,'已处理',2,'2025-04-02 18:22:41.000000','Fix the problem of the brakes'),(3,'The tyre is leaking air.',1,'已处理',6,'2025-04-13 09:34:45.278114','sdf'),(4,'The seat is uncomfortable.',1,'未处理',3,'2025-04-16 21:40:28.000000',NULL),(5,'The tyre is leaking air.',1,'已处理',4,'2025-04-16 21:40:50.000000','OK'),(6,'This is a test feedback. The system is operating normally',1,'已处理',1,'2025-04-29 11:44:57.000000','ok'),(7,'This is a test feedback. The system is operating normally',1,'已处理',1,'2025-04-24 11:44:59.000000',NULL),(8,'123',1,'未处理',23,'2025-05-07 08:56:33.161450',NULL),(9,'24',1,'已处理',78,'2025-05-07 09:46:50.159427','error fixed successfully'),(10,'这是一个测试反馈，系统运行正常。',1,'未处理',1,NULL,NULL);
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hire_option`
--

DROP TABLE IF EXISTS `hire_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hire_option` (
  `id` int NOT NULL AUTO_INCREMENT,
  `hours` int DEFAULT NULL,
  `money` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hire_option`
--

LOCK TABLES `hire_option` WRITE;
/*!40000 ALTER TABLE `hire_option` DISABLE KEYS */;
INSERT INTO `hire_option` VALUES (1,1,3),(3,4,10),(5,24,30),(6,168,100),(17,1,10),(18,4,30),(19,24,100),(20,168,200);
/*!40000 ALTER TABLE `hire_option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scooter`
--

DROP TABLE IF EXISTS `scooter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scooter` (
  `id` int NOT NULL AUTO_INCREMENT,
  `config` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `power` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  `station_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKj3t235sktl3h6tsfmgo6omafq` (`station_id`),
  CONSTRAINT `FKj3t235sktl3h6tsfmgo6omafq` FOREIGN KEY (`station_id`) REFERENCES `station` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scooter`
--

LOCK TABLES `scooter` WRITE;
/*!40000 ALTER TABLE `scooter` DISABLE KEYS */;
INSERT INTO `scooter` VALUES (1,'Speed: 30 km/h, Includes helmet and phone holder',30.666182541698344,104.06927120541933,66,0,2),(2,'Speed: 30 km/h, Includes helmet and phone holder',30.656472877030765,104.0742647406009,86,0,4),(3,'Speed: 30 km/h, Includes helmet and phone holder',30.651060567669667,104.05666759366696,15,0,3),(4,'Speed: 30 km/h, Includes helmet and phone holder',30.650853857946778,104.07125841944789,69,0,3),(5,'Speed: 30 km/h, Includes helmet and phone holder',30.664027168694343,104.07118129411072,26,1,2),(12,'speed:30km/h',30.656182541698342,104.06927120531932,80,2,2),(13,'Speed: 30 km/h, Includes helmet and phone holder',30.65803328743451,104.07488425334873,50,0,1),(14,'Speed: 30 km/h, Includes helmet and phone holder',30.647945575687288,104.06487341793812,99,1,3),(15,'Speed: 30 km/h, Includes helmet and phone holder',30.66051781955808,104.05619560058577,68,3,4),(16,'Speed: 30 km/h, Includes helmet and phone holder',30.64758761173608,104.06202857740602,34,4,2),(17,'Speed: 30 km/h, Includes helmet and phone holder',30.648063610053608,104.0707801962473,37,1,3),(18,'speed:30km/h',30.656182541698342,104.06927120531932,80,2,4),(19,'Speed: 30 km/h, Includes helmet and phone holder',30.6586,104.0757,78,1,1),(30,'Speed: 30 km/h, Includes helmet and phone holder',30.6678,104.0873,78,0,2),(31,'Speed: 30 km/h, Includes helmet and phone holder',30.662352512008642,104.08174261966224,66,1,4),(32,'Speed: 30 km/h, Includes helmet and phone holder',30.662067740915735,104.04480238283978,60,1,3),(34,'Speed: 30 km/h, Includes helmet and phone holder',30.634814609918877,104.07556545598561,94,1,1),(35,'Speed: 30 km/h, Includes helmet and phone holder',30.68033571791218,104.04300048544778,42,0,3),(36,'Speed: 30 km/h, Includes helmet and phone holder',30.64481249296901,104.04889406241391,17,1,3),(38,'Speed: 30 km/h, Includes helmet and phone holder',30.680463494766606,104.05527551783074,21,1,3),(39,'Speed: 30 km/h, Includes helmet and phone holder',30.650747959087475,104.08828192429927,47,0,2),(45,'Speed: 30km/h, Includes helmet and phone holder',30.7048,104.0731,79,1,1),(46,'Speed: 30km/h, Includes helmet and phone holder',30.6586,104.0757,78,1,4),(47,'Speed: 30km/h, Includes helmet and phone holder',NULL,NULL,80,0,4),(48,'Speed: 30 km/h, Includes helmet and phone holder',30.67954757546507,104.08741484146763,29,0,3),(51,'Speed: 30 km/h, Includes helmet and phone holder',30.667610252074635,104.07012816801179,30,0,3),(52,'Speed: 30 km/h, Includes helmet and phone holder',30.666942487862766,104.0559777768913,98,0,4),(53,'Speed: 30 km/h, Includes helmet and phone holder',30.658485839325404,104.0531503539891,13,0,2),(55,'Speed: 30 km/h, Includes helmet and phone holder',30.665039215824574,104.0819519959534,90,0,3);
/*!40000 ALTER TABLE `scooter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `station`
--

DROP TABLE IF EXISTS `station`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `station` (
  `id` int NOT NULL AUTO_INCREMENT,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `station`
--

LOCK TABLES `station` WRITE;
/*!40000 ALTER TABLE `station` DISABLE KEYS */;
INSERT INTO `station` VALUES (1,30.6586,104.0757,'ChunxiRoadStation'),(2,30.6678,104.0873,'ShuyuanRoadStation'),(3,30.7048,104.0731,'ShuangshuiRoadStation'),(4,30.6383,104.0526,'YulingRoadStation');
/*!40000 ALTER TABLE `station` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `card` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `discount` double NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `role` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `discount_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'6222021234567890','2025-03-13 02:40:12.323190',0.81,'1550320491@qq.com','name1','$2a$10$rvsli65r/GG/nr34ZKQQguwEnm2XEp6LAETmjknq03U4..DOVUwRC','user','2025-04-30 06:32:09.529669'),(2,'7222021234567890','2025-03-13 02:40:12.386188',0.85,'email2@example.com','name2','$2a$10$1T8rqDPncGBfobnwAvyVO.d8RfP2baT.SNkQaPPCK2s9pYKwSI42m','user','2025-04-29 13:25:51.179854'),(3,NULL,'2025-03-13 02:40:12.391188',1,'email3@example.com','name3','$2a$10$1TboSLgqTi5vyJGm6NLvLuLrgsMowEaLo.R.y4sT9plfrqbP4Vy2C','user',NULL),(4,NULL,'2025-03-13 02:40:12.396195',0.8,'email4@example.com','name4','$2a$10$3xRNt2RDdRbljyyTFpr29OMB/BLxLUptCtKDmUu4zfNOyUfmZPHfG','user','2025-04-29 13:25:55.402528'),(5,NULL,'2025-03-13 02:40:12.400194',1,'email5@example.com','name5','$2a$10$xJbqfWY0EQR5kS/29xSGqeqbQM9heE/.4jQFp/4cYz6qGj6zTSvEC','user',NULL),(6,NULL,'2025-03-13 02:40:12.405181',0.85,'email6@example.com','name6','$2a$10$3F1oHpyO7N64qXeIv9YNA.V4L7Y1dRXBCfiacPy4PIsAMU9Zpmy/G','user','2025-04-29 13:25:59.511716'),(7,NULL,'2025-03-13 02:40:12.409192',1,'email7@example.com','name7','$2a$10$afYpUJVWS.BqAdRzh7wvxOxSIbF0.uEUsi9/8v7ASF5K6Kxpvnata','user',NULL),(8,NULL,'2025-03-13 02:40:12.413188',1,'email8@example.com','name8','$2a$10$EsPdwfhIbNBUo66KhRhzm./GzfLbVCoU3gWtqZC.t2pUK2sd/tG0i','user',NULL),(9,NULL,'2025-03-13 02:40:12.418181',1,'email9@example.com','name9','$2a$10$33x1XRD2XoSMS2dsvvD/v.Pc3oqZPE/4Tz.m6N1F4uisfbC2iv5MG','user',NULL),(10,'6222021234567890123','2025-03-13 02:40:12.422188',1,'1550320492@qq.com','testUser','$2a$10$cJJkdYzIoJV9dvuVLsDESeYHJHYery/Si02R.VaE/s6AFIFUytjJe','user',NULL),(11,NULL,'2025-03-13 02:40:12.426188',1,'email11@example.com','name11','$2a$10$FUzVX2FFzJLyAoMvEoWDmulNirOVOoNAoSvdZIeDL6jwjvBY1.H76','user',NULL),(12,NULL,'2025-03-13 02:40:12.430181',1,'email12@example.com','name12','$2a$10$bYH5D.4tugrzDXfl59SHR.FT1x6uZov82q3DnzypDWEV3Ip1XjXXm','user',NULL),(13,NULL,'2025-03-13 02:40:12.434190',1,'email13@example.com','name13','$2a$10$LcN2JS3xSZJuX3dmMCfEaO2yLLWl.3MVJHL09/ovwP5fG9UiUHQSK','user',NULL),(14,NULL,'2025-03-13 02:40:12.438188',1,'email14@example.com','name14','$2a$10$3K7J7BskqLuS.XBrsXjY6.5iBGUgAir7f..XIPhCgY9DiyDJM48WW','user',NULL),(15,NULL,'2025-03-13 02:40:12.442186',1,'email15@example.com','name15','$2a$10$qF/yZ769unUGKXWBn0HfCu8UUAN98XyA7zf8bw/O79c32HPyHqzhu','user',NULL),(16,NULL,'2025-03-13 02:40:12.446189',1,'email16@example.com','name16','$2a$10$KMBL/k10oluhsKjTxrFM9OQ0km0UUB5smQR2x6thG9YTs24nI/dIu','user',NULL),(17,NULL,'2025-03-13 02:40:12.451189',1,'email17@example.com','name17','$2a$10$M/ol.0whuR8Xmt4ZPqwjxODXQhRmoKeS1ZTT1Jow8ZSQFUiRmbh2u','user',NULL),(18,NULL,'2025-03-13 02:40:12.455188',1,'email18@example.com','name18','$2a$10$WAZIUyCl0g7oe02ZFPO1kemHoTHYWJa.eGoUoNUfd3bS5cmQlc5s.','user',NULL),(19,NULL,'2025-03-13 02:40:12.459188',1,'email19@example.com','name19','$2a$10$UsJCt0dReTyz2D2FX13QzeOejcNjcJkTuIdMK9iZvQnEcyWNifwnS','user',NULL),(20,NULL,'2025-03-13 02:40:12.463189',1,'email20@example.com','name20','$2a$10$txaN1a3mhV2QsUiEbGB8/uST/79MJgh6tW7wKWJP75nL0B3zx38fu','user',NULL),(21,NULL,'2025-03-17 07:51:29.819477',1,'123456@qq.com','manager1','$2a$10$TsxmFvLJfr4d4W.f8O5wTeFwVGe1Y43uyz/N3OVMG.RPwUlKfGOUi','admin',NULL),(22,NULL,'2025-04-06 10:04:55.469573',1,'111111@qq.com','123','$2a$10$wO9E34RbquZyYORrIa7kUOb2Emt/YlpYuoEIfmGYP5E7qzuwQ8aP2','user',NULL),(23,NULL,'2025-04-15 08:39:55.177858',1,'ccgshabi@qq.com','ccgshabi','$2a$10$PMQKyUmf26NLI.n7Vqg74eKwrYRe7sYhHkQwKEs6W.b6IKUylDieK','user',NULL),(24,NULL,'2025-04-18 04:57:01.865124',1,'1email1@example.com','name1','$2a$10$npIgvHZcOdGSv0MC8LEWN.AqBF00Sm9UoKgLgVHLy8dk5lpPNGCa6','user',NULL),(25,NULL,'2025-04-18 04:57:01.871129',1,'1email2@example.com','name2','$2a$10$cPteBNC0d0HpzLkNjj8ye.Bd9CSdxqTa2QBvhdG0AEkiSRamGM5hu','user',NULL),(26,NULL,'2025-04-18 04:57:01.876128',1,'1email3@example.com','name3','$2a$10$uVLnLA9FddUOc1Un4F6s3OwOOfUs78kgrzPRalQyOgjtveWNLBw3O','user',NULL),(27,NULL,'2025-04-18 04:57:01.880128',1,'1email4@example.com','name4','$2a$10$9zF1QGPnmqiDx8esIb.XWuyhw6KtySuJYOXBAOcy/N8ZW0MkjCk66','user',NULL),(28,NULL,'2025-04-18 04:57:01.886129',1,'1email5@example.com','name5','$2a$10$07qza7Rs5Mi/2V/S8oe7z.zqM74r/tkKq7pOpd3BWaMmIxLE9QHde','user',NULL),(29,NULL,'2025-04-18 04:57:01.891137',1,'1email6@example.com','name6','$2a$10$Kt3Q73lh8c0tk4vdgJyMu.8kwQ8GculG7HXLrtBIQacTVVTxHqb5q','user',NULL),(30,NULL,'2025-04-18 04:57:01.896129',1,'1email7@example.com','name7','$2a$10$oJou975JI97j2RHARBtsnu6apTe6.gSKG6sDntZJY1uo00ZMpxgNC','user',NULL),(31,NULL,'2025-04-18 04:57:01.901124',1,'1email8@example.com','name8','$2a$10$UEg1AoygO/GdAzXPrV.P9.SKVWHJmZUAyHvEuZ45PFB3EX9Mdhb7y','user',NULL),(32,NULL,'2025-04-18 04:57:01.906128',1,'1email9@example.com','name9','$2a$10$j6C3B8xiQVHjHPARzOmWl.SYKEUcEYOpibFlw4GetFWmRuzrZyGOa','user',NULL),(64,NULL,'2025-05-04 09:33:31.721458',1,'3211742933@qq.com','3211742933@qq.com','$2a$10$BanYCyj0TLp.nCOHjPptwe9tg3S4ApeAG513v73zXEAROhVMxAmG2','user',NULL),(65,NULL,'2025-05-04 09:39:17.811222',1,'3211742@qq.com','3211742@qq.com','$2a$10$RMx5xXhc0yzfSLQohmCAO.UlXM.buuBdBin9F0CCB3nIE/XQb.ItW','user',NULL),(66,NULL,'2025-05-08 05:59:15.803703',1,'guest_5890@guest.com','Guest_5890','$2a$10$JsJf/UW4OEzh6y4/P0Rw.uPpFbwXe9siWFOPpfwAvkhaBGiuIlTdq','user',NULL),(67,'6222021234567890123','2025-05-08 05:59:33.780608',1,'guest_628@guest.com','Guest_628','$2a$10$WjWtLJ7wOSGRJzRWGbH0TOAG1jR7T8LqFjqgEMq9Bm13OQP/zbrbG','user',NULL),(68,'6222021234567890123','2025-05-08 06:03:37.195784',1,'guest_7591@guest.com','Guest_7591','$2a$10$PqvBagRiLn8rtrDhER6o2euOk.8mdi0pkJGJlj10QGA62fui4gaie','user',NULL),(69,'6222021234567890123','2025-05-08 06:11:41.144675',1,'guest_3489@guest.com','Guest_3489','$2a$10$xWLosmY7s9Y2ytYo.mq2W.OxslLSFsmiM/K6qxEFSKvDVzhAp5ciC','user',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-08 20:53:36
