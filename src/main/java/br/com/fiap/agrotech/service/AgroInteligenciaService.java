package br.com.fiap.agrotech.service;

import br.com.fiap.agrotech.dto.RegistroSoloDto;
import br.com.fiap.agrotech.model.PrevisaoSatelite;
import br.com.fiap.agrotech.model.RegistroSolo;
import br.com.fiap.agrotech.repository.PrevisaoSateliteRepository;
import br.com.fiap.agrotech.repository.RegistroSoloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgroInteligenciaService {

    private final RegistroSoloRepository soloRepository;
    private final PrevisaoSateliteRepository sateliteRepository;

    public String processarAnaliseSolo(RegistroSoloDto dto) {

      RegistroSolo registro = new RegistroSolo(
         null,
         dto.getUmidade(),
         dto.getTemperatura(),
         LocalDateTime.now(),
         dto.getDispositivoId(),
         null
     );
        soloRepository.save(registro);

        Optional<PrevisaoSatelite> previsaoOpt = sateliteRepository.findFirstByRegiaoOrderByDataPrevisaoDesc("Setor_A_Principal");

        if (previsaoOpt.isPresent()) {
            PrevisaoSatelite previsao = previsaoOpt.get();
            if (dto.getUmidade() < 40.0 && previsao.getChuvaIminente()) {
                return "SOLICITAÇÃO DE REGA RECEBIDA. Ação: REGA BLOQUEADA. Motivo: Dados orbitais indicam chuva iminente.";
            }
        }

        if (dto.getUmidade() < 40.0) {
            return "Ação: SISTEMA DE IRRIGAÇÃO ATIVADO. Solo seco e sem previsão de chuva.";
        }

        return "Ação: SISTEMA EM ESPERA. Umidade adequada (" + dto.getUmidade() + "%).";
    }
}
