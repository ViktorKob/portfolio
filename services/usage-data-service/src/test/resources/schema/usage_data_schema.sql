CREATE TABLE IF NOT EXISTS usage_data.user (
  id INTEGER NOT NULL,
  name TEXT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS usage_data.access_type (
  id INTEGER NOT NULL,
  name TEXT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS usage_data.user_accessed_document (
  row INTEGER NOT NULL,
  document_type TEXT NOT NULL,
  document_uid TEXT NOT NULL,
  user_id INTEGER NOT NULL,
  access_type_id INTEGER NOT NULL,
  time_of_access INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (row),
  FOREIGN KEY (user_id) REFERENCES user(id),
  FOREIGN KEY (access_type_id) REFERENCES access_type (id)
);

CREATE INDEX document_index ON user_accessed_document (document_type,document_uid);