CREATE DATABASE IF NOT EXISTS skill_chain DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE skill_chain;

CREATE TABLE IF NOT EXISTS `user` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `phone` VARCHAR(20),
    `email` VARCHAR(100),
    `nickname` VARCHAR(50),
    `avatar` VARCHAR(255),
    `bio` VARCHAR(500),
    `role` INT NOT NULL DEFAULT 0,
    `real_name_status` INT DEFAULT 0,
    `id_card_front` VARCHAR(255),
    `id_card_back` VARCHAR(255),
    `deleted` INT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `wallet` (
    `wallet_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `cny_coin_balance` DECIMAL(10,2) DEFAULT 0.00,
    `point_balance` DECIMAL(10,2) DEFAULT 0.00,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`wallet_id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `decoration` (
    `deco_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `image_url` VARCHAR(255),
    `type` INT NOT NULL,
    `is_equipped` INT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`deco_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `skill_category` (
    `category_id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `icon` VARCHAR(255),
    `sort_order` INT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `skill` (
    `skill_id` BIGINT NOT NULL AUTO_INCREMENT,
    `provider_id` BIGINT NOT NULL,
    `category_id` BIGINT NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `price_per_unit` DECIMAL(10,2) NOT NULL,
    `unit` VARCHAR(20),
    `service_mode` INT DEFAULT 1,
    `cover_image` VARCHAR(255),
    `images` TEXT,
    `status` INT DEFAULT 0,
    `view_count` INT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`skill_id`),
    KEY `idx_provider_id` (`provider_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `schedule` (
    `schedule_id` BIGINT NOT NULL AUTO_INCREMENT,
    `provider_id` BIGINT NOT NULL,
    `skill_id` BIGINT NOT NULL,
    `date` DATE NOT NULL,
    `time_slot` VARCHAR(20) NOT NULL,
    `status` INT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`schedule_id`),
    KEY `idx_provider_date` (`provider_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `order` (
    `order_id` BIGINT NOT NULL AUTO_INCREMENT,
    `buyer_id` BIGINT NOT NULL,
    `provider_id` BIGINT NOT NULL,
    `skill_id` BIGINT NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `currency_type` INT DEFAULT 1,
    `status` INT DEFAULT 0,
    `schedule_date` VARCHAR(20),
    `time_slot` VARCHAR(20),
    `location` VARCHAR(255),
    `verification_code` VARCHAR(20),
    `deleted` INT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`order_id`),
    KEY `idx_buyer_id` (`buyer_id`),
    KEY `idx_provider_id` (`provider_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `transaction_log` (
    `log_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `type` INT NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `currency` INT DEFAULT 1,
    `balance_after` DECIMAL(10,2),
    `description` VARCHAR(255),
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`log_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `task` (
    `task_id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255),
    `condition` VARCHAR(50) NOT NULL,
    `target_progress` INT NOT NULL,
    `reward_points` INT NOT NULL,
    `task_type` INT DEFAULT 1,
    `is_active` INT DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `user_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `task_id` BIGINT NOT NULL,
    `current_progress` INT DEFAULT 0,
    `is_claimed` INT DEFAULT 0,
    `reset_date` DATE,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_task_date` (`user_id`, `task_id`, `reset_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `review` (
    `review_id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `reviewer_id` BIGINT NOT NULL,
    `provider_id` BIGINT NOT NULL,
    `rating` INT NOT NULL,
    `content` VARCHAR(500),
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`review_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_provider_id` (`provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `appeal` (
    `appeal_id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `appealer_id` BIGINT NOT NULL,
    `reason` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `status` INT DEFAULT 0,
    `handle_result` VARCHAR(255),
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`appeal_id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `system_config` (
    `config_id` BIGINT NOT NULL AUTO_INCREMENT,
    `config_key` VARCHAR(50) NOT NULL,
    `config_value` VARCHAR(255),
    `description` VARCHAR(255),
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`config_id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
