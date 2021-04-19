package com.stripe.payamentgateway.stripegateway.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.stripe.payamentgateway.stripegateway.enums.CardCompany;
import com.stripe.payamentgateway.stripegateway.enums.PaymentType;
import com.stripe.payamentgateway.stripegateway.enums.Status;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentCriteria {

    private PaymentType type;
    private Status status;
    private Boolean isDefault;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime activeFrom; //TODO dicuss

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime activeEnd; // TODO discuss

    private Boolean useForRecurring;
    private CardCompany cardCompany;
    private String tokenID;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime tokenExpDt;
    private Long cardNo;
    private short cvv;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expiryDt;
    private Address billAddress;
}
