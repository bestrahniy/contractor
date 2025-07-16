CREATE TABLE contractor (
    id VARCHAR(12) PRIMARY KEY NOT NULL,
    parent_id VARCHAR(12) REFERENCES contractor(id) ON DELETE SET NULL,
    name TEXT NOT NULL,
    name_full TEXT,
    inn TEXT,
    ogrn TEXT,
    country TEXT REFERENCES country(id) ON DELETE SET NULL,
    industry INTEGER REFERENCES industry(id) ON DELETE SET NULL,
    org_form  INTEGER REFERENCES org_form(id) ON DELETE SET NULL,
    create_date TIMESTAMP NOT NULL DEFAULT NOW(),
    modify_date TIMESTAMP,
    create_user_id TEXT,
    modify_user_id TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);