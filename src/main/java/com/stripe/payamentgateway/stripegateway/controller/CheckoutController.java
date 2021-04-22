package com.stripe.payamentgateway.stripegateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckoutController {

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    /*http://localhost:6666/checkout?systemUserId=111&&amount=30*/
    @GetMapping("/checkout")
    public String checkout(Model model, @RequestParam(required = true) String systemUserId,
                           @RequestParam(required = true) Integer amount) {
        model.addAttribute("amount", amount * 100); // in cents
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.USD);
        model.addAttribute("systemUserId", systemUserId);
        return "checkout";
    }
}
