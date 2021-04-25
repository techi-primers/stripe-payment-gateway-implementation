/**
 * Created by: nuwan_r
 * Created on: 4/21/2021
 **/
package com.stripe.payamentgateway.stripegateway.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SystemPaymentInfo {

    private String systemUserId; // business partner unique id
    private String customerId;   // stripe customer id
    private String subscriptionStatus; // success or failed
    private Date  subscriptionStartDate;
    private Date subscriptionEndDate;
    private String SubscriptionDuration; // 1 Month 1 Year ...
    private String activeStatus; // ACTIVE DE-ACTIVE
    private String transactionExpiringStatus = "NOT_EXPIRED";

}
