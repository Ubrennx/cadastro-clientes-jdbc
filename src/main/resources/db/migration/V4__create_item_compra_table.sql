CREATE TABLE item_compra (
    compra_id      NUMBER NOT NULL REFERENCES compra (id),
    produto_id     NUMBER NOT NULL REFERENCES produto (id),
    quantidade     NUMBER NOT NULL,
    preco_unitario NUMBER(10,2) NOT NULL,
    PRIMARY KEY (compra_id, produto_id)
);