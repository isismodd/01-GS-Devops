package br.com.fiap.agrotech.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO para recebimento de dados telemétricos do solo via IOT")
public class RegistroSoloDto {

    @Schema(description = "Nível de umidade do solo em porcentagem", example = "35.5")
    @NotNull(message = "O nível de umidade é obrigatório.")
    @Min(value = 0, message = "A umidade não pode ser menor que 0%.")
    @Max(value = 100, message = "A umidade não pode ser maior que 100%.")
    private Double umidade;

    @Schema(description = "Temperatura do solo em graus Celsius", example = "26.0")
    @NotNull(message = "A temperatura do solo é obrigatória.")
    @Min(value = -10, message = "Temperatura abaixo do limite operacional.")
    @Max(value = 60, message = "Temperatura acima do limite operacional.")
    private Double temperatura;

    @Schema(description = "ID único do dispositivo IoT na fazenda", example = "ESP32-FAZENDA-01")
    @NotBlank(message = "O identificador do dispositivo IoT é obrigatório.")
    private String dispositivoId;

    @Schema(description = "ID da previsão satélite associada", example = "1")
    private Long previsaoId;
}
