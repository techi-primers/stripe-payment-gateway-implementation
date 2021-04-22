/**
 * Created by: nuwan_r
 * Created on: 4/21/2021
 **/
package com.stripe.payamentgateway.stripegateway.entity;

import com.stripe.model.*;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "stripe_customer_document")
@Data
public class StripeCustomer {

    String id;
    String systemUserId;
    String object;
    Long accountBalance;
    String businessVatId;
    Long created;
    String currency;
    ExpandableField<ExternalAccount> defaultSource;
    Boolean deleted;
    Boolean delinquent;
    String description;
    Discount discount;
    String email;
    Boolean livemode;
    Map<String, String> metadata;
    ShippingDetails shipping;
    ExternalAccountCollection sources;
    CustomerSubscriptionCollection subscriptions;
    /** @deprecated */
    @Deprecated
    CustomerCardCollection cards;
    /** @deprecated */
    @Deprecated
    String defaultCard;
    /** @deprecated */
    @Deprecated
    Customer.NextRecurringCharge nextRecurringCharge;
    /** @deprecated */
    @Deprecated
    Subscription subscription;
    /** @deprecated */
    @Deprecated
    Long trialEnd;
}