package br.com.fiap.agrotech.repository;

import br.com.fiap.agrotech.model.PrevisaoSatelite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PrevisaoSateliteRepository extends JpaRepository<PrevisaoSatelite, Long> {
    Optional<PrevisaoSatelite> findFirstByRegiaoOrderByDataPrevisaoDesc(String regiao);
}