/**
 * Created by: nuwan_r
 * Created on: 4/22/2021
 **/
package com.stripe.payamentgateway.stripegateway.service;

import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccount;
import com.stripe.payamentgateway.stripegateway.controller.ChargeRequest;
import com.stripe.payamentgateway.stripegateway.dto.ChargingResponseCustom;
import com.stripe.payamentgateway.stripegateway.dto.SystemPaymentInfo;
import com.stripe.payamentgateway.stripegateway.entity.ChargingRecordDocument;
import com.stripe.payamentgateway.stripegateway.entity.StripeCustomer;
import com.stripe.payamentgateway.stripegateway.repository.ChargingRecordDocumentRepository;
import com.stripe.payamentgateway.stripegateway.repository.StripeCustomerRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChargeProcessService {

    public static final String END_VIEW_DESCRIPTION_V1 = "More Information Need For Initiate Charging Process ";
    public static final String END_VIEW_DESCRIPTION_V2 ="Stripe Customer Id Missed Match";
    public static final String PAYMENT_FAILED = "failed";
    public static final String PAYMENT_SUCCESS = "succeeded";
    public static final String PAY_FOR_FIRST_MONTH = "First Month Payment";
    public static final String PAY_RECURSIVELY = "RECURSIVE Payment";
    public static final String PAYMENT_EXPIRING_AFTER = "1 Month"; // 1 Month 1 Year
    private static final String END_VIEW_DESCRIPTION_V3 = "Isssue With  Internal Data Saving ";
    private static final String END_VIEW_DESCRIPTION_V4 = "Stripe Charging Response Not Success";
    private static final String END_VIEW_DESCRIPTION_V5 = "Issue When Creating Stripe Customer";
    @Autowired
    private StripeService stripeService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private StripeCustomerRepository stripedCustomerRepository;
    @Autowired
    private ChargingRecordDocumentRepository chargingResponseCustomRepository;

    private Logger logger = LogManager.getLogger(ChargeProcessService.class);

    public String doInitialChargeProcess(Model model, String stripeEmail, Integer amount,String  stripToken,String systemUserId) {
        if(stripToken!=null && amount!=null && stripToken!=null && systemUserId!=null) {
            logger.info("Charging process initiated : systemUserId :"+systemUserId+" Amount : "+amount +" stripe email :"+stripeEmail);

            // create customer
            // adding default card to the customer itself
            Map<String, Object> customerParameter = createCustomerObj(stripeEmail,stripToken);

            try {
                // Use Stripe's library to make requests...
                Customer customer = Customer.create(customerParameter);
                if(Optional.ofNullable(customer).isPresent()) {
                    logger.info("Stripe Customer created! ");
                    // save customer object with system user
                    StripeCustomer stripeCustomer = saveStripedCustomerResponse(systemUserId, customer);

                    Customer customerFromStrped = getCustomerByCustomerId(stripeCustomer.getId());
                    if(stripeCustomer.getId().equals(customerFromStrped.getId())) {
                        logger.info("Stripe Customer Id Matched!");
                        logger.info("Stripe Customer Id : "+customerFromStrped.getId());

                        return chargingProcessStarter(model, amount, systemUserId, customerFromStrped);


                    } else {
                        logger.warn("Stripe Customer Id Missed Match!");
                        createErrorEndResultModel(model,END_VIEW_DESCRIPTION_V2);
                        return "result";
                    }


                } else {
                    logger.warn("Stripe Customer not created!");
                    createErrorEndResultModel(model,END_VIEW_DESCRIPTION_V5);
                    return "result";
                }
            } catch (CardException e) {
                // Since it's a decline, CardException will be caught
                logger.error("Status is: " + e.getCode());
                logger.error("Message is: " + e.getMessage());
                createErrorEndResultModel(model,e.getMessage());
                return "result";
            } catch (RateLimitException e) {
                // Too many requests made to the API too quickly
                logger.error("Status is: " + e.getCode());
                logger.error("Message is: " + e.getMessage());
                createErrorEndResultModel(model,e.getMessage());
                return "result";
            } catch (InvalidRequestException e) {
                // Invalid parameters were supplied to Stripe's API
                logger.error("Status is: " + e.getCode());
                logger.error("Message is: " + e.getMessage());
                createErrorEndResultModel(model,e.getMessage());
                return "result";
            } catch (AuthenticationException e) {
                // Authentication with Stripe's API failed
                // (maybe you changed API keys recently)
                logger.error("Status is: " + e.getCode());
                logger.error("Message is: " + e.getMessage());
                createErrorEndResultModel(model,e.getMessage());
                return "result";
            } catch (StripeException e) {
                // Display a very generic error to the user, and maybe send
                // yourself an email
                logger.error("Status is: " + e.getCode());
                logger.error("Message is: " + e.getMessage());
                createErrorEndResultModel(model,e.getMessage());
                return "result";
            } catch (Exception e) {
                // Something else happened, completely unrelated to Stripe
                logger.error("Message is: " + e.getMessage());
              //  createErrorEndResultModel(model,e.getMessage());
                model.addAttribute("status",PAYMENT_FAILED);
                model.addAttribute("Description", "description");
                return "result";
            }


        } else {
            logger.info("Inputs for  charging process  missing");
            createErrorEndResultModel(model,END_VIEW_DESCRIPTION_V1);
            return "result";
        }
    }

    private String chargingProcessStarter(Model model, Integer amount, String systemUserId, Customer customerFromStrped) throws StripeException , Exception {

        logger.info("inside the chargingProcessStarter");
        Map<String, Object> chargePrams = createChargeParams(amount, customerFromStrped);
        Charge firstMOnthChargeResponse   = Charge.create(chargePrams);

        if(firstMOnthChargeResponse.getStatus().equals(PAYMENT_SUCCESS)) {
            logger.info("Stripe Payment has succeeded from stripe end!");
            // save charge response locally
            ChargingRecordDocument crd = createChargingRecordDoc(systemUserId, customerFromStrped, firstMOnthChargeResponse);

            try {
                ChargingRecordDocument chargingrecordsavedObj = chargingResponseCustomRepository.save(crd);
                return Optional.ofNullable(chargingrecordsavedObj).map(rec -> {

                    if(rec.getChargingResponseCustom()!=null && rec.getSystemPaymentInfo()!=null) {
                        logger.info("Fisrt Time Payment Success "+firstMOnthChargeResponse.getId());
                        /*generate end sucess result page infor*/
                        model.addAttribute("System User Id", firstMOnthChargeResponse.getId());
                        model.addAttribute("Status", rec.getChargingResponseCustom().getStatus());
                        model.addAttribute("Amount", rec.getChargingResponseCustom().getAmount());
                        model.addAttribute("Receipt Email", rec.getChargingResponseCustom().getReceipt_email());
                        model.addAttribute("Description", rec.getChargingResponseCustom().getDescription());

                        return "result";
                    }else {
                        logger.error(END_VIEW_DESCRIPTION_V3);
                        createErrorEndResultModel(model,END_VIEW_DESCRIPTION_V3);
                        return "result";

                    }
                }).orElseThrow(() -> new Exception("Error From Persiting charging record"));
            }catch (Exception e ) {
                logger.error("Error from saving charging Object..");
                createErrorEndResultModel(model,END_VIEW_DESCRIPTION_V3);
                return "result";
            }
        } else{
            logger.error("Stripe Charging Respose not success!");
            createErrorEndResultModel(model,END_VIEW_DESCRIPTION_V4);
            return "result";
        }
    }

    private ChargingRecordDocument createChargingRecordDoc(String systemUserId, Customer customerFromStrped, Charge firstMOnthChargeResponse) {
        logger.info("creating charging record document");
        ChargingRecordDocument crd = new ChargingRecordDocument();
        SystemPaymentInfo spi = new SystemPaymentInfo();
        spi.setCustomerId(customerFromStrped.getId());
        spi.setSystemUserId(systemUserId);
        spi.setSubscriptionStatus(PAYMENT_SUCCESS);
        spi.setSubscriptionDuration(PAYMENT_EXPIRING_AFTER);
        Date paymentWillExpireOn = calculatePaymentExpiredDate(PAYMENT_EXPIRING_AFTER);
        logger.info("Subscription Ending Date "+paymentWillExpireOn);
        spi.setSubscriptionEndDate(paymentWillExpireOn);
        final Calendar cal = Calendar.getInstance();
        spi.setSubscriptionStartDate(cal.getTime());
        crd.setChargingResponseCustom(this.modelMapper.map(firstMOnthChargeResponse, ChargingResponseCustom.class));
        return crd;
    }

    private Map<String, Object> createChargeParams(Integer amount, Customer customerFromStrped) {
        Map<String,Object> chargePrams = new HashMap<String,Object>();

        chargePrams.put("amount", amount);
        chargePrams.put("currency", ChargeRequest.Currency.USD);
        chargePrams.put("description",PAY_FOR_FIRST_MONTH);

        chargePrams.put("customer",customerFromStrped.getId());
        return chargePrams;
    }

    private StripeCustomer saveStripedCustomerResponse(String systemUserId, Customer customer) {
        logger.info("Stripe Customer Saving! ");
        StripeCustomer stripeCustomer = new StripeCustomer();
        stripeCustomer.setSystemUserId(systemUserId);
        StripeCustomer stripeCustomerMapped =  this.modelMapper.map(customer,StripeCustomer.class);
        return this.stripedCustomerRepository.save(stripeCustomer);
    }

    private Model createErrorEndResultModel(Model model, String description) {
        model.addAttribute("status",PAYMENT_FAILED);
        model.addAttribute("Description", description);
        return model;
    }

    private ExternalAccount addCardToCustomer(String stripToken, Customer customer) throws Exception {
        logger.info("adding card to the customer");
        Customer retrivedCustomer =  Customer.retrieve(customer.getId());
        //   return Optional.ofNullable(retrivedCustomer).map(retri -> {
        Map<String,Object> source = new HashMap<String, Object>();
        source.put("source",stripToken);
        ExternalAccount externalAccount = null;
        try {

            externalAccount = retrivedCustomer.getSources().create(source);
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return externalAccount;
        //  }).orElseThrow(() -> new Exception("Stripe Customer Cannt retrived "+customer.getId()));
    }

    private Map<String, Object> createCustomerObj(String stripeEmail,String stripToken) {
        String customer_id = "cus_"+UUID.randomUUID().toString();
        Map<String,Object> customerParameter = new HashMap<>();
        customerParameter.put("email",stripeEmail);
        customerParameter.put("id",customer_id);
        customerParameter.put("source",stripToken);
        logger.info("customer info "+stripeEmail+" , "+ customer_id);
        return customerParameter;
    }

    private Date calculatePaymentExpiredDate(final String durationString) {
        Pattern p = Pattern.compile("[1-9+]+\\s+MONTH|[1-9+]+\\s+YEAR");
        Matcher m = p.matcher(durationString);

        if (m.find()) {

            /*durationString is behaving ex; 6 Month 1 Year */
            String[] splited = durationString.split("\\s+");
            /*6*/
            String duration = splited[0];
            /*Month or Year*/
            String scale = splited[1];

            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(new Date().getTime());

            if(scale.equals("YEAR")) {

                cal.add(Calendar.YEAR, Integer.parseInt(duration));
                return new Date(cal.getTime().getTime());
            } else if (scale.equals("MONTH")) {
                cal.add(Calendar.MONTH, Integer.parseInt(duration));
                return new Date(cal.getTime().getTime());
            }
        }
        return null;
    }

    @GetMapping("/makeMonthlyPayment")
    public ResponseEntity makeMonthlyPayment() {

        String customerId = "cus_c8e24feb-bf0f-4ae4-b42e-765bb25e7226";
        Map<String,Object> chargePrams = new HashMap<String,Object>();

        chargePrams.put("amount", 35*100);
        chargePrams.put("currency", "usd");
        chargePrams.put("description","monthly payment process");
        Customer dbCustomer = null;

        try {
            dbCustomer = getCustomerByCustomerId(customerId);
        } catch (Exception e) {
            logger.error(e);
        }
        chargePrams.put("customer",dbCustomer.getId());
        Charge monthlyChargeResponse = null;
        try {
            monthlyChargeResponse   = Charge.create(chargePrams);
        } catch (StripeException e) {
            logger.error("Error when monthly charging process");
            e.printStackTrace();
        }

        return new ResponseEntity(monthlyChargeResponse,HttpStatus.OK);
    }

    private Customer getCustomerByCustomerId(String customerId) throws Exception {
        return Optional.ofNullable(Customer.retrieve(customerId)).orElseThrow(() -> new Exception("Cannot find Customer"));
    }

    @ExceptionHandler(StripeException.class)
    public String handleError(Model model, StripeException ex) {
        model.addAttribute("error", ex.getMessage());
        return "result";
    }
}
