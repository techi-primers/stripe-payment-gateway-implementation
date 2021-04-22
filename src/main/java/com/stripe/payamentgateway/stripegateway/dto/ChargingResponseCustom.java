/**
 * Created by: nuwan_r
 * Created on: 4/21/2021
 **/
package com.stripe.payamentgateway.stripegateway.dto;

import com.stripe.model.Customer;
import lombok.Data;

@Data
public class ChargingResponseCustom {

    private String id;
    private String object;
    private Integer amount;
    private String balance_transaction;
    private BillingDetails billing_details;
    private String currency;
    private Customer customer;
    private String description;
    private String failure_code;
    private String failure_message;
    private Boolean livemode;
    private Boolean paid;
    private String payment_method;
    private PaymentMethodDetails payment_method_details;
    private String receipt_email;
    private String receipt_url;
    private String receipt_number;
    private String status;  //succeeded failed
}
