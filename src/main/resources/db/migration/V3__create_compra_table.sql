CREATE TABLE IF NOT EXISTS compra (
    id           BIGSERIAL PRIMARY KEY,
    data_compra  TIMESTAMP,
    cliente_id   BIGINT NOT NULL REFERENCES usuarios (id),
    valor_total  DOUBLE PRECISION
);