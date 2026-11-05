package com.healthhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.healthhub.model.Teste;

public interface TesteRepositorio extends JpaRepository<Teste, Long> {

}