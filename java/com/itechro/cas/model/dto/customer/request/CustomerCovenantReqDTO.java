package com.itechro.cas.model.dto.customer.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itechro.cas.commons.constants.AppsConstants;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 *
 *
 * @author tharushi
 */
@Data
public class CustomerCovenantReqDTO implements Serializable {

    //private Integer customerCovenantId;
    @JsonProperty("RequestUUID")
    private String RequestUUID;

    private String custId;

    private String casReference;

    private List<CustomerCovenantDetailsDTO> covenantDetails;

    private String createdUserDisplayName;
    //private String userName;

    private String createdBy;

    private Date createdDate;

    private Integer facilityPaperID;

    private AppsConstants.CovenantStatusOnDisbursement disbursementType;

    private String noFrequencyDueDate;
}
