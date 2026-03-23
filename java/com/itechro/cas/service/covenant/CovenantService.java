package com.itechro.cas.service.covenant;

import com.itechro.cas.model.dto.covenants.CovenantDataDTO;
import com.itechro.cas.model.dto.covenants.CovenantDetailsFinacleDTO;
import com.itechro.cas.model.dto.covenants.LoadCovenantDataDTO;
import com.itechro.cas.model.security.CredentialsDTO;
import com.itechro.cas.service.integration.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CovenantService {

    @Autowired
    private IntegrationService integrationService;

    public CovenantDetailsFinacleDTO getCovenantDetailsFromFinacle(LoadCovenantDataDTO loadCovenantDataDTO, CredentialsDTO credentialsDTO) throws Exception{

        CovenantDetailsFinacleDTO covenantDetailsFinacleDTO = integrationService.getCovenantDetailsFromFinacle(loadCovenantDataDTO, credentialsDTO);

        if (covenantDetailsFinacleDTO != null && covenantDetailsFinacleDTO.getCovenant() != null) {
            List<CovenantDataDTO> covenantList = covenantDetailsFinacleDTO.getCovenant();

            covenantList.sort((c1, a1) -> {
                boolean isc_other = c1.getCovenantInq().stream().anyMatch(cov -> cov.getCovCod().endsWith("_OTH"));
                boolean isa_other = a1.getCovenantInq().stream().anyMatch(cov -> cov.getCovCod().endsWith("_OTH"));

                if (isc_other && !isa_other) return 1;
                if (!isc_other && isa_other) return -1;
                return 0;
            });

            covenantDetailsFinacleDTO.setCovenant(covenantList);
        }

        return covenantDetailsFinacleDTO;

    }

}
