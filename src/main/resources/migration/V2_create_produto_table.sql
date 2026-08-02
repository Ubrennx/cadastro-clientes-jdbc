CREATE TABLE IF NOT EXISTS produto (
    id                        BIGSERIAL PRIMARY KEY,
    codigo_de_barras          VARCHAR(255) NOT NULL UNIQUE,
    nome                      VARCHAR(255) NOT NULL,
    preco                     DOUBLE PRECISION NOT NULL,
    quatidade_em_estoque      INTEGER NOT NULL,
    data_criacao              TIMESTAMP,
    data_ultima_atualizacao   TIMESTAMP
);