CREATE TABLE IF NOT EXISTS hospitais (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(160) NOT NULL,
    cnpj CHAR(14) NOT NULL UNIQUE,
    endereco VARCHAR(255) NOT NULL,
    foto MEDIUMTEXT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_hospital BIGINT,
    perfil ENUM('ADMIN', 'RECEPCAO', 'MEDICO', 'PACIENTE') NOT NULL,
    nome VARCHAR(160) NOT NULL,
    login VARCHAR(254) COLLATE utf8mb4_bin NOT NULL UNIQUE,
    senha_hash VARCHAR(60) NOT NULL,
    crm VARCHAR(40) UNIQUE,
    especialidade VARCHAR(120),
    cpf CHAR(11) UNIQUE,
    nascimento DATE,
    telefone VARCHAR(30),
    foto MEDIUMTEXT,

    FOREIGN KEY (id_hospital)
        REFERENCES hospitais(id),

    CHECK (
        (perfil = 'ADMIN' AND id_hospital IS NULL)
        OR
        (perfil <> 'ADMIN' AND id_hospital IS NOT NULL)
    )

) ENGINE=InnoDB;