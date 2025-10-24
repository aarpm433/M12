package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateOrderDto;
import com.rocketFoodDelivery.rocketFood.models.*;
import com.rocketFoodDelivery.rocketFood.repository.*;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductOrderRepository productOrderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private CourierRepository courierRepository;
    @Autowired private OrderStatusRepository orderStatusRepository;

    @GetMapping
    public ResponseEntity<?> getOrders(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer id) {

        if (type == null || id == null) {
            return ResponseBuilder.buildBadRequest("Invalid or missing parameters");
        }

        List<Order> orders;
        switch (type.toLowerCase()) {
            case "customer" -> orders = orderRepository.findByCustomer_Id(id);
            case "restaurant" -> orders = orderRepository.findByRestaurant_Id(id);
            case "courier" -> orders = orderRepository.findByCourier_Id(id);
            default -> {
                return ResponseBuilder.buildBadRequest("Invalid type parameter");
            }
        }

        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Order order : orders) {
            responseList.add(buildOrderResponse(order));
        }

        return ResponseEntity.ok(responseList);
    }

    // ✅ POST /api/orders
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody ApiCreateOrderDto dto) {
        try {
            // statusId is not in the example request, so remove it from validation
            if (dto.getCustomerId() == null || dto.getRestaurantId() == null ||
                dto.getProducts() == null || dto.getProducts().isEmpty()) {
                return ResponseBuilder.buildBadRequest("Invalid or missing parameters");
            }

            // Create order with default "in progress" status if none provided
            Order order = new Order();
            order.setCustomer(customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found")));
            order.setRestaurant(restaurantRepository.findById(dto.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found")));

            order.setOrder_status(orderStatusRepository.findByName("in progress")
                    .orElseThrow(() -> new RuntimeException("Order status 'in progress' not found")));

            if (dto.getCourierId() != null) {
                order.setCourier(courierRepository.findById(dto.getCourierId()).orElse(null));
            }

            Order savedOrder = orderRepository.save(order);

            // Save product orders
        for (ApiCreateOrderDto.ProductOrderDto pDto : dto.getProducts()) {
            Product product = productRepository.findById(pDto.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            ProductOrder productOrder = new ProductOrder();
            productOrder.setOrder(savedOrder);
            productOrder.setProduct(product);
            productOrder.setQuantity(pDto.getQuantity());
            productOrderRepository.save(productOrder);
        }

        // After this, return the response
        return ResponseBuilder.buildCreatedResponse("Order created successfully", buildOrderResponse(savedOrder));

        } catch (Exception e) {
            return ResponseBuilder.buildBadRequest("Error creating order" + e.getMessage());
        }
    }

    // ✅ Helper
    private Map<String, Object> buildOrderResponse(Order order) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", order.getId());
        data.put("customer_id", order.getCustomer() != null ? order.getCustomer().getId() : null);
        data.put("customer_name", order.getCustomer() != null ? order.getCustomer().getFullName() : null);
        data.put("customer_address", order.getCustomer() != null ? order.getCustomer().getAddress() : null);
        data.put("restaurant_id", order.getRestaurant() != null ? order.getRestaurant().getId() : null);
        data.put("restaurant_name", order.getRestaurant() != null ? order.getRestaurant().getName() : null);
        data.put("restaurant_address", order.getRestaurant() != null ? order.getRestaurant().getAddress() : null);
        data.put("courier_id", order.getCourier() != null ? order.getCourier().getId() : null);
        data.put("courier_name", order.getCourier() != null ? order.getCourier().getFullName() : null);
        data.put("status", order.getOrder_status() != null ? order.getOrder_status().getName() : null);

        List<Map<String, Object>> productList = new ArrayList<>();
        long totalCost = 0;

        List<ProductOrder> productOrders = productOrderRepository.findByOrderId(order.getId());
        for (ProductOrder po : productOrders) {
            Product p = po.getProduct();
            if (p == null) continue;
            long cost = p.getCost() * po.getQuantity();

            Map<String, Object> prodMap = new LinkedHashMap<>();
            prodMap.put("product_id", p.getId());
            prodMap.put("product_name", p.getName());
            prodMap.put("quantity", po.getQuantity());
            prodMap.put("unit_cost", p.getCost());
            prodMap.put("total_cost", cost);

            totalCost += cost;
            productList.add(prodMap);
        }

        data.put("products", productList);
        data.put("total_cost", totalCost);
        return data;
    }
}
