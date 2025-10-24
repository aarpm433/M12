package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.exception.BadRequestException;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.service.ProductOrderService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class ProductOrderApiController {

    private final ProductOrderService orderService;

    @Autowired
    public ProductOrderApiController(ProductOrderService orderService) {
        this.orderService = orderService;
    }

    /** POST /api/order/{order_id}/status */
    @PostMapping("/{order_id}/status")
    public ResponseEntity<Object> updateOrderStatus(
            @PathVariable("order_id") int orderId,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");
        if (status == null || status.isBlank()) {
            throw new BadRequestException("Invalid or missing parameters");
        }

        // For now, we simulate order existence
        boolean orderExists = orderId <= 10; // example placeholder

        if (!orderExists) {
            throw new ResourceNotFoundException("Order with id " + orderId + " not found");
        }

        return ResponseBuilder.buildOkResponse("Success", Map.of("status", status));
    }
}
