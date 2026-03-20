-- 创建数据库
CREATE DATABASE IF NOT EXISTS skill_chain DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE skill_chain;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `bio` VARCHAR(500) COMMENT '个人简介',
    `role` INT NOT NULL DEFAULT 0 COMMENT '角色: 0-普通用户, 1-工作者, 2-管理员',
    `real_name_status` INT DEFAULT 0 COMMENT '实名认证状态: 0-未认证, 1-待审核, 2-已认证, 3-审核失败',
    `id_card_front` VARCHAR(255) COMMENT '身份证正面照',
    `id_card_back` VARCHAR(255) COMMENT '身份证背面照',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 钱包表
CREATE TABLE IF NOT EXISTS `wallet` (
    `wallet_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `cny_coin_balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT 'CNY挂钩币余额',
    `point_balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '积分余额',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`wallet_id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包表';

-- 虚拟装饰表
CREATE TABLE IF NOT EXISTS `decoration` (
    `deco_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '装饰ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '装饰名称',
    `image_url` VARCHAR(255) COMMENT '装饰图片URL',
    `type` INT NOT NULL COMMENT '装饰类型: 1-头像框, 2-头衔, 3-背景',
    `is_equipped` INT DEFAULT 0 COMMENT '是否装备: 0-未装备, 1-已装备',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`deco_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='虚拟装饰表';

-- 技能分类表
CREATE TABLE IF NOT EXISTS `skill_category` (
    `category_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(255) COMMENT '分类图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能分类表';

-- 技能表
CREATE TABLE IF NOT EXISTS `skill` (
    `skill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '技能ID',
    `provider_id` BIGINT NOT NULL COMMENT '提供者ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `title` VARCHAR(100) NOT NULL COMMENT '技能标题',
    `description` TEXT COMMENT '技能描述',
    `price_per_unit` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `unit` VARCHAR(20) COMMENT '单位: 次/小时/天',
    `service_mode` INT DEFAULT 1 COMMENT '服务方式: 1-线上, 2-线下',
    `cover_image` VARCHAR(255) COMMENT '封面图片',
    `images` TEXT COMMENT '图片列表(JSON)',
    `status` INT DEFAULT 0 COMMENT '状态: 0-待审核, 1-已上架, 2-已下架',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`skill_id`),
    KEY `idx_provider_id` (`provider_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能表';

-- 排期表
CREATE TABLE IF NOT EXISTS `schedule` (
    `schedule_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '排期ID',
    `provider_id` BIGINT NOT NULL COMMENT '提供者ID',
    `skill_id` BIGINT NOT NULL COMMENT '技能ID',
    `date` DATE NOT NULL COMMENT '日期',
    `time_slot` VARCHAR(20) NOT NULL COMMENT '时间段',
    `status` INT DEFAULT 0 COMMENT '状态: 0-空闲, 1-已预约, 2-锁定',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`schedule_id`),
    KEY `idx_provider_date` (`provider_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排期表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `order_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `buyer_id` BIGINT NOT NULL COMMENT '买家ID',
    `provider_id` BIGINT NOT NULL COMMENT '提供者ID',
    `skill_id` BIGINT NOT NULL COMMENT '技能ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    `currency_type` INT DEFAULT 1 COMMENT '货币类型: 1-CNY挂钩币, 2-积分',
    `status` INT DEFAULT 0 COMMENT '状态: 0-待支付, 1-待接单, 2-已接单, 3-服务中, 4-待评价, 5-已完成, 6-退款中, 7-已退款, 8-已取消',
    `schedule_date` VARCHAR(20) COMMENT '预约日期',
    `time_slot` VARCHAR(20) COMMENT '时间段',
    `location` VARCHAR(255) COMMENT '服务地点',
    `verification_code` VARCHAR(20) COMMENT '核销码',
    `deleted` INT DEFAULT 0 COMMENT 'logic delete: 0-no 1-yes',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`order_id`),
    KEY `idx_buyer_id` (`buyer_id`),
    KEY `idx_provider_id` (`provider_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 交易流水表
CREATE TABLE IF NOT EXISTS `transaction_log` (
    `log_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` INT NOT NULL COMMENT '类型: 1-充值, 2-消费, 3-收入, 4-提现, 5-退款',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `currency` INT DEFAULT 1 COMMENT '货币: 1-CNY挂钩币, 2-积分',
    `balance_after` DECIMAL(10,2) COMMENT '交易后余额',
    `description` VARCHAR(255) COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`log_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易流水表';

-- 任务字典表
CREATE TABLE IF NOT EXISTS `task` (
    `task_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `name` VARCHAR(50) NOT NULL COMMENT '任务名称',
    `description` VARCHAR(255) COMMENT '任务描述',
    `condition` VARCHAR(50) NOT NULL COMMENT '触发条件',
    `target_progress` INT NOT NULL COMMENT '目标进度',
    `reward_points` INT NOT NULL COMMENT '奖励积分',
    `task_type` INT DEFAULT 1 COMMENT '任务类型: 1-每日, 2-每周, 3-一次性',
    `is_active` INT DEFAULT 1 COMMENT '是否启用: 0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务字典表';

-- 用户任务进度表
CREATE TABLE IF NOT EXISTS `user_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `current_progress` INT DEFAULT 0 COMMENT '当前进度',
    `is_claimed` INT DEFAULT 0 COMMENT '是否已领取奖励: 0-未领取, 1-已领取',
    `reset_date` DATE COMMENT '重置日期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_task_date` (`user_id`, `task_id`, `reset_date`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户任务进度表';

-- 评价表
CREATE TABLE IF NOT EXISTS `review` (
    `review_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `reviewer_id` BIGINT NOT NULL COMMENT '评价者ID',
    `provider_id` BIGINT NOT NULL COMMENT '被评价者ID',
    `rating` INT NOT NULL COMMENT '评分: 1-5星',
    `content` VARCHAR(500) COMMENT '评价内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`review_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_provider_id` (`provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 提现申请表
CREATE TABLE IF NOT EXISTS `withdrawal_request` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '提现金额',
    `fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '手续费',
    `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实际到账',
    `status` INT DEFAULT 0 COMMENT '状态: 0-待审核, 1-已通过, 2-已拒绝',
    `bank_name` VARCHAR(50) COMMENT '银行名称',
    `bank_card` VARCHAR(50) COMMENT '银行卡号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `process_time` DATETIME COMMENT '处理时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提现申请表';

-- 申诉表
CREATE TABLE IF NOT EXISTS `appeal` (
    `appeal_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申诉ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `appealer_id` BIGINT NOT NULL COMMENT '申诉人ID',
    `reason` VARCHAR(255) NOT NULL COMMENT '申诉原因',
    `description` TEXT COMMENT '申诉描述',
    `status` INT DEFAULT 0 COMMENT '状态: 0-待处理, 1-处理中, 2-已处理',
    `handle_result` VARCHAR(255) COMMENT '处理结果',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`appeal_id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申诉表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
    `config_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` VARCHAR(50) NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(255) COMMENT '配置值',
    `description` VARCHAR(255) COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`config_id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 插入初始数据
INSERT INTO `skill_category` (`name`, `icon`, `sort_order`) VALUES
('家政服务', 'home-o', 1),
('技能陪练', 'game-o', 2),
('设计服务', 'photo-o', 3),
('咨询服务', 'chat-o', 4),
('教育培训', 'book-o', 5);

INSERT INTO `task` (`name`, `description`, `condition`, `target_progress`, `reward_points`, `task_type`) VALUES
('每日登录', '每天登录一次即可获得积分', 'daily_login', 1, 10, 1),
('浏览技能', '浏览技能详情可获得积分', 'view_skill', 5, 5, 1),
('完成订单', '完成订单可获得积分', 'complete_order', 1, 50, 1),
('发布评价', '发布评价可获得积分', 'publish_review', 1, 20, 1);

INSERT INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
('platform_commission_rate', '0.05', '平台佣金率'),
('withdrawal_fee_rate', '0.05', '提现手续费率'),
('point_to_coin_rate', '100', '积分兑换CNY挂钩币比例');

-- 工作者申请表
CREATE TABLE IF NOT EXISTS `worker_application` (
    `application_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `real_name` VARCHAR(50),
    `id_card` VARCHAR(30),
    `phone` VARCHAR(20),
    `skill_title` VARCHAR(120),
    `category` VARCHAR(60),
    `description` TEXT,
    `experience` TEXT,
    `price` DECIMAL(10,2),
    `unit` VARCHAR(20),
    `service_area` VARCHAR(120),
    `id_card_front_url` VARCHAR(255),
    `id_card_back_url` VARCHAR(255),
    `certificate_urls` TEXT,
    `status` INT DEFAULT 0 COMMENT '0-待审核 1-已通过 2-已拒绝 3-已撤回',
    `reject_reason` VARCHAR(255),
    `reviewer_id` BIGINT,
    `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `review_time` DATETIME,
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`application_id`),
    KEY `idx_worker_apply_user` (`user_id`),
    KEY `idx_worker_apply_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 公告表
CREATE TABLE IF NOT EXISTS `notice` (
    `notice_id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(120) NOT NULL,
    `type` VARCHAR(30) DEFAULT 'system',
    `content` TEXT,
    `status` INT DEFAULT 0 COMMENT '0-草稿 1-已发布',
    `is_top` TINYINT DEFAULT 0,
    `view_count` INT DEFAULT 0,
    `publish_time` DATETIME,
    `end_time` DATETIME,
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`notice_id`),
    KEY `idx_notice_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================
-- Compatibility patch: keep schema aligned with current entities
-- =============================================================

CREATE TABLE IF NOT EXISTS `withdrawal_request` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `fee` DECIMAL(10,2) DEFAULT 0.00,
    `actual_amount` DECIMAL(10,2) NOT NULL,
    `status` INT DEFAULT 0,
    `bank_name` VARCHAR(50),
    `bank_card` VARCHAR(50),
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `process_time` DATETIME,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `wallet` ADD COLUMN IF NOT EXISTS `payment_password` VARCHAR(255);
ALTER TABLE `wallet` ADD COLUMN IF NOT EXISTS `deleted` INT DEFAULT 0;

ALTER TABLE `decoration` ADD COLUMN IF NOT EXISTS `deleted` INT DEFAULT 0;
ALTER TABLE `decoration` ADD COLUMN IF NOT EXISTS `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE `skill` ADD COLUMN IF NOT EXISTS `unit_type` INT DEFAULT 1;
ALTER TABLE `skill` ADD COLUMN IF NOT EXISTS `media_urls` TEXT;
ALTER TABLE `skill` ADD COLUMN IF NOT EXISTS `deleted` INT DEFAULT 0;

ALTER TABLE `schedule` ADD COLUMN IF NOT EXISTS `location` VARCHAR(255);
ALTER TABLE `schedule` ADD COLUMN IF NOT EXISTS `deleted` INT DEFAULT 0;
ALTER TABLE `schedule` ADD COLUMN IF NOT EXISTS `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `schedule_id` BIGINT;

ALTER TABLE `transaction_log` ADD COLUMN IF NOT EXISTS `deleted` INT DEFAULT 0;
ALTER TABLE `transaction_log` ADD COLUMN IF NOT EXISTS `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE `task` ADD COLUMN IF NOT EXISTS `deleted` INT DEFAULT 0;
ALTER TABLE `task` ADD COLUMN IF NOT EXISTS `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE `user_task` ADD COLUMN IF NOT EXISTS `deleted` INT DEFAULT 0;

ALTER TABLE `review` ADD COLUMN IF NOT EXISTS `images` TEXT;
ALTER TABLE `review` ADD COLUMN IF NOT EXISTS `reply_content` VARCHAR(500);
ALTER TABLE `review` ADD COLUMN IF NOT EXISTS `is_appeal` INT DEFAULT 0;
ALTER TABLE `review` ADD COLUMN IF NOT EXISTS `deleted` INT DEFAULT 0;
ALTER TABLE `review` ADD COLUMN IF NOT EXISTS `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
