-- O MySQL cria TINYINT para as migrations anteriores, enquanto o Hibernate
-- pode validar booleanos Java como BIT. Ajustamos os campos booleanos para
-- BIT(1), mantendo os valores 0/1 já existentes.
ALTER TABLE instrutores
    MODIFY COLUMN ativo BIT(1) NOT NULL DEFAULT b'1';

ALTER TABLE alunos
    MODIFY COLUMN ativo BIT(1) NOT NULL DEFAULT b'1';

ALTER TABLE instrucoes
    MODIFY COLUMN cancelada BIT(1) NOT NULL DEFAULT b'0';
