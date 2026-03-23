package com.itechro.cas.model.dto.customer.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itechro.cas.commons.constants.AppsConstants;
import lombok.Data;

import java.util.Date;

@Data
public class CustomerCovenantDetailsDTO {
    @JsonProperty("covenant_Code")
    private String covenant_Code;

    private String covenant_Description;

    private String covenant_Frequency;

    private Date covenant_Due_Date;

    private AppsConstants.CovenantStatusOnDisbursement disbursementType;

    private String noFrequencyDueDate;

    private AppsConstants.CovenantApplicableType applicableType;
}
