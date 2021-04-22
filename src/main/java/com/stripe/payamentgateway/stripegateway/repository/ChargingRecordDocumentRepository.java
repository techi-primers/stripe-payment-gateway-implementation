/**
 * Created by: nuwan_r
 * Created on: 4/22/2021
 **/
package com.stripe.payamentgateway.stripegateway.repository;

import com.stripe.payamentgateway.stripegateway.dto.ChargingResponseCustom;
import com.stripe.payamentgateway.stripegateway.entity.ChargingRecordDocument;
import com.stripe.payamentgateway.stripegateway.entity.StripeCustomer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChargingRecordDocumentRepository extends MongoRepository<ChargingRecordDocument,String> {
}
