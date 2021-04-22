/**
 * Created by: nuwan_r
 * Created on: 4/22/2021
 */
package com.stripe.payamentgateway.stripegateway.repository;

import com.stripe.payamentgateway.stripegateway.entity.StripeCustomer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StripeCustomerRepository extends MongoRepository<StripeCustomer,String> {
}
