-- cidade: usada na busca por exemplo (Query by example)
-- valor_diaria: base do cálculo automático do valor da locação
ALTER TABLE imoveis ADD COLUMN cidade VARCHAR(100) NULL AFTER cep;
ALTER TABLE imoveis ADD COLUMN valor_diaria DECIMAL(12, 2) NULL AFTER metragem;

-- Preenche os imóveis que já existiam antes de tornar as colunas obrigatórias
UPDATE imoveis SET cidade = 'São Luís' WHERE cidade IS NULL;
UPDATE imoveis SET valor_diaria = 100.00 WHERE valor_diaria IS NULL;

-- NOT NULL adicionado depois para evitar falhas caso o banco já houvesse imóveis cadastrados, pois ficariam sem valor.
ALTER TABLE imoveis MODIFY cidade VARCHAR(100) NOT NULL;
ALTER TABLE imoveis MODIFY valor_diaria DECIMAL(12, 2) NOT NULL;