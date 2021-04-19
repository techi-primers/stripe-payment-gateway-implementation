package com.stripe.payamentgateway.stripegateway.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.stripe.payamentgateway.stripegateway.util.EnumUtils;
import org.springframework.util.StringUtils;

public enum CardCompany {

    VISA, AMEX, MASTERCARD, DISCOVER;

    @JsonCreator
    public static CardCompany fromValue(String value) {
        return EnumUtils.getEnumFromString(CardCompany.class, value.toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return StringUtils.capitalize(name());
    }
}
