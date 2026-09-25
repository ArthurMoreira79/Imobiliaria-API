CREATE TABLE imoveis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_imovel VARCHAR(50) NOT NULL,
    endereco    VARCHAR(255) NOT NULL,
    cep         VARCHAR(10) NOT NULL,
    dormitorios INT NOT NULL,
    banheiros   INT NOT NULL,
    suites      INT NOT NULL,
    metragem    INT NOT NULL,
    valor_aluguel_sug DECIMAL(12, 2),
    obs         VARCHAR(500)
);