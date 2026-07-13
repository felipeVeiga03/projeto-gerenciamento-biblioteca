-- Tabela de Livros
CREATE TABLE livro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    isbn VARCHAR(255) NOT NULL UNIQUE,
    disponivel BOOLEAN NOT NULL
);

-- Tabela de Leitores
CREATE TABLE leitores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    documento VARCHAR(255) NOT NULL UNIQUE,
    data_nascimento DATE NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL
);

-- Tabela de Usuários para autenticação
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    leitor_id BIGINT,
    FOREIGN KEY (leitor_id) REFERENCES leitores(id)
);

-- Tabela de Perfis (Roles)
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE
);

-- Tabela de Junção Usuario-Role
CREATE TABLE usuario_role (
    usuario_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, role_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (role_id) REFERENCES role(id)
);

-- Tabela de Empréstimos
CREATE TABLE emprestimo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_emprestimo DATE NOT NULL,
    data_prevista_devolucao DATE NOT NULL,
    data_efetiva_devolucao DATE,
    dias_atraso INT,
    valor_multa DECIMAL(10, 2),
    status_multa VARCHAR(50),
    id_livro BIGINT NOT NULL,
    id_leitor BIGINT NOT NULL,
    FOREIGN KEY (id_livro) REFERENCES livro(id),
    FOREIGN KEY (id_leitor) REFERENCES leitores(id)
);

-- Tabela de Reservas
CREATE TABLE reserva (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_criacao TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    data_atendimento TIMESTAMP,
    id_livro BIGINT NOT NULL,
    id_leitor BIGINT NOT NULL,
    FOREIGN KEY (id_livro) REFERENCES livro(id),
    FOREIGN KEY (id_leitor) REFERENCES leitores(id)
);

-- Tabela de Auditoria
CREATE TABLE auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    acao VARCHAR(255) NOT NULL,
    nome_entidade VARCHAR(255) NOT NULL,
    entidade_id BIGINT,
    timestamp TIMESTAMP NOT NULL
);
