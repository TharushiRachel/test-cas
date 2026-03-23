package com.itechro.cas.controller.covenant;

import com.itechro.cas.controller.BaseController;
import com.itechro.cas.exception.aop.ResponseExceptionHandler;
import com.itechro.cas.model.dto.covenants.CovenantDetailsFinacleDTO;
import com.itechro.cas.model.dto.covenants.LoadCovenantDataDTO;
import com.itechro.cas.model.security.CredentialsDTO;
import com.itechro.cas.service.covenant.CovenantService;
import com.itechro.cas.service.integration.IntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 *
 * @author tharushi
 */
@RestController
@RequestMapping(value = "${api.prefix}/covenant")
public class CovenantController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(CovenantController.class);

    @Autowired
    IntegrationService integrationService;

    @Autowired
    private CovenantService covenantService;

    @ResponseExceptionHandler
    @PostMapping(value = "/getCovenantsDetails", headers = "Accept=application/json")
    public ResponseEntity<CovenantDetailsFinacleDTO> getCovenantDetailsFromFinacle(@RequestBody LoadCovenantDataDTO request) throws Exception {

        CredentialsDTO credentialsDTO = getCredentialsDTO();

        LOG.info("START : Get covenant details from finacle : user {}", credentialsDTO.getUserID());

        CovenantDetailsFinacleDTO covenantDetailsFinacleDTO = covenantService.getCovenantDetailsFromFinacle(request, credentialsDTO);

        LOG.info("END : Get covenant details from finacle : user {}", credentialsDTO.getUserID());

        return new ResponseEntity<>(covenantDetailsFinacleDTO, HttpStatus.OK);
    }
}
