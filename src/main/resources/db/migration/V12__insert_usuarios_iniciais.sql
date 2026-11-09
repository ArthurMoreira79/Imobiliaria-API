-- Perfis e usuários iniciais para testes.
-- Senha de todos os usuários de teste: senha123
-- Hashes gerados com BCryptPasswordEncoder (custo 10). A mesma senha gera hashes
-- diferentes porque o BCrypt sorteia um "salt" a cada encode; o salt fica dentro do próprio hash.

INSERT INTO perfis (nome) VALUES ('ADMIN'), ('ATENDENTE'), ('CLIENTE');

INSERT INTO usuarios (login, senha, id_cliente) VALUES
('admin',     '$2a$10$zc8X1nb721XuUDk.U4ISueRpMJXQPX2lDScjeUHpzVwtb9wy7wURi', NULL),
('atendente', '$2a$10$aaTWqe2hmJPAbQbijyj87.ooi4YEt4gAPq03YKRhpWi1ElVh1BGuC', NULL);

-- Usuários CLIENTE ligados aos clientes da V9 (buscados pelo CPF, como na V9)
INSERT INTO usuarios (login, senha, id_cliente)
SELECT 'ana', '$2a$10$gYwc1j1ewPKRM4Ju7QeCbet5R167Wy7LLIgzPP.fPQanCiqRIpjwi', id
FROM clientes WHERE cpf = '30111122233';

INSERT INTO usuarios (login, senha, id_cliente)
SELECT 'bruno', '$2a$10$DG9VchgrS0AAJ.Kh7RAlnuDzyuZ865xjIP3N2XXNOJKbqYsVnLYHC', id
FROM clientes WHERE cpf = '30122233344';

-- Associação usuário x perfil, buscando os ids pelos nomes
INSERT INTO usuarios_perfis (id_usuario, id_perfil)
SELECT u.id, p.id FROM usuarios u, perfis p
WHERE (u.login = 'admin'     AND p.nome = 'ADMIN')
   OR (u.login = 'atendente' AND p.nome = 'ATENDENTE')
   OR (u.login IN ('ana', 'bruno') AND p.nome = 'CLIENTE');