
USE healthhub;

CREATE TABLE IF NOT EXISTS hospitais (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(160) NOT NULL,
    cnpj CHAR(14) NOT NULL UNIQUE,
    endereco VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_hospital BIGINT,
    perfil ENUM('ADMIN','RECEPCAO','MEDICO','PACIENTE') NOT NULL,
    nome VARCHAR(160) NOT NULL,
    login VARCHAR(254) COLLATE utf8mb4_bin NOT NULL UNIQUE,
    senha_hash VARCHAR(60) NOT NULL,
    crm VARCHAR(40) UNIQUE,
    especialidade VARCHAR(120),
    cpf CHAR(11) UNIQUE,
    nascimento DATE,
    telefone VARCHAR(30),
    FOREIGN KEY (id_hospital) REFERENCES hospitais(id),
    INDEX idx_usuarios_hospital_perfil (id_hospital, perfil),
    CHECK ((perfil = 'ADMIN' AND id_hospital IS NULL)
        OR (perfil <> 'ADMIN' AND id_hospital IS NOT NULL))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS agendamentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_hospital BIGINT NOT NULL,
    id_medico BIGINT NOT NULL,
    id_paciente BIGINT NOT NULL,
    inicio DATETIME NOT NULL,
    observacao VARCHAR(1000) NOT NULL DEFAULT '',
    status ENUM('AGENDADO','CANCELADO','CONCLUIDO') NOT NULL DEFAULT 'AGENDADO',
    FOREIGN KEY (id_hospital) REFERENCES hospitais(id),
    FOREIGN KEY (id_medico) REFERENCES usuarios(id),
    FOREIGN KEY (id_paciente) REFERENCES usuarios(id),
    INDEX idx_agendamentos_hospital_inicio (id_hospital, inicio),
    INDEX idx_agendamentos_medico_inicio (id_medico, inicio),
    INDEX idx_agendamentos_paciente_inicio (id_paciente, inicio)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS prontuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_agendamento BIGINT NOT NULL UNIQUE,
    relato TEXT NOT NULL,
    diagnostico TEXT NOT NULL,
    conduta TEXT NOT NULL,
    FOREIGN KEY (id_agendamento) REFERENCES agendamentos(id)
) ENGINE=InnoDB;

