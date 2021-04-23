/**
 * Created by: nuwan_r
 * Created on: 4/23/2021
 **/
package com.stripe.payamentgateway.stripegateway.dto;

import lombok.Data;

@Data
public class PaymentSuccessDto {

    private String systemUserId;
    private String status;
    private Integer amount;
    private String receiptEmail;
    private String description;
    private String chargingResId;
}
