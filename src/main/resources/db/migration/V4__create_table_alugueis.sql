CREATE TABLE alugueis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_locacao INT NOT NULL,
    dt_vencimento DATE NOT NULL,
    valor_pago DECIMAL(12, 2),
    obs VARCHAR(500),

    CONSTRAINT fk_locacao_alugueis FOREIGN KEY (id_locacao) REFERENCES locacao(id) ON DELETE RESTRICT
);