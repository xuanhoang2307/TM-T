package huce.edu.web.services;
import com.paypal.orders.*;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.paypal.core.PayPalEnvironment;

@Service
public class PayPalService {
    private final PayPalHttpClient client;
    public PayPalService(@Value("${paypal.client.id}") String clientId,
            @Value("${paypal.client.secret}") String clientSecret) {
    	PayPalEnvironment environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
        this.client = new PayPalHttpClient(environment);
    }
    public String createPayment(huce.edu.web.model.Order order) {
        OrdersCreateRequest request = new OrdersCreateRequest();
        request.header("prefer", "return=representation");
        request.requestBody(buildOrderRequest(order));
        try {
            // Gửi yêu cầu đến PayPal
            HttpResponse<Order> response = client.execute(request);
            for (LinkDescription link : response.result().links()) {
                if ("approve".equals(link.rel())) {
                    return link.href(); // URL này để redirect người dùng đến PayPal
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    private OrderRequest buildOrderRequest(huce.edu.web.model.Order order) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");
        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        purchaseUnits.add(
            new PurchaseUnitRequest()
                .amountWithBreakdown(
                    new AmountWithBreakdown()
                        .currencyCode("USD")
                        .value(String.valueOf(order.getTotalDiscountedPrice()))
                )
        );
        orderRequest.purchaseUnits(purchaseUnits);
        return orderRequest;
    }
    public boolean executePayment(String paymentId, String payerId) {
        OrdersCaptureRequest request = new OrdersCaptureRequest(paymentId);
        
        try {
            HttpResponse<Order> response = client.execute(request);
            if (response.statusCode() == 200) {
                String status = response.result().status();
                return "COMPLETED".equals(status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
