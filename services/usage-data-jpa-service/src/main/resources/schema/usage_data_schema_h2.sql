CREATE TABLE IF NOT EXISTS user (
  id INT unsigned NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY user_name_UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS access_type (
  id INT unsigned NOT NULL AUTO_INCREMENT,
  name VARCHAR(45) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY access_type_name_UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS user_accessed_document (
  row_number BIGINT unsigned NOT NULL AUTO_INCREMENT,
  document_type VARCHAR(45) NOT NULL,
  document_uid VARCHAR(32) NOT NULL,
  user_id INT unsigned NOT NULL,
  access_type_id INT unsigned NOT NULL,
  time_of_access TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (row_number),
  FOREIGN KEY (user_id) REFERENCES user(id),
  FOREIGN KEY (access_type_id) REFERENCES access_type(id)
);

CREATE INDEX IF NOT EXISTS document_index ON user_accessed_document (document_type, document_uid);