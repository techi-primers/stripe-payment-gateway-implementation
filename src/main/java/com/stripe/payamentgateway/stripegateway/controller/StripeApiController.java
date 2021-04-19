/**
 * Created by: nuwan_r
 * Created on: 4/18/2021
 **/
package com.stripe.payamentgateway.stripegateway.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.payamentgateway.stripegateway.dto.FirstPaymentDto;
import com.stripe.payamentgateway.stripegateway.dto.StripeTokenObjDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/stripe-api")
@CrossOrigin(origins = "http://localhost:4200/strip")
public class StripeApiController {

    @PostMapping("/doFirstTimePayment")
    public ResponseEntity doFirstPayment(@RequestBody StripeTokenObjDto stripeTokenObjDto) throws StripeException {

        Stripe.apiKey = "sk_test_51IZ2zyB7fkZfvOQILz1ofC7nVyozMMi5bxbFz10AftRj1cglU0fpRESXuYDRtKUCQDresRwUa1aaQlbZq3xhWlnc00faawks0R";

        Map<String,Object> customerParameter = new HashMap<>();
        customerParameter.put("email","b@gmail.com");
        //customerParameter.put("id","1232");

        Customer customer = Customer.create(customerParameter);
                            //retrive customer by if
                             Customer cusRetri =        Customer.retrieve("cus_JK8tBeK0zOr4CB");
                             Gson gson = new GsonBuilder().setPrettyPrinting().create();


        return new ResponseEntity(gson.toJson(cusRetri), HttpStatus.ACCEPTED);
    }

    @PostMapping("/addCardToCustomer")
    public ResponseEntity addCardIntoCustomer(@RequestBody FirstPaymentDto firstPaymentDto) throws StripeException {

        Stripe.apiKey = "sk_test_51IZ2zyB7fkZfvOQILz1ofC7nVyozMMi5bxbFz10AftRj1cglU0fpRESXuYDRtKUCQDresRwUa1aaQlbZq3xhWlnc00faawks0R";

        //retrive customer by if
        Customer cusRetri =        Customer.retrieve("cus_JK8tBeK0zOr4CB");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // add card into customer
        Map<String,Object> source = new HashMap<String, Object>();
        source.put("source",firstPaymentDto.getToken());
        cusRetri.getSources().create(source);
        return new ResponseEntity(gson.toJson(cusRetri), HttpStatus.ACCEPTED);
    }

    @GetMapping("/sayHello")
    public ResponseEntity sayHello () {
        return new ResponseEntity("hello world",HttpStatus.OK);
    }
}
