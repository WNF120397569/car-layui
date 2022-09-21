/*
Navicat MySQL Data Transfer

Source Server         : mysql1
Source Server Version : 50736
Source Host           : localhost:3306
Source Database       : car_new

Target Server Type    : MYSQL
Target Server Version : 50736
File Encoding         : 65001

Date: 2022-07-03 18:54:36
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for car
-- ----------------------------
DROP TABLE IF EXISTS `car`;
CREATE TABLE `car` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `user_id` bigint(20) DEFAULT NULL COMMENT '车主id',
  `car_id` varchar(20) DEFAULT NULL COMMENT '车牌号',
  `type` varchar(20) DEFAULT NULL COMMENT '车辆类型',
  `car_describe` varchar(255) DEFAULT NULL COMMENT '描述',
  `rent` int(11) DEFAULT NULL COMMENT '价格',
  `deposit` int(11) DEFAULT NULL COMMENT '押金',
  `status` int(11) DEFAULT NULL COMMENT '状态',
  `thumbnail_url` varchar(255) DEFAULT NULL COMMENT '略缩图',
  `slide_url` varchar(255) DEFAULT NULL COMMENT '轮播图',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `contact_name` varchar(20) DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(11) DEFAULT NULL COMMENT '联系人电话',
  `is_deleted` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8 COMMENT='汽车表';

-- ----------------------------
-- Records of car
-- ----------------------------
INSERT INTO `car` VALUES ('1', '2022-06-30 12:28:05', '0', '111', '轿车', '黄色的车', '150', '150', '1', '/upload/2022-06-30-11-19-37.jpeg', '[\"/upload/2022-06-30-11-19-37.jpeg\"]', '01', '汪楠丰', '15213535519', '0');
INSERT INTO `car` VALUES ('2', '2022-06-30 12:28:19', '0', '222', '轿车', '白色的车车', '250', '250', '1', '/upload/2022-06-30-11-20-17.jpeg', '[\"/upload/2022-06-30-11-20-17.jpeg\"]', '02', '汪楠丰', '15213535519', '0');
INSERT INTO `car` VALUES ('3', '2022-06-30 12:28:28', '0', '333', 'SUV', '黑色的车', '350', '350', '1', '/upload/2022-06-30-11-21-33.jpeg', '[\"/upload/2022-06-30-11-21-33.jpeg\"]', '03', '汪楠丰', '15213535519', '0');
INSERT INTO `car` VALUES ('4', '2022-06-30 12:28:35', '0', '444', '跑车', '白色的车', '450', '450', '0', '/upload/2022-06-30-11-22-11.jpeg', '[\"/upload/2022-06-30-11-22-11.jpeg\"]', '04', '汪楠丰', '15213535519', '0');
INSERT INTO `car` VALUES ('5', '2022-06-30 12:28:42', '0', '555', '跑车', '白色的跑车', '550', '550', '0', '/upload/2022-06-30-11-22-42.jpeg', '[\"/upload/2022-06-30-11-22-42.jpeg\"]', '05', '汪楠丰', '15213535519', '0');
INSERT INTO `car` VALUES ('6', '2022-06-30 12:28:51', '0', '666', 'SUV', '红色的suv车', '650', '650', '1', '/upload/2022-06-30-11-24-57.jpeg', '[\"/upload/2022-06-30-11-24-57.jpeg\"]', '06', '汪楠丰', '15213535519', '0');
INSERT INTO `car` VALUES ('7', '2022-06-30 11:25:46', '0', '777', '轿车', '红色的轿车', '700', '700', '1', '/upload/2022-06-30-11-25-31.jpeg', '[\"/upload/2022-06-30-11-25-31.jpeg\"]', '07', '汪楠丰', '15213535519', '0');
INSERT INTO `car` VALUES ('75', '2022-07-01 09:23:20', '0', '12222222', '轿车', '123123', '13231', '123123', '1', '/upload/2022-07-01-09-23-18.jpeg', '[\"/upload/2022-07-01-09-23-18.jpeg\"]', 'wang-nanfeng', '汪楠丰', '15213535519', '0');

-- ----------------------------
-- Table structure for car_order
-- ----------------------------
DROP TABLE IF EXISTS `car_order`;
CREATE TABLE `car_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `customer_id` bigint(20) DEFAULT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  `car_id` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '-3已取消，-2待签合同，-1待付款，0生效中，1已到期,2换车',
  `rent` int(11) DEFAULT NULL,
  `deposit` int(11) DEFAULT NULL,
  `days` mediumtext,
  `total` int(11) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COMMENT='订单表';

-- ----------------------------
-- Records of car_order
-- ----------------------------
INSERT INTO `car_order` VALUES ('19', '2022-06-30 20:24:39', '1', '0', '7', '1', '700', '700', '1', '1400', '2022-06-30 00:00:00', '2022-07-01 09:23:48');
INSERT INTO `car_order` VALUES ('20', '2022-07-01 09:16:35', '1', '0', '5', '0', '550', '550', '32', '17600', '2022-07-01 00:00:00', '2022-08-02 00:00:00');
INSERT INTO `car_order` VALUES ('21', '2022-07-01 09:20:40', '1', '0', '4', '0', '450', '450', '33', '14850', '2022-07-01 00:00:00', '2022-08-03 00:00:00');

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `title` varchar(64) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `username` varchar(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '0：待处理\n1：已处理',
  `reply` varchar(255) DEFAULT NULL COMMENT '回复',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='反馈表';

-- ----------------------------
-- Records of feedback
-- ----------------------------
INSERT INTO `feedback` VALUES ('1', '2022-06-23 21:46:04', '反馈测试', '这里是内容这里是内容这里是内容这里是内容', '1', 'user', '1', '123');
INSERT INTO `feedback` VALUES ('5', '2022-07-01 09:21:32', '123', '123', '1', 'user', '1', '456');

-- ----------------------------
-- Table structure for mark
-- ----------------------------
DROP TABLE IF EXISTS `mark`;
CREATE TABLE `mark` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `car_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='收藏';

-- ----------------------------
-- Records of mark
-- ----------------------------
INSERT INTO `mark` VALUES ('1', '2022-06-30 20:34:03', '1', '6');

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `content` varchar(1024) DEFAULT NULL,
  `is_deleted` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='资讯表';

-- ----------------------------
-- Records of news
-- ----------------------------
INSERT INTO `news` VALUES ('1', '2022-06-26 18:33:36', '第一条资讯测试', '这里是内容这里是内容这里是内容这里是内容这里是内容这里是内容这里是内容这里是内容这里是内容这里是内容这里是内容哈哈哈哈', '0');
INSERT INTO `news` VALUES ('2', '2022-06-26 18:36:35', '测试', '测试测试测试测试测试', '0');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(20) DEFAULT NULL COMMENT '姓名',
  `username` varchar(20) DEFAULT NULL COMMENT '用户名',
  `password` varchar(20) DEFAULT NULL COMMENT '密码',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机',
  `email` varchar(20) DEFAULT NULL COMMENT '邮箱',
  `create_time` datetime DEFAULT NULL COMMENT '注册时间',
  `id_card` varchar(20) DEFAULT NULL COMMENT '身份证号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `status` int(11) DEFAULT '0' COMMENT '1正常，0禁用',
  `role` varchar(20) DEFAULT NULL COMMENT '角色',
  `sex` varchar(20) DEFAULT NULL COMMENT '性别',
  `is_deleted` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_username_uindex` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('0', 'admin', 'admin', '123', '11111111111', '123456789@qq.com', null, '123456798765432100', '/img/default-avatar.jpg', '1', 'admin', '保密', '0');
INSERT INTO `user` VALUES ('1', '汪楠丰', 'user', '123', '15213535519', '120397569@qq.com', '2022-06-30 10:58:13', '500228200104278958', '/img/default-avatar.jpg', '1', 'customer', '保密', '0');
