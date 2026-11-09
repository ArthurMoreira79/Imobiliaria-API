-- Perfis de acesso (ADMIN, ATENDENTE, CLIENTE)
CREATE TABLE perfis (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(30) NOT NULL,
    CONSTRAINT uk_perfis_nome UNIQUE (nome)
);

-- Usuários que fazem login na API.
-- senha: guarda o hash BCrypt (60 caracteres), nunca a senha em texto puro.
-- id_cliente: preenchido só para usuários com perfil CLIENTE; liga o login ao cadastro em clientes.
--             UNIQUE: um cliente tem no máximo um usuário (NULL pode repetir, como no email da V6).
CREATE TABLE usuarios (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    login      VARCHAR(50)  NOT NULL,
    senha      VARCHAR(100) NOT NULL,
    id_cliente INT NULL,
    CONSTRAINT uk_usuarios_login   UNIQUE (login),
    CONSTRAINT uk_usuarios_cliente UNIQUE (id_cliente),
    CONSTRAINT fk_usuarios_cliente FOREIGN KEY (id_cliente)
        REFERENCES clientes (id) ON DELETE RESTRICT
);

-- Relação N:N: um usuário pode ter vários perfis, e um perfil pertence a vários usuários.
CREATE TABLE usuarios_perfis (
    id_usuario INT NOT NULL,
    id_perfil  INT NOT NULL,
    PRIMARY KEY (id_usuario, id_perfil),
    CONSTRAINT fk_usuarios_perfis_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_usuarios_perfis_perfil FOREIGN KEY (id_perfil)
        REFERENCES perfis (id) ON DELETE RESTRICT
);