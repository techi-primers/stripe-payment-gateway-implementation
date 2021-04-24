/**
 * Created by: nuwan_r
 * Created on: 4/24/2021
 **/
package com.stripe.payamentgateway.stripegateway.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "test")
@Data
public class Test {

    @Id
    private String id;
    private String name;
}
