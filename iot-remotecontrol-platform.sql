/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.27-log : Database - smarthomesystemdatabase
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`smarthomesystemdatabase` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `smarthomesystemdatabase`;

/*Table structure for table `device` */

DROP TABLE IF EXISTS `device`;

CREATE TABLE `device` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `deviceName` varchar(30) NOT NULL COMMENT '设备标识',
  `remarks` varchar(50) DEFAULT NULL COMMENT '备注',
  `switchState` tinyint(1) NOT NULL DEFAULT '0' COMMENT '开关状态（1开、0关）',
  `roomId` int(11) unsigned DEFAULT NULL COMMENT '房间id（外键）',
  `deviceTypeId` int(11) unsigned DEFAULT NULL COMMENT '设备类型id（外键）',
  `isMultiSwitch` tinyint(1) DEFAULT '0' COMMENT '是否是多状态设备',
  `deviceSecret` varchar(35) DEFAULT NULL COMMENT '设备密钥',
  `pic` varchar(250) DEFAULT 'img/default.png' COMMENT '图片地址：deviceName + "-" + deviceSeret + .png',
  `unit` varchar(30) DEFAULT NULL COMMENT '传感器采样单位',
  `ownerId` int(11) unsigned DEFAULT NULL COMMENT '拥有者id',
  `currentMultiSwitchState` int(10) DEFAULT NULL COMMENT '当前多状态设备的状态',
  `latestUpdateTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后一个数据更新的时间',
  PRIMARY KEY (`id`),
  KEY `deviceTypeId` (`deviceTypeId`),
  KEY `roomId` (`roomId`),
  KEY `ownerId` (`ownerId`),
  CONSTRAINT `device_ibfk_1` FOREIGN KEY (`deviceTypeId`) REFERENCES `devicetype` (`id`),
  CONSTRAINT `device_ibfk_2` FOREIGN KEY (`roomId`) REFERENCES `room` (`id`),
  CONSTRAINT `device_ibfk_3` FOREIGN KEY (`ownerId`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8;

/*Data for the table `device` */

insert  into `device`(`id`,`deviceName`,`remarks`,`switchState`,`roomId`,`deviceTypeId`,`isMultiSwitch`,`deviceSecret`,`pic`,`unit`,`ownerId`,`currentMultiSwitchState`,`latestUpdateTime`) values (2,'alarmTest01','主报警器',0,NULL,1,0,'092901dc3e3449b2ab34075534d83b2a','img/deviceImg/alarm.jpg?id=666',NULL,1,1,'2022-06-28 10:26:08'),(3,'alarmTest02','一体式报警器',0,7,1,0,'ba103ceb1bd848848e1744653fc0b8dd','img/deviceImg/yanwu.jpg?id=3cfc8c',NULL,1,1,'2022-06-18 11:41:34'),(5,'testRealy01','继电器',0,5,3,0,'972e1ca506be4dc08c130ad4a30d0b90','img/deviceImg/relay.jpg',NULL,1,1,'2022-05-13 16:36:00'),(6,'temperatureTest01','温度计',1,NULL,5,0,'bb90f08378e54ee7a0a5f4b6e37c193a','img/deviceImg/IMG_20220202_134445.jpg','摄氏度',1,1,'2022-02-07 23:34:41'),(7,'humidityTest01','湿度计',0,NULL,5,0,'bb90f08378e54ee7a0a5f4b6e37c193a','img/deviceImg/IMG_20220202_134445.jpg','%',1,1,'2022-02-05 01:54:09'),(8,'mq5Test','可燃气传感器',0,NULL,5,0,'01b95f5404cd4de29b966101a6064f24','img/deviceImg/yanwu.jpg?id=a7868','%',1,1,'2022-02-05 01:54:09'),(9,'mq2Test','烟雾传感器',0,NULL,5,0,'bb90f08378e54ee7a0a5f4b6e37c193a','img/deviceImg/yanwu.jpg?id=1e2a5','%',1,1,'2022-02-05 01:54:09'),(69,'lightRGB','RGB多色灯',0,1,2,1,'14e4c5cd489e4d7c93fac1d532a9e4d8','img/deviceImg/light159-14e4c5cd489e4d7c93fac1d532a9e4d8.png?id=6a9ae2',NULL,1,1,'2022-04-19 19:53:15'),(72,'espCam','门口监控',0,8,4,1,'2863624987774568b5e98e0f915f9bd8','img/deviceImg/espCam-2863624987774568b5e98e0f915f9bd8.png',NULL,1,8,'2022-02-22 00:42:29'),(76,'bodySensor','门口人体传感器',0,8,5,0,'bc843d2ceda64bdd99a1f55b6c706aa7','img/deviceImg/bodySensor-bc843d2ceda64bdd99a1f55b6c706aa7.png?id=1aa12','',1,NULL,'2022-02-10 00:48:29'),(78,'laptopCamera','笔记本摄像头',0,NULL,4,1,'1a9d7bcf183f46eebbd5ca4fe580efca','img/default.png',NULL,1,0,'2022-02-16 22:23:34'),(79,'RFID_RC522','门禁',0,8,6,1,'e0e2287607d64acaa12c97efe3c1f891','img/deviceImg/RFID_RC522-e0e2287607d64acaa12c97efe3c1f891.png',NULL,1,0,'2022-04-19 19:53:15'),(80,'duoji','已弃用（开门舵机）',0,8,6,1,'6fbca216f9a14f639152d4d08ac1272b','img/deviceImg/duoji-6fbca216f9a14f639152d4d08ac1272b.png?id=fe19ce',NULL,1,2,'2022-04-19 19:53:15'),(82,'aaa','aaaa',0,NULL,3,0,'a807d0e6b9364786871eb8887b322855','img/default.png',NULL,4,NULL,'2022-02-19 22:41:26'),(83,'stepMotor','步进电机（开关门）',0,8,6,1,'30cfad3b9f874a15b4c14a5322421044','img/deviceImg/stepMotor-30cfad3b9f874a15b4c14a5322421044.png',NULL,1,0,'2022-04-19 19:53:40');

/*Table structure for table `devicetype` */

DROP TABLE IF EXISTS `devicetype`;

CREATE TABLE `devicetype` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `typeName` varchar(20) DEFAULT NULL COMMENT '类型名',
  `describe` varchar(30) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `typeName` (`typeName`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `devicetype` */

insert  into `devicetype`(`id`,`typeName`,`describe`) values (1,'alarm','报警设备'),(2,'light','灯光设备'),(3,'relay','继电器'),(4,'camera','摄像头设备'),(5,'sensor','传感器'),(6,'other','其他类型设备');

/*Table structure for table `event` */

DROP TABLE IF EXISTS `event`;

CREATE TABLE `event` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '模式主键',
  `modeName` varchar(20) NOT NULL COMMENT '模式名',
  `describe` varchar(50) DEFAULT NULL COMMENT '描述',
  `triggerDeviceId` int(10) unsigned DEFAULT NULL COMMENT '触发设备的id',
  `triggerThreshold` double(10,2) DEFAULT NULL COMMENT '触发阈值，当超过这个值，执行事件',
  `comparisonOperator` int(11) DEFAULT NULL COMMENT '1等于、2大于、3小于',
  `triggerState` int(11) DEFAULT NULL COMMENT '为改状态时触发',
  `active` tinyint(4) DEFAULT NULL COMMENT '是否激活',
  `autoSendEmail` tinyint(4) DEFAULT NULL COMMENT '自动发送邮件',
  PRIMARY KEY (`id`),
  KEY `triggerDeviceId` (`triggerDeviceId`),
  CONSTRAINT `event_ibfk_1` FOREIGN KEY (`triggerDeviceId`) REFERENCES `device` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8;

/*Data for the table `event` */

insert  into `event`(`id`,`modeName`,`describe`,`triggerDeviceId`,`triggerThreshold`,`comparisonOperator`,`triggerState`,`active`,`autoSendEmail`) values (112,'湿度过高报警','湿度过高报警',7,75.00,2,0,0,0),(129,'触发监控','触发监控',76,1.00,1,0,0,1),(131,'nfc开门','开门',79,0.00,2,1,1,0),(133,'asdsa','asdsa',3,0.00,2,1,0,0),(134,'可燃浓度气过高报警','可燃浓度气过高报警',8,25.00,2,0,0,0);

/*Table structure for table `eventtriggerstep` */

DROP TABLE IF EXISTS `eventtriggerstep`;

CREATE TABLE `eventtriggerstep` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `eventId` int(11) unsigned DEFAULT NULL COMMENT '时间id',
  `deviceId` int(10) unsigned DEFAULT NULL COMMENT '设备id',
  `switchState` tinyint(1) DEFAULT NULL COMMENT '开还是关',
  `isMultiSwitch` tinyint(4) DEFAULT NULL COMMENT '是否是多状态设备',
  PRIMARY KEY (`id`),
  KEY `deviceId` (`deviceId`),
  KEY `eventId` (`eventId`),
  CONSTRAINT `eventtriggerstep_ibfk_1` FOREIGN KEY (`deviceId`) REFERENCES `device` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `eventtriggerstep_ibfk_2` FOREIGN KEY (`eventId`) REFERENCES `event` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=189 DEFAULT CHARSET=utf8;

/*Data for the table `eventtriggerstep` */

insert  into `eventtriggerstep`(`id`,`eventId`,`deviceId`,`switchState`,`isMultiSwitch`) values (144,112,2,1,0),(175,129,72,8,1),(178,131,79,0,1),(179,131,69,1,1),(180,131,80,2,1),(181,131,83,2,1),(185,133,3,0,0),(186,134,3,1,0),(187,134,2,1,0),(188,134,69,3,1);

/*Table structure for table `multistateswitch` */

DROP TABLE IF EXISTS `multistateswitch`;

CREATE TABLE `multistateswitch` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `deviceId` int(11) unsigned DEFAULT NULL COMMENT '设备id',
  `switchState` int(11) DEFAULT NULL COMMENT '状态',
  `describe` varchar(20) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`),
  KEY `deviceId` (`deviceId`),
  CONSTRAINT `multistateswitch_ibfk_1` FOREIGN KEY (`deviceId`) REFERENCES `device` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=154 DEFAULT CHARSET=utf8;

/*Data for the table `multistateswitch` */

insert  into `multistateswitch`(`id`,`deviceId`,`switchState`,`describe`) values (111,78,0,'开'),(112,78,1,'关'),(130,72,0,'分辨率：96x96'),(131,72,2,'分辨率：176x144'),(132,72,4,'分辨率：240x240'),(133,72,6,'分辨率：400x296'),(134,72,8,'分辨率：640x480'),(135,72,10,'分辨率：1024x768'),(136,79,0,'待机'),(137,79,1,'开门'),(144,80,0,'门常关'),(145,80,1,'门关开'),(146,80,2,'门开后延迟6秒关'),(147,83,0,'常关'),(148,83,1,'常开'),(149,83,2,'开门6秒后关'),(150,69,0,'关'),(151,69,1,'白光'),(152,69,2,'暖光'),(153,69,3,'氛围模式');

/*Table structure for table `room` */

DROP TABLE IF EXISTS `room`;

CREATE TABLE `room` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(20) NOT NULL COMMENT '房间名字',
  `ownerId` int(11) unsigned NOT NULL COMMENT '用户id（外键）',
  `describe` varchar(50) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`),
  KEY `ownerId` (`ownerId`),
  CONSTRAINT `room_ibfk_1` FOREIGN KEY (`ownerId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

/*Data for the table `room` */

insert  into `room`(`id`,`name`,`ownerId`,`describe`) values (1,'卧室',1,'tony的卧室'),(4,'书房',1,'tony的书房'),(5,'主卧',1,'tony的主卧'),(6,'厨房',1,'我的厨房'),(7,'客厅',1,'tony的客厅'),(8,'入户门',1,'tony的入户门'),(12,'卧室',2,'卧室'),(24,'sad',4,'da');

/*Table structure for table `sensordata` */

DROP TABLE IF EXISTS `sensordata`;

CREATE TABLE `sensordata` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sensorId` int(10) unsigned NOT NULL COMMENT '外键（传感器id）',
  `samplingTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '采样时间（datetime类型）',
  `samplingData` double(10,2) NOT NULL COMMENT '采样数据',
  PRIMARY KEY (`id`),
  KEY `sensorId` (`sensorId`),
  CONSTRAINT `sensordata_ibfk_1` FOREIGN KEY (`sensorId`) REFERENCES `device` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1620182 DEFAULT CHARSET=utf8;

/*Data for the table `sensordata` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(20) NOT NULL COMMENT '用户名',
  `password` varchar(20) NOT NULL COMMENT '密码',
  `email` varchar(40) DEFAULT NULL,
  `realName` varchar(15) DEFAULT NULL COMMENT '名字',
  `gender` char(1) DEFAULT NULL COMMENT '性别',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`id`,`username`,`password`,`email`,`realName`,`gender`) values (1,'tony','123','1605337475@qq.com','马志明','男'),(2,'testUser','123456',NULL,NULL,NULL),(4,'testTony','testTony',NULL,NULL,NULL);

/*!50106 set global event_scheduler = 1*/;

/* Event structure for event `del_event` */

/*!50106 DROP EVENT IF EXISTS `del_event`*/;

DELIMITER $$

/*!50106 CREATE DEFINER=`root`@`localhost` EVENT `del_event` ON SCHEDULE EVERY 1 DAY STARTS '2022-02-08 00:00:00' ON COMPLETION NOT PRESERVE ENABLE DO call del_data() */$$
DELIMITER ;

/* Procedure structure for procedure `del_data` */

/*!50003 DROP PROCEDURE IF EXISTS  `del_data` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `del_data`()
BEGIN
	DELETE FROM sensorData WHERE samplingTime < DATE_SUB(CURDATE(),INTERVAL 1 DAY);
END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
