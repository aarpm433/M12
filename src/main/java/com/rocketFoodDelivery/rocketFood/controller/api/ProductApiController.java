package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiProductDTO;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rocketFoodDelivery.rocketFood.repository.ProductRepository;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;

    @Autowired
    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    /** GET /api/products?restaurant=<id> */
    @GetMapping
    public ResponseEntity<Object> getProductsByRestaurant(@RequestParam(name = "restaurant") Integer restaurantId) {
        List<ApiProductDTO> products = productService.findProductsByRestaurantId(restaurantId);

        if (products.isEmpty()) {
            throw new ResourceNotFoundException("Product with id " + restaurantId + " not found");
        }

        return ResponseEntity.ok(products);
    }
}
