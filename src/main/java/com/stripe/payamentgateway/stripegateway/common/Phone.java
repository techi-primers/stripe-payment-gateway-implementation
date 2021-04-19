package com.stripe.payamentgateway.stripegateway.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.stripe.payamentgateway.stripegateway.enums.PhoneType;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Phone {

    @Indexed
    private String countryCode;
    @Indexed
    private String number;
    private PhoneType type;

}
