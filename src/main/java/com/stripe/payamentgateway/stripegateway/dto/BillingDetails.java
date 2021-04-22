/**
 * Created by: nuwan_r
 * Created on: 4/21/2021
 **/
package com.stripe.payamentgateway.stripegateway.dto;

import com.stripe.model.Address;
import lombok.Data;

@Data
public class BillingDetails {

    private String email;
    private String name;
    private String phone;
    private Address address;
}
