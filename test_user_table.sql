/*
Navicat MySQL Data Transfer

Source Server         : mysal
Source Server Version : 50727
Source Host           : localhost:3306
Source Database       : test_demo

Target Server Type    : MYSQL
Target Server Version : 50727
File Encoding         : 65001

Date: 2020-12-03 18:26:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `test_user_table`
-- ----------------------------
DROP TABLE IF EXISTS `test_user_table`;
CREATE TABLE `test_user_table` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(30) NOT NULL,
  `age` int(11) NOT NULL,
  `phone_num` varchar(11) NOT NULL,
  `password` varchar(30) NOT NULL,
  `create_dt` datetime NOT NULL,
  `update_dt` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of test_user_table
-- ----------------------------
INSERT INTO `test_user_table` VALUES ('1', '李询', '20', '19983515447', '13579', '2020-12-01 22:42:53', '2020-12-01 22:42:53');
INSERT INTO `test_user_table` VALUES ('6', '李二', '30', '19984515447', '13579', '2020-12-01 23:01:18', '2020-12-01 23:01:18');
INSERT INTO `test_user_table` VALUES ('7', '李一', '18', '19984515447', '13579', '2020-12-01 23:01:18', '2020-12-01 23:01:18');
INSERT INTO `test_user_table` VALUES ('8', '王五', '60', '19984515447', '13579', '2020-12-01 23:01:18', '2020-12-01 23:01:18');
INSERT INTO `test_user_table` VALUES ('9', '李四', '50', '19984515447', '13579', '2020-12-01 23:01:18', '2020-12-01 23:01:18');
INSERT INTO `test_user_table` VALUES ('10', '李三', '40', '19984515447', '13579', '2020-12-01 23:01:18', '2020-12-01 23:01:18');
INSERT INTO `test_user_table` VALUES ('11', '张一', '18', '19984515447', '13579', '2020-12-01 23:04:20', '2020-12-01 23:04:20');
INSERT INTO `test_user_table` VALUES ('12', '明五', '60', '19984515447', '13579', '2020-12-01 23:04:20', '2020-12-01 23:04:20');
INSERT INTO `test_user_table` VALUES ('13', '张三', '40', '19984515447', '13579', '2020-12-01 23:04:20', '2020-12-01 23:04:20');
INSERT INTO `test_user_table` VALUES ('14', '张四', '50', '19984515447', '13579', '2020-12-01 23:04:20', '2020-12-01 23:04:20');
INSERT INTO `test_user_table` VALUES ('15', '张二', '30', '19984515447', '13579', '2020-12-01 23:04:20', '2020-12-01 23:04:20');
INSERT INTO `test_user_table` VALUES ('16', '张一', '18', '19984515447', '13579', '2020-12-01 23:05:57', '2020-12-01 23:05:57');
