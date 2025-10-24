package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiProductDTO;
import com.rocketFoodDelivery.rocketFood.models.Product;
import com.rocketFoodDelivery.rocketFood.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ✅ Add this method so the controller can use it
    public List<ApiProductDTO> findProductsByRestaurantId(int restaurantId) {
        return productRepository.findProductsByRestaurantId(restaurantId)
                .stream() // <-- convert List to Stream
                .map(p -> new ApiProductDTO(p.getId(), p.getName(), p.getCost()))
                .collect(Collectors.toList());
    }
}
