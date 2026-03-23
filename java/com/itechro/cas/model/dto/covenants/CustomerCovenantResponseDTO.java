package com.itechro.cas.model.dto.covenants;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 *
 *
 * @author tharushi
 */

@Data
public class CustomerCovenantResponseDTO {

    @JsonProperty("Status")
    private String Status;

    @JsonProperty("covenants")
    private CovenantLevelDTO covenants;

}
