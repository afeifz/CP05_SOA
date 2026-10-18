ALTER TABLE instrucoes
    ADD COLUMN cancelada tinyint NOT NULL DEFAULT 0,
    ADD COLUMN motivo_cancelamento varchar(30),
    ADD COLUMN data_cancelamento datetime;
