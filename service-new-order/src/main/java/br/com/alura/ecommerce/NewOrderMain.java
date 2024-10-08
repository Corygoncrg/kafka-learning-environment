package br.com.alura.ecommerce;


import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<String>()) {
                for (var i = 0; i < 10; i++) {
                    var email = Math.random() + "@email.com";
                    var orderId = UUID.randomUUID().toString();
                    var amount = BigDecimal.valueOf(Math.random() * 5000 + 1);
                    var order = new Order(orderId, amount, email);
                    var id = new CorrelationId(NewOrderMain.class.getSimpleName());
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, id, order);

                    var emailCode = "Thank you for your order! We are now processing it!";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, id, emailCode);
                }
            }
        }
    }
}