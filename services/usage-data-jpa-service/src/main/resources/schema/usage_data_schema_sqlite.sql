CREATE TABLE user (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL UNIQUE
);

CREATE TABLE access_type (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT UNIQUE
);

CREATE TABLE user_accessed_document (
  document_type TEXT NOT NULL,
  document_uid TEXT NOT NULL,
  user_id INTEGER NOT NULL,
  access_type_id INTEGER NOT NULL,
  time_of_access TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES user (id),
  FOREIGN KEY (access_type_id) REFERENCES access_type (id)
);
