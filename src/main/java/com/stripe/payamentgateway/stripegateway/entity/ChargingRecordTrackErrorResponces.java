/**
 * Created by: nuwan_r
 * Created on: 4/24/2021
 **/
package com.stripe.payamentgateway.stripegateway.entity;

import com.stripe.payamentgateway.stripegateway.dto.ChargingResponseCustom;
import com.stripe.payamentgateway.stripegateway.dto.SystemPaymentInfo;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Document(collection = "charging_record_track_error_document")
@Data
public class ChargingRecordTrackErrorResponces {

    @Id
    private String chargingRecordId;
    private SystemPaymentInfo systemPaymentInfo;
    private ChargingResponseCustom chargingResponseCustom;
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date lastModifiedDate;

}
