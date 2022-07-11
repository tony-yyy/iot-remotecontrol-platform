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


/*Table structure for table `devicetype` */

DROP TABLE IF EXISTS `deviceType`;

CREATE TABLE `deviceType` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `typeName` varchar(20) DEFAULT NULL COMMENT '类型名',
  `describe` varchar(30) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `typeName` (`typeName`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `devicetype` */


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
