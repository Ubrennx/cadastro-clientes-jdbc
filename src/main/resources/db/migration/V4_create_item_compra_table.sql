CREATE TABLE IF NOT EXISTS item_compra (
    compra_id       BIGINT NOT NULL REFERENCES compra (id),
    produto_id      BIGINT NOT NULL REFERENCES produto (id),
    quantidade      INTEGER NOT NULL,
    preco_unitario  DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (compra_id, produto_id)
);