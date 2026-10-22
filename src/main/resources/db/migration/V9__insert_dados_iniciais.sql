-- Dados iniciais para testar as buscas (paginação e Query by Example)
-- e as regras de locação. Os ids são buscados pelo CPF e pelo endereço,
-- para funcionar mesmo que o banco já tenha outros registros.

INSERT INTO clientes (nome_cliente, cpf, telefone, email, dt_nascimento) VALUES
('Ana Beatriz Souza',  '30111122233', '98981110001', 'ana.souza@exemplo.com',     '1988-03-12'),
('Bruno Carvalho',     '30122233344', '98981110002', 'bruno.carvalho@exemplo.com', '1992-07-25'),
('Carla Mendes',       '30133344455', '98981110003', 'carla.mendes@exemplo.com',   '1979-11-02'),
('Diego Ferreira',     '30144455566', '98981110004', NULL,                         '2000-01-18'),
('Eduarda Lima',       '30155566677', '98981110005', 'eduarda.lima@exemplo.com',   '1995-05-30');

INSERT INTO imoveis (tipo_imovel, endereco, cep, cidade, dormitorios, banheiros, suites, metragem, valor_diaria, valor_aluguel_sug, obs) VALUES
('Apartamento',    'Av. Litorânea, 500, Calhau',             '65071377', 'São Luís',     3, 2, 1,  95, 250.00, 3500.00, 'Vista para o mar'),
('Casa',           'Rua do Sol, 120, Centro',                '65020590', 'São Luís',     4, 3, 2, 180, 320.00, 4200.00, 'Casarão histórico reformado'),
('Kitnet',         'Rua dos Afogados, 45, Centro',           '65010020', 'São Luís',     1, 1, 0,  30,  90.00,  900.00, NULL),
('Apartamento',    'Av. dos Holandeses, 10, Ponta d''Areia', '65077357', 'São Luís',     2, 2, 1,  70, 210.00, 2800.00, NULL),
('Casa',           'Rua Grande, 300, Centro',                '65900000', 'Imperatriz',   3, 2, 1, 140, 180.00, 2500.00, NULL),
('Sala comercial', 'Av. Getúlio Vargas, 900, Centro',        '65901000', 'Imperatriz',   0, 1, 0,  45, 150.00, 1800.00, 'Térreo, de frente para a avenida'),
('Casa de praia',  'Rua da Praia, 15, Araçagi',              '65110000', 'Raposa',       4, 3, 2, 220, 450.00, NULL,    'Pé na areia'),
('Apartamento',    'Rua Portugal, 77, Centro',               '65010480', 'São José de Ribamar', 2, 1, 0, 60, 160.00, 2000.00, NULL);

-- Locações: uma encerrada, uma em andamento, uma futura e uma cancelada.
-- O valor_total segue a mesma regra do service: diárias (DATEDIFF) x valor_diaria do imóvel.
INSERT INTO locacao (id_inquilino, id_imovel, ativo, data_inicio, data_fim, dia_vencimento, perc_multa, valor_total, obs)
SELECT c.id, i.id, TRUE, '2026-01-05', '2026-03-05', 5, 2.00, DATEDIFF('2026-03-05', '2026-01-05') * i.valor_diaria, 'Locação encerrada'
FROM clientes c, imoveis i WHERE c.cpf = '30111122233' AND i.endereco = 'Av. Litorânea, 500, Calhau';

INSERT INTO locacao (id_inquilino, id_imovel, ativo, data_inicio, data_fim, dia_vencimento, perc_multa, valor_total, obs)
SELECT c.id, i.id, TRUE, '2026-10-01', '2026-12-31', 10, 2.00, DATEDIFF('2026-12-31', '2026-10-01') * i.valor_diaria, 'Locação em andamento'
FROM clientes c, imoveis i WHERE c.cpf = '30122233344' AND i.endereco = 'Rua do Sol, 120, Centro';

INSERT INTO locacao (id_inquilino, id_imovel, ativo, data_inicio, data_fim, dia_vencimento, perc_multa, valor_total, obs)
SELECT c.id, i.id, TRUE, '2027-01-10', '2027-02-10', 10, 2.00, DATEDIFF('2027-02-10', '2027-01-10') * i.valor_diaria, 'Locação futura'
FROM clientes c, imoveis i WHERE c.cpf = '30133344455' AND i.endereco = 'Rua da Praia, 15, Araçagi';

INSERT INTO locacao (id_inquilino, id_imovel, ativo, data_inicio, data_fim, dia_vencimento, perc_multa, valor_total, obs)
SELECT c.id, i.id, FALSE, '2026-11-01', '2026-11-15', 1, NULL, DATEDIFF('2026-11-15', '2026-11-01') * i.valor_diaria, 'Locação cancelada: não bloqueia o período'
FROM clientes c, imoveis i WHERE c.cpf = '30144455566' AND i.endereco = 'Av. dos Holandeses, 10, Ponta d''Areia';

-- Aluguéis (pagamentos) da locação em andamento
INSERT INTO alugueis (id_locacao, dt_vencimento, valor_pago, obs)
SELECT l.id, '2026-10-10', 9600.00, 'Pago em dia'
FROM locacao l JOIN clientes c ON c.id = l.id_inquilino WHERE c.cpf = '30122233344';

INSERT INTO alugueis (id_locacao, dt_vencimento, valor_pago, obs)
SELECT l.id, '2026-11-10', NULL, 'Em aberto'
FROM locacao l JOIN clientes c ON c.id = l.id_inquilino WHERE c.cpf = '30122233344';