package com.stripe.payamentgateway.stripegateway.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
public class AbstractDocument {

  @Id
  private String id;

  @CreatedDate
  private LocalDateTime createdDate;

  @LastModifiedDate
  private LocalDateTime updatedDate;

  // @CreatedBy private String createdBy;

  // @LastModifiedBy private String updatedBy;
}
