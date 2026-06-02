package br.com.fiap.agrotech.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "TAB_REGISTRO_SOLO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroSolo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_solo")
    @SequenceGenerator(name = "seq_solo", sequenceName = "SEQ_REGISTRO_SOLO", allocationSize = 1)
    @Column(name = "ID_REGISTRO")
    private Long id;

    @Column(name = "NUM_UMIDADE", nullable = false)
    private Double umidade;

    @Column(name = "NUM_TEMPERATURA", nullable = false)
    private Double temperatura;

    @Column(name = "DAT_LEITURA", nullable = false)
    private LocalDateTime dataLeitura;

    @Column(name = "ID_DISPOSITIVO", nullable = false)
    private String dispositivoId;

    @ManyToOne
    @JoinColumn(name = "PREVISAO_ID")
    private PrevisaoSatelite previsao;
}
