/**
 * Created by: nuwan_r
 * Created on: 4/24/2021
 */
package com.stripe.payamentgateway.stripegateway.repository;

import com.stripe.payamentgateway.stripegateway.entity.ChargingRecordTrackErrorResponces;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargingRecordTrackErrorResponcesRepository  extends MongoRepository<ChargingRecordTrackErrorResponces,String> {
}
