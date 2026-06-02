package br.com.fiap.agrotech.controller;

import br.com.fiap.agrotech.dto.PrevisaoSateliteDto;
import br.com.fiap.agrotech.dto.RegistroSoloDto;
import br.com.fiap.agrotech.model.PrevisaoSatelite;
import br.com.fiap.agrotech.model.RegistroSolo;
import br.com.fiap.agrotech.repository.PrevisaoSateliteRepository;
import br.com.fiap.agrotech.repository.RegistroSoloRepository;
import br.com.fiap.agrotech.service.AgroInteligenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/agro")
@RequiredArgsConstructor
@Tag(name = "Agricultura de Precisão", description = "Endpoints para integração IoT e Satélites")
public class AgroController {

    private final AgroInteligenciaService inteligenciaService;
    private final PrevisaoSateliteRepository sateliteRepository;
    private final RegistroSoloRepository soloRepository;

    @PostMapping("/solo")
    @Operation(summary = "Recebe dados do ESP32", description = "Avalia umidade/temperatura e decide sobre bloqueio de rega")
    public ResponseEntity<String> receberDadosSolo(@Valid @RequestBody RegistroSoloDto dto) {
        String resultadoAnalise = inteligenciaService.processarAnaliseSolo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultadoAnalise);
    }

    @PostMapping("/satelite")
    @Operation(summary = "Atualiza previsão orbital", description = "Recebe dados climáticos da NASA/ESA sobre chuva iminente")
    public ResponseEntity<PrevisaoSatelite> atualizarDadosSatelite(@Valid @RequestBody PrevisaoSateliteDto dto) {
        PrevisaoSatelite novaPrevisao = new PrevisaoSatelite(
                null,
                dto.getRegiao(),
                dto.getChuvaIminente(),
                LocalDate.now()
        );
        PrevisaoSatelite salva = sateliteRepository.save(novaPrevisao);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    // ========== ENDPOINTS ==========
    @GetMapping("/solo")
    @Operation(summary = "Lista todos os registros de solo")
    public List<RegistroSolo> listarSolo() {
        return soloRepository.findAll();
    }

    @GetMapping("/solo/{id}")
    @Operation(summary = "Busca um registro de solo por ID")
    public ResponseEntity<RegistroSolo> buscarSolo(@PathVariable Long id) {
        return soloRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/solo/{id}")
    @Operation(summary = "Atualiza um registro de solo existente")
    public ResponseEntity<RegistroSolo> atualizarSolo(@PathVariable Long id, @Valid @RequestBody RegistroSoloDto dto) {
        return soloRepository.findById(id).map(registro -> {
            registro.setUmidade(dto.getUmidade());
            registro.setTemperatura(dto.getTemperatura());
            registro.setDispositivoId(dto.getDispositivoId());
            registro.setDataLeitura(LocalDateTime.now());
            return ResponseEntity.ok(soloRepository.save(registro));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/solo/{id}")
    @Operation(summary = "Remove um registro de solo")
    public ResponseEntity<?> deletarSolo(@PathVariable Long id) {
        if (soloRepository.existsById(id)) {
            soloRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
