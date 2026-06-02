package br.com.fiap.agrotech.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "TAB_PREVISAO_SATELITE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrevisaoSatelite {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_satelite")
    @SequenceGenerator(name = "seq_satelite", sequenceName = "SEQ_PREVISAO_SATELITE", allocationSize = 1)
    @Column(name = "ID_PREVISAO")
    private Long id;

    @Column(name = "TXT_REGIAO", nullable = false)
    private String regiao;

    @Column(name = "BOL_CHUVA_IMINENTE", nullable = false)
    private Boolean chuvaIminente;

    @Column(name = "DAT_PREVISAO", nullable = false)
    private LocalDate dataPrevisao;
}