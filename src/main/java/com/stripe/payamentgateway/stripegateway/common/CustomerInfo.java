package com.stripe.payamentgateway.stripegateway.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.stripe.payamentgateway.stripegateway.enums.PaymentTerm;
import com.stripe.payamentgateway.stripegateway.enums.PaymentType;
import com.stripe.payamentgateway.stripegateway.enums.Status;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerInfo {

    private String country; //Customer Country
    private String companyNbr;
    private String customerGroup;
    private Status status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime activeFrom; // TODO discuss

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime activeEnd; // TODO discuss
    private String email;
    private String sourceSystemNbr;
    private String distributionChannel;
    private String creditStatus;
    private String dunninglevel; // TODO dicuss can be enum
    private Boolean recurringBill;
    private PaymentType preferredPayment;
    private PaymentTerm paymentTerms;
    private String invoiceType; //todo can be enum;
    private String billPreference; //todo can be enum;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate nextBillDt; //todo can be enum;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate lastBillDt; //todo can be enum;
    private String taxCustomerClassification;
    private Integer reconGl;
    private Integer unBilledArGl;

    // TODO make contact Object
    private List<Address> addresses;
    private List<Phone> contacts;
    private List<PaymentCriteria> customerPayCriteria;
}
