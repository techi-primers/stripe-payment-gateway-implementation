package com.stripe.payamentgateway.stripegateway.controller;

import com.plaid.client.request.LinkTokenCreateRequest;
import com.plaid.client.response.LinkTokenCreateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



import javax.xml.ws.Response;

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

    @GetMapping("/plaid_checkout")
    public String checkout(Model model) {

/*        String clientUserId = "123-test-user-id";

        LinkTokenCreateRequest.User user = new LinkTokenCreateRequest.User(clientUserId);
        Map<String, LinkTokenCreateRequest.SubtypeFilters> accountFilters = new HashMap<>();
        accountFilters.put("depository",
                new LinkTokenCreateRequest.SubtypeFilters(Collections.singletonList("checking")));
        LinkTokenCreateRequest request = new LinkTokenCreateRequest(
                user,
                "Plaid Test App",
                Collections.singletonList("auth"))
                .withCountryCodes(Collections.singletonList("US"))
                .withLanguage("en")
                .withWebhook("https://example.com/webhook")
                .withLinkCustomizationName("default")
                .withAccountFilters(accountFilters);

        Response<LinkTokenCreateResponse> response =
                client().service().linkTokenCreate(
                        request).execute();

        String linkToken = response.body().getLinkToken();*/
        model.addAttribute("publicToken", "testtoken");
        return "plaidCheckout";
    }

    @PostMapping("/test")
    public void test(Model model){

        System.out.println("hello world");
    }
}
