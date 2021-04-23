package com.stripe.payamentgateway.stripegateway.controller;

import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccount;
import com.stripe.payamentgateway.stripegateway.dto.ChargingResponseCustom;
import com.stripe.payamentgateway.stripegateway.dto.SystemPaymentInfo;
import com.stripe.payamentgateway.stripegateway.entity.ChargingRecordDocument;
import com.stripe.payamentgateway.stripegateway.entity.StripeCustomer;
import com.stripe.payamentgateway.stripegateway.repository.ChargingRecordDocumentRepository;
import com.stripe.payamentgateway.stripegateway.repository.StripeCustomerRepository;
import com.stripe.payamentgateway.stripegateway.service.ChargeProcessService;
import lombok.extern.java.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
@RestController
public class ChargeController {


    @Autowired
    private ChargeProcessService chargeProcessService;

    private Logger logger = LogManager.getLogger(ChargeController.class);

    @PostMapping("/charge")
    public ModelAndView charge(ChargeRequest chargeRequest, Model model)  {

        String stripeEmail = chargeRequest.getStripeEmail();
        Integer amount = chargeRequest.getAmount();
        String stripToken = chargeRequest.getStripeToken();
        String systemUserId = chargeRequest.getSystemUserId();

        return this.chargeProcessService.doInitialChargeProcess(model, stripeEmail, amount, stripToken, systemUserId);

    }

}
