package br.com.fiap.agrotech.repository;

import br.com.fiap.agrotech.model.RegistroSolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroSoloRepository extends JpaRepository<RegistroSolo, Long> {}