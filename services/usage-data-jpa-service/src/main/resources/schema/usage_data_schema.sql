CREATE TABLE `user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `username_index` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `access_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `access_type_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `user_accessed_document` (
  `row` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `document_type` varchar(45) NOT NULL,
  `document_uid` varchar(32) NOT NULL,
  `user_id` int(11) unsigned NOT NULL,
  `access_type_id` int(11) unsigned NOT NULL,
  `time_of_access` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row`),
  UNIQUE KEY `row_UNIQUE` (`row`),
  KEY `user_foreign_key` (`user_id`),
  KEY `access_type_foreign_key_idx` (`access_type_id`),
  KEY `document_index` (`document_type`,`document_uid`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;