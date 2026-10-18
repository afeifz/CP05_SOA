-- Usuário inicial para permitir o primeiro acesso à API em uma instalação limpa.
-- Login: admin@autoescola.com
-- Senha: password
-- A senha armazenada abaixo já está em BCrypt.
INSERT INTO usuarios (login, senha, perfil)
SELECT 'admin@autoescola.com',
       '$2a$10$qGPzunjL9SR7bsoVpc7uieBH8BzyDEKtOpSRxKgUUcGMxj.bIHPaS',
       'ADMIN'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE login = 'admin@autoescola.com'
);
