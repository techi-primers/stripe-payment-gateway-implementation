/**
 * Created by: nuwan_r
 * Created on: 4/24/2021
 */
package com.stripe.payamentgateway.stripegateway.repository;

import com.stripe.payamentgateway.stripegateway.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends MongoRepository<Test,String > {
}
