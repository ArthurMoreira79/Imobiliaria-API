CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_cliente VARCHAR(255) NOT NULL,
    cpf          VARCHAR(11)  NOT NULL,
    telefone     VARCHAR(12),
    email        VARCHAR(100),
    dt_nascimento DATE NOT NULL
);
