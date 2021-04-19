package com.stripe.payamentgateway.stripegateway.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.stripe.payamentgateway.stripegateway.common.CustomerInfo;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessPartner extends AbstractDocument {

    private String docType;
    private Long docNbr;
    private Long vbBPNbr;
    private String firstName;
    private String lastName;
    private Boolean isCustomer;
    private Boolean isVendor;
    private Boolean isDealer;
    private Boolean isEmployee;
    private List<CustomerInfo> customerInfo;
}
