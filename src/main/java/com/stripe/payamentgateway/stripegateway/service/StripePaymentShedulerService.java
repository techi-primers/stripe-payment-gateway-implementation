/**
 * Created by: nuwan_r
 * Created on: 4/24/2021
 **/
package com.stripe.payamentgateway.stripegateway.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.payamentgateway.stripegateway.controller.ChargeRequest;
import com.stripe.payamentgateway.stripegateway.dto.ChargeResponseSource;
import com.stripe.payamentgateway.stripegateway.dto.ChargingResponseCustom;
import com.stripe.payamentgateway.stripegateway.dto.PaymentSuccessDto;
import com.stripe.payamentgateway.stripegateway.dto.SystemPaymentInfo;
import com.stripe.payamentgateway.stripegateway.entity.ChargingRecordDocument;
import com.stripe.payamentgateway.stripegateway.entity.ChargingRecordTrackErrorResponces;
import com.stripe.payamentgateway.stripegateway.repository.ChargingRecordDocumentRepository;
import com.stripe.payamentgateway.stripegateway.repository.ChargingRecordTrackErrorResponcesRepository;
import org.apache.catalina.session.JDBCStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Service
public class StripePaymentShedulerService {

    private Logger logger = LogManager.getLogger(StripePaymentShedulerService.class);

    @Autowired
    private ChargingRecordDocumentRepository chargingRecordDocumentRepository;
    @Autowired
    private ChargeProcessService chargeProcessService;
    private static final String TRANSACTION_NOT_EXPIRED = "NOT_EXPIRED";
    private static final String TRANSACTION_EXPIRED = "EXPIRED";
    public static final String PAYMENT_ACTIVE = "ACTIVE";
    public static final String PAYMENT_DE_ACTIVE = "De-ACTIVE";
    public static final String PAY_RECURSIVELY = "RECURSIVE Payment";
    public static final String PAYMENT_FAILED = "failed";
    public static final String PAYMENT_SUCCESS = "succeeded";

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @Autowired
    private ChargingRecordTrackErrorResponcesRepository chargingRecordTrackErrorResponcesRepository;

    /*Fires at 12 PM every day:*/
    //@Scheduled(cron = "0 48 12 * * *", zone = "Asia/Colombo")
    //@Scheduled(cron = "0 17 12 * * ?")
    @Scheduled(fixedDelayString = "${payment.purge.frequency}",
            initialDelayString = "${payment.purge.initial-delay}")
    @Transactional
    public void paymentShedular()  {

        Calendar cal = Calendar.getInstance();

        logger.info("################# Payment Shedule Process Started ################# " + cal.getTime());

        logger.info("checkSubscriptionExpired System Users " + cal.getTime());
        Set<ChargingRecordDocument> subscriptionExpiredRec = checkSubscriptionExpiredUsers();

        logger.info("doChargingProcess " + cal.getTime());
        doChargingProcess(subscriptionExpiredRec);

        logger.info("################## Payment Shedule Process Terminated ################" + cal.getTime());

    }

    @Transactional
    public void doChargingProcess(Set<ChargingRecordDocument> subscriptionExpiredRec) {


        if (Optional.ofNullable(subscriptionExpiredRec).filter(y -> y.size() > 0).isPresent()) {

            for (ChargingRecordDocument trialRec : subscriptionExpiredRec) {
                logger.info("Charging process started for " + trialRec.getSystemPaymentInfo().getSystemUserId());
                try{
                    recurrencePaymentProcess(trialRec);
                } catch (CardException e) {
                    // Since it's a decline, CardException will be caught
                    logger.error("Status is: " + e.getCode());
                    logger.error("Message is: " + e.getMessage());

                } catch (RateLimitException e) {
                    // Too many requests made to the API too quickly
                    logger.error("Status is: " + e.getCode());
                    logger.error("Message is: " + e.getMessage());
                } catch (InvalidRequestException e) {
                    // Invalid parameters were supplied to Stripe's API
                    logger.error("Status is: " + e.getCode());
                    logger.error("Message is: " + e.getMessage());
                } catch (AuthenticationException e) {
                    // Authentication with Stripe's API failed
                    // (maybe you changed API keys recently)
                    logger.error("Status is: " + e.getCode());
                    logger.error("Message is: " + e.getMessage());

                } catch (StripeException e) {
                    // Display a very generic error to the user, and maybe send
                    // yourself an email
                    logger.error("Status is: " + e.getCode());
                    logger.error("Message is: " + e.getMessage());
                } catch (Exception e) {
                    // Something else happened, completely unrelated to Stripe
                    logger.error("Message is: " + e.getMessage());
                    logger.info("doChargingProcess-subscriptionExpiredChargingRecords: catch"+trialRec.getSystemPaymentInfo().getSystemUserId());
                }

            }

        } else {
            logger.info("No Charging expird record....");
        }
    }

    private void recurrencePaymentProcess(ChargingRecordDocument expiredChargingRec) throws Exception {

        String systemUserId = expiredChargingRec.getSystemPaymentInfo().getSystemUserId();
        logger.info("System User "+ systemUserId);
        String customerId = expiredChargingRec.getSystemPaymentInfo().getCustomerId();
        logger.info("Stripe Customer Id "+ customerId);
        Integer amount = expiredChargingRec.getChargingResponseCustom().getAmount();

        Stripe.apiKey = stripeSecretKey;

        //retrive customer by if
        Customer customerFromStripe =  Customer.retrieve(expiredChargingRec.getSystemPaymentInfo().getCustomerId());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Customer customerFromStrped = getCustomerByCustomerId(customerId);
        if(customerFromStrped.getId().equals(customerFromStripe.getId())) {

            logger.info("Stripe Customer Id Matched!");
            chargingProcessStarter(amount , systemUserId, customerFromStrped);

        } else {

            logger.error("Stripe Customer Id Missed Match!");

        }


    }

    private void chargingProcessStarter( Integer amount, String systemUserId, Customer customerFromStrped) throws StripeException , Exception {

        logger.info("inside the chargingProcessStarter");
        Map<String, Object> chargePrams = createChargeParams(amount, customerFromStrped);
        Charge recursiveChargeResponse   = Charge.create(chargePrams);

        if(recursiveChargeResponse.getStatus().equals(PAYMENT_SUCCESS)) {
            logger.info("Stripe Payment has succeeded from stripe end!");
            // save charge response locally
            ChargingRecordDocument crd = this.chargeProcessService.createChargingRecordDoc(systemUserId, customerFromStrped, recursiveChargeResponse);

            ChargingRecordDocument chargingrecordsavedObj = chargingRecordDocumentRepository.save(crd);
            Optional.ofNullable(chargingrecordsavedObj).map(rec -> {

                if(rec.getChargingResponseCustom()!=null && rec.getSystemPaymentInfo()!=null) {
                    logger.info("Recursion Payment Success "+recursiveChargeResponse.getId());
                    /*generate end sucess result page infor*/
                    ModelAndView view = new ModelAndView();
                    /*invoice monthly*/
                    logger.info("Amount : "+rec.getChargingResponseCustom().getAmount()/100);
                    logger.info("Description : "+rec.getChargingResponseCustom().getDescription());
                    logger.info("Email : "+rec.getChargingResponseCustom().getSource().getName());
                    logger.info("Status : "+rec.getChargingResponseCustom().getStatus());
                    logger.info("System User : "+systemUserId);
                    logger.info("Payment Charge Id : "+rec.getChargingResponseCustom().getId());

                }
                return rec;
            }).orElseThrow(() -> new Exception("Error From Persiting charging record"));

        } else{

            logger.error("Stripe Charging Respose status not success!");
            ChargingRecordTrackErrorResponces crd = this.chargeProcessService.createChargingFailedRecordDoc(systemUserId, customerFromStrped, recursiveChargeResponse);
            this.chargingRecordTrackErrorResponcesRepository.save(crd);
        }
    }

    private Map<String, Object> createChargeParams(Integer amount, Customer customerFromStrped) {
        Map<String,Object> chargePrams = new HashMap<String,Object>();

        chargePrams.put("amount", amount);
        chargePrams.put("currency", ChargeRequest.Currency.USD);
        chargePrams.put("description",PAY_RECURSIVELY);

        chargePrams.put("customer",customerFromStrped.getId());
        return chargePrams;
    }

    private Customer getCustomerByCustomerId(String customerId) throws Exception {
        return Optional.ofNullable(Customer.retrieve(customerId)).orElseThrow(() -> new Exception("Cannot find Customer"));
    }

    @Transactional
    public Set<ChargingRecordDocument> checkSubscriptionExpiredUsers() {

        /*load trial expired records from charging records  */
        /*update expired records status*/
        /*return expired records*/

        Set<ChargingRecordDocument> subscriptionExpiredTenants = new HashSet<>();

        this.chargingRecordDocumentRepository.findByTransactionExpiringStatusAndDeactivateStatus(TRANSACTION_NOT_EXPIRED,PAYMENT_ACTIVE).stream()
                .forEach(expiredRecord -> {
                    if (this.checkPaymentNotExpired(expiredRecord.getSystemPaymentInfo().getSubscriptionEndDate()).equals(false)) {
                        try {
                            expiredRecord.getSystemPaymentInfo().setTransactionExpiringStatus(TRANSACTION_EXPIRED);
                            this.chargingRecordDocumentRepository.save(expiredRecord);
                            subscriptionExpiredTenants.add(expiredRecord);
                            logger.info("Expired System User " + expiredRecord.getSystemPaymentInfo().getSystemUserId());
                        } catch (Exception exception) {
                            logger.error("check Subscription Expired System User: catch");
                            exception.printStackTrace();
                        }
                    }

                });
        return subscriptionExpiredTenants;
    }

    public Boolean checkPaymentNotExpired(Date ExpiringDate) {

        Date date = new Date();
        if ((ExpiringDate.compareTo(date)) > 0) {
            /*not expired*/
            return true;
        } else {
            return false;
        }

    }
}
