CREATE TABLE IF NOT EXISTS usuarios (
    id     BIGSERIAL PRIMARY KEY,
    nome   VARCHAR(255) NOT NULL,
    idade  INTEGER,
    email  VARCHAR(255) NOT NULL UNIQUE,
    senha  VARCHAR(255) NOT NULL,
    role   VARCHAR(50)  NOT NULL
);