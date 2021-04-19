package com.stripe.payamentgateway.stripegateway.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccount;
import com.stripe.model.ExternalAccountCollection;
import lombok.extern.java.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Log
@Controller
public class ChargeController {

    @Autowired
    StripeService paymentsService;

    private Logger logger = LogManager.getLogger(ChargeController.class);

    @PostMapping("/charge")
    public String charge(ChargeRequest chargeRequest, Model model) throws StripeException {

        String stripeEmail = chargeRequest.getStripeEmail();
        Integer amount = chargeRequest.getAmount();
        String stripToken = chargeRequest.getStripeToken();



        // create customer
        // adding default card to the customer itself
        Map<String, Object> customerParameter = createCustomerObj(stripeEmail,stripToken);
        Customer customer = Customer.create(customerParameter);

        // add card to the customer
     /*   ExternalAccount externalAccount = null;
        try {
            externalAccount = addCardToCustomer(stripToken, customer);
        }catch (Exception e) {
            logger.error(e);
        }*/

        chargeRequest.setDescription("Example charge");
        chargeRequest.setCurrency(ChargeRequest.Currency.EUR);
        Charge charge = paymentsService.charge(chargeRequest);
        model.addAttribute("id", charge.getId());
        model.addAttribute("status", charge.getStatus());
        model.addAttribute("chargeId", charge.getId());
        model.addAttribute("balance_transaction", charge.getBalanceTransaction());
        return "result";
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
