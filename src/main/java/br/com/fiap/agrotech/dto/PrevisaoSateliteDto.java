package br.com.fiap.agrotech.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO para recepção de dados orbitais da NASA/ESA")
public class PrevisaoSateliteDto {

    @Schema(description = "Região de análise do satélite", example = "Setor_A_Principal")
    @NotBlank(message = "A região de monitoramento orbital é obrigatória.")
    private String regiao;

    @Schema(description = "Indicador booleano de chuva para as próximas horas", example = "true")
    @NotNull(message = "O status de chuva iminente deve ser informado.")
    private Boolean chuvaIminente;
}

