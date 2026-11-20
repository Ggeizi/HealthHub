CREATE TABLE IF NOT EXISTS prontuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_agendamento BIGINT NOT NULL UNIQUE,
    relato TEXT NOT NULL,
    diagnostico TEXT NOT NULL,
    conduta TEXT NOT NULL,
    FOREIGN KEY (id_agendamento) REFERENCES agendamentos(id)
) ENGINE=InnoDB;