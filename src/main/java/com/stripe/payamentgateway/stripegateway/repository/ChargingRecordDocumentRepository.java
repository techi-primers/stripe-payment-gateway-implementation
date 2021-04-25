/**
 * Created by: nuwan_r
 * Created on: 4/22/2021
 **/
package com.stripe.payamentgateway.stripegateway.repository;

import com.stripe.payamentgateway.stripegateway.dto.ChargingResponseCustom;
import com.stripe.payamentgateway.stripegateway.entity.ChargingRecordDocument;
import com.stripe.payamentgateway.stripegateway.entity.StripeCustomer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChargingRecordDocumentRepository extends MongoRepository<ChargingRecordDocument,String> {

    @Query(value = "{'systemPaymentInfo.systemUserId' :?0, 'systemPaymentInfo.activeStatus':'?1'}")
    Optional<ChargingRecordDocument> getChargingRecordBySystemUserIdAndActiveStatus(String systemUserId, String activeStatus);

    @Query(value = "{'systemPaymentInfo.transactionExpiringStatus' :?0}")
    List<ChargingRecordDocument> findByTransactionExpiringStatus(String transactionExpiringStatus);

    @Query(value = "{'systemPaymentInfo.transactionExpiringStatus' :?0, 'systemPaymentInfo.activeStatus':'?1'}")
    List<ChargingRecordDocument> findByTransactionExpiringStatusAndDeactivateStatus(String transactionExpiringStatus,String activeStatus);

    @Query(value = "{'systemPaymentInfo.systemUserId' :?0}")
    List<ChargingRecordDocument> findBySystemUserId(String systemUserID);
}
