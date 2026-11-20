CREATE TABLE IF NOT EXISTS agendamentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_hospital BIGINT NOT NULL,
    id_medico BIGINT NOT NULL,
    id_paciente BIGINT NOT NULL,
    inicio DATETIME NOT NULL,
    observacao VARCHAR(1000) NOT NULL DEFAULT '',
    status ENUM('AGENDADO', 'CANCELADO', 'CONCLUIDO')
        NOT NULL DEFAULT 'AGENDADO',

    FOREIGN KEY (id_hospital) REFERENCES hospitais(id),
    FOREIGN KEY (id_medico) REFERENCES usuarios(id),
    FOREIGN KEY (id_paciente) REFERENCES usuarios(id)

) ENGINE=InnoDB;