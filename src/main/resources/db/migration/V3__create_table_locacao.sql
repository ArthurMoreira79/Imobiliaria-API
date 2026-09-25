CREATE TABLE locacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_imovel INT NOT NULL,
    id_inquilino INT NOT NULL,
    ativo BOOLEAN NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim    DATE,
    dia_vencimento INT NOT NULL,
    perc_multa     DECIMAL(5, 2),
    valor_aluguel DECIMAL (12, 2) NOT NULL,
    obs VARCHAR (500),

    CONSTRAINT fk_inquilino_locacao FOREIGN KEY (id_inquilino) REFERENCES clientes(id) ON DELETE RESTRICT,
    CONSTRAINT fk_imovel_locacao    FOREIGN KEY (id_imovel)    REFERENCES imoveis(id) ON DELETE RESTRICT
);