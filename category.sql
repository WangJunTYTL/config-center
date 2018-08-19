drop table if exists `category`;
CREATE TABLE `category`(
`id` bigint PRIMARY KEY NOT NULL COMMENT '类目id' AUTO_INCREMENT,
`name` VARCHAR(100) Not NULL COMMENT '类目名称',
`parent_category_id` INT NOT NULL DEFAULT 0 COMMENT '父类目id，默认为0，代表为顶级类目',
`description` VARCHAR(200) NOT NULL COMMENT '描述',
`add_time` DATETIME NOT NULL COMMENT '创建时间',
`mod_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
UNIQUE INDEX `category.name` (name)
);

drop table if exists `category_property`;
CREATE TABLE `category_property`(
  `id` bigint PRIMARY KEY NOT NULL COMMENT '属性id' AUTO_INCREMENT,
  `name` VARCHAR(100) Not NULL COMMENT '属性名称',
  `description` VARCHAR(200) NOT NULL COMMENT '描述',
  `type` INT NOT NULL DEFAULT 0 COMMENT '属性表单类型',
  `option` VARCHAR(200)  COMMENT '属性表单类型',
  `default_value` VARCHAR(200)  COMMENT '属性默认值',
  `add_time` DATETIME NOT NULL COMMENT '创建时间',
  `mod_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间'
);

drop table if exists `category_property_map`;
CREATE TABLE `category_property_map`(
  `id` bigint PRIMARY KEY NOT NULL COMMENT '自增id' AUTO_INCREMENT,
  `category_id` bigint Not NULL COMMENT '类目id',
  `property_id` bigint NOT NULL COMMENT '属性id',
  `add_time` DATETIME NOT NULL COMMENT '创建时间',
  `mod_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
   INDEX `category_property.category_id` (category_id),
   UNIQUE INDEX `category_property.category_id.property_id` (category_id, property_id)
) ;

drop table if exists `category_property_value`;
CREATE TABLE `category_property_value`(
  `id` bigint PRIMARY KEY NOT NULL COMMENT '自增id' AUTO_INCREMENT,
  `object_key` VARCHAR(500) not null default '' comment '属性值关联对象key',
  `category_id` bigint Not NULL COMMENT '类目id',
  `property_id` bigint NOT NULL COMMENT '属性id',
  `value` VARCHAR(100) DEFAULT NULL COMMENT '属性值',
  `comment` VARCHAR (500) DEFAULT NULL COMMENT '类目属性值可读描述',
  `add_time` DATETIME NOT NULL COMMENT '创建时间',
  `mod_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
  INDEX `category_property_value.merchant_id` (category_id),
  UNIQUE INDEX `category_property_value.category_id.property_id` (category_id, property_id)
) ;

drop table if exists `category_sync_log`;
CREATE TABLE `category_sync_log`(
`id` bigint PRIMARY KEY NOT NULL COMMENT '类目id' AUTO_INCREMENT,
`domain_type` int Not NULL COMMENT '事件对应的实体类型',
`domain_id` bigint Not NULL COMMENT '事件对应的实体ID',
`event_type` VARCHAR(100) Not NULL COMMENT '事件类型',
`description` VARCHAR(200)  COMMENT '中文描述，用于在Mis展示',
`add_time` DATETIME NOT NULL COMMENT '创建时间',
`mod_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间'
);

insert into category (`name`,`parent_category_id`,`description`,`add_time`) values ('ROOT',0,'类目根目录',now());

