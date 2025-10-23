package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.exception.*;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantApiController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantApiController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /** GET all restaurants with optional filters */
    @GetMapping
    public ResponseEntity<Object> getAllRestaurants(
            @RequestParam(name = "rating", required = false) Integer rating,
            @RequestParam(name = "price_range", required = false) Integer priceRange) {

        // Return a plain JSON array
        List<ApiRestaurantDto> restaurants = restaurantService.findRestaurantsByRatingAndPriceRange(rating, priceRange);
        return ResponseEntity.ok(restaurants);
    }

    /** GET a restaurant by ID */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getRestaurantById(@PathVariable int id) {
        Optional<ApiRestaurantDto> restaurantOpt = restaurantService.findRestaurantWithAverageRatingById(id);

        if (restaurantOpt.isPresent()) {
            return ResponseEntity.ok(restaurantOpt.get());
        } else {
            // Return proper 404 JSON structure
            throw new ResourceNotFoundException("Restaurant with id " + id + " not found");
        }
    }

    /** POST create a new restaurant */
    @PostMapping
    public ResponseEntity<Object> createRestaurant(@Valid @RequestBody ApiCreateRestaurantDto restaurant, BindingResult result) {
        if (result.hasErrors()) {
            throw new BadRequestException("Invalid or missing parameters");
        }

        Optional<ApiCreateRestaurantDto> createdRestaurant = restaurantService.createRestaurant(restaurant);
        if (createdRestaurant.isPresent()) {
            return ResponseBuilder.buildCreatedResponse("Success", createdRestaurant.get());
        } else {
            throw new BadRequestException("User with id " + restaurant.getUserId() + " not found");
        }
    }

    /** PUT update a restaurant */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateRestaurant(
            @PathVariable int id,
            @Valid @RequestBody ApiCreateRestaurantDto restaurantUpdateData,
            BindingResult result) {

        if (result.hasErrors()) {
            throw new BadRequestException("Validation failed: " + result.getAllErrors());
        }

        Optional<ApiCreateRestaurantDto> updatedRestaurant = restaurantService.updateRestaurant(id, restaurantUpdateData);
        if (updatedRestaurant.isPresent()) {
            return ResponseBuilder.buildOkResponse("Success", updatedRestaurant.get());
        } else {
            throw new ResourceNotFoundException("Restaurant with id " + id + " not found");
        }
    }

    /** DELETE a restaurant */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRestaurant(@PathVariable int id) {
        Optional<ApiRestaurantDto> restaurantOpt = restaurantService.findRestaurantWithAverageRatingById(id);

        if (restaurantOpt.isPresent()) {
            restaurantService.deleteRestaurant(id);
            return ResponseBuilder.buildOkResponse("Success", restaurantOpt.get());
        } else {
            throw new ResourceNotFoundException("Restaurant with id " + id + " not found");
        }
    }
}
