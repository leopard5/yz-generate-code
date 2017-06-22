USE `marketing_center`;

/*Table structure for table `mmc_dictionary` */

DROP TABLE IF EXISTS `mmc_dictionary`;

CREATE TABLE `mmc_dictionary` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `dict_item` varchar(32) NOT NULL COMMENT '字典类型',
  `dict_id` int(11) NOT NULL COMMENT '字典ID',
  `dict_value` varchar(32) NOT NULL COMMENT '字典值',
  `dict_name` varchar(128) NOT NULL COMMENT '字典名称',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1.生效 0.无效',
  `create_time` datetime DEFAULT NULL COMMENT '数据创建时间',
  `create_user` varchar(32) CHARACTER SET latin1 DEFAULT NULL COMMENT '数据创建人',
  `update_time` datetime DEFAULT NULL COMMENT '数据更新时间',
  `update_user` varchar(32) CHARACTER SET latin1 DEFAULT NULL COMMENT '数据创建人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `u_Item_id_value` (`dict_id`,`dict_item`,`dict_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='字典表';

/*Data for the table `mmc_dictionary` */
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COUPON_TYPE', 51, 'COUPON_CASH', '无门槛现金抵用券', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COUPON_TYPE', 52, 'COUPON_FULL_MINUS', '满减券', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COUPON_TYPE', 53, 'COUPON_PER_FULL_MINUS', '叠加满减券', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COUPON_TYPE', 54, 'COUPON_STEP_FULL_MINUS', '阶梯满减券', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COUPON_TYPE', 55, 'COUPON_DISCOUNT', '折扣券', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COUPON_TYPE', 56, 'COUPON_GIFT', '赠品券', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COUPON_TYPE', 57, 'COUPON_PRESENT', '礼品券', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('SUBPROMOTION_TYPE', 32, 'SUBPROMOTION_FULL_MINUS', '订单满减', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('SUBPROMOTION_TYPE', 33, 'SUBPROMOTION_PER_FULL_MINUS', '订单叠加满减', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('SUBPROMOTION_TYPE', 34, 'SUBPROMOTION_STEP_FULL_MINUS', '订单阶梯满减', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('SUBPROMOTION_TYPE', 35, 'SUBPROMOTION_DISCOUNT', '订单折扣', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('RESULT_TYPE', 1, 'RESULT_SUCCESS', '通过', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('RESULT_TYPE', 0, 'RESULT_FAIL', '不通过', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COMMON_STATUS', 1, 'SAVED', '已保存', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COMMON_STATUS', 2, 'APPLYING', '报名中', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COMMON_STATUS', 3, 'ONGOING', '进行中', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COMMON_STATUS', 4, 'STOPED', '已中止', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COMMON_STATUS', 5, 'CANCELED', '已取消', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COMMON_STATUS', 6, 'ENDED', '已结束', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COMMON_STATUS', 7, 'CLOSED', '已关闭', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COMMON_STATUS', 20, 'ONLINE', '已上线', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COMMON_STATUS', 21, 'OFFLINE', '已下线', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('AUDIT_STATUS', 0, 'INITIAL', '初始状态', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('AUDIT_STATUS', 1, 'AUDITING', '审核中', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('AUDIT_STATUS', 2, 'STOP_AUDITING', '中止审核中', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('AUDIT_STATUS', 3, 'CANCEL_AUDITING', '取消审核中', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('OWNER_TYPE', 1, 'PLATFORM', '平台', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('OWNER_TYPE', 2, 'MALL', '商场', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('OWNER_TYPE', 3, 'SHOP', '商户', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('PROMOTION_TYPE', 1, 'ITEM_PROMOTION', '商品促销', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('PROMOTION_TYPE', 2, 'SUB_PROMOTION', '订单促销', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('PROMOTION_TYPE', 3, 'COUPON', '优惠券', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('PARTICIPATE_STATUS', 1, 'NO_OPERATION', '未操作', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('PARTICIPATE_STATUS', 2, 'PARTICIPATED', '已参加', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('PARTICIPATE_STATUS', 3, 'NOT_PARTICIPATE', '不参加', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('BOOKING_FAVOR_TYPE', 1, 'SKU_REDUCE', '商品直降', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('BOOKING_FAVOR_TYPE', 2, 'BALANCE_DISCOUNT', '余款打折', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('BOOKING_FAVOR_TYPE', 3, 'DEPOSIT_DOUBLED', '定金翻倍', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('USED_PROMOTION_STATUS', 1, 'VALID', '有效', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('USED_PROMOTION_STATUS', 2, 'INVALID', '失效', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('USED_PROMOTION_STATUS', 3, 'FAILED', '失败', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('USED_PROMOTION_STATUS', 4, 'LOCKING', '锁定', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('USED_PROMOTION_STATUS', 5, 'REFUND', '退款', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('USER_COUPON_STATUS', 0, 'INVALID', '已失效', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('USER_COUPON_STATUS', 1, 'VALID', '未使用', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('USER_COUPON_STATUS', 2, 'USEING', '使用中', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('USER_COUPON_STATUS', 3, 'CANCELED', '已取消', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('USER_COUPON_STATUS', 4, 'USED', '已使用', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('USER_BOOKING_STATUS', 1, 'BOOKING', '已预订', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('USER_BOOKING_STATUS', 2, 'USED', '已使用(已购买) ', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('USER_BOOKING_STATUS', 3, 'REFUND', '已退款', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('AUDIT_OPT_TYPE', 1, 'CREATE', '创建', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('AUDIT_OPT_TYPE', 2, 'PATICIPATE', '参与', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('AUDIT_OPT_TYPE', 3, 'ADD', '新增', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('AUDIT_OPT_TYPE', 4, 'REMOVE', '删除', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('AUDIT_OPT_TYPE', 5, 'STOP', '中止', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('AUDIT_OPT_TYPE', 6, 'CANCEL', '取消', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('ITEM_PROMOTION_TYPE', 29, 'ITEM_PROMOTION_LIMITED_PURCHASE', '限时购', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('ITEM_PROMOTION_TYPE', 30, 'ITEMPROMOTION_BOOKING', '商品预定', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('ITEM_PROMOTION_TYPE', 31, 'ITEMPROMOTION_HOT', '爆款', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('REBATE_TYPE', 1, 'FAVOR_AMOUNT_PERCENT', '优惠金额比例', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('REBATE_TYPE', 2, 'PAYED_AMOUNT_PERCENT', '订单金额比例', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('REBATE_TYPE', 3, 'CONSTANT_AMOUNT', '固定金额', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('REBATE_TYPE', 4, 'PAYED_AMOUNT_PERCENT_LAYER', '订单金额比例阶梯', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('REBATE_LIMIT_TYPE', 1, 'RENT_RELATE', '租金倍数', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('REBATE_LIMIT_TYPE', 2, 'CONSTANT_LIMIT', '固定金额', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COMPUTE_STATUS', 1, 'INIT', '未计算', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COMPUTE_STATUS', 2, 'REBATE_COMPUTED', '已计算', 1, NULL, 'system', NULL, 'system');
INSERT INTO `mmc_dictionary` (`dict_item`,`dict_id`,`dict_value`,`dict_name`,`status`,`create_time`,`create_user`,`update_time`,`update_user`) VALUES ('COMPUTE_STATUS', 3, 'REBATE_SENT', '返点已发送', 1, NULL, 'system', NULL, 'system');