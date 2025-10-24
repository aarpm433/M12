package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rocketFoodDelivery.rocketFood.models.Address;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;


@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductOrderRepository productOrderRepository;
    private final UserRepository userRepository;
    private final AddressService addressService;

    @Autowired
    public RestaurantService(
        RestaurantRepository restaurantRepository,
        ProductRepository productRepository,
        OrderRepository orderRepository,
        ProductOrderRepository productOrderRepository,
        UserRepository userRepository,
        AddressService addressService
        ) {
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.productOrderRepository = productOrderRepository;
        this.userRepository = userRepository;
        this.addressService = addressService;
    }

    public List<Restaurant> findAllRestaurants() {
        return restaurantRepository.findAll();
    }

    /**
     * Retrieves a restaurant with its details, including the average rating, based on the provided restaurant ID.
     *
     * @param id The unique identifier of the restaurant to retrieve.
     * @return An Optional containing a RestaurantDTO with details such as id, name, price range, and average rating.
     *         If the restaurant with the given id is not found, an empty Optional is returned.
     *
     * @see RestaurantRepository#findRestaurantWithAverageRatingById(int) for the raw query details from the repository.
     */
    public Optional<ApiRestaurantDto> findRestaurantWithAverageRatingById(int id) {
        List<Object[]> restaurant = restaurantRepository.findRestaurantWithAverageRatingById(id);

        if (!restaurant.isEmpty()) {
            Object[] row = restaurant.get(0);
            int restaurantId = (int) row[0];
            String name = (String) row[1];
            int priceRange = (int) row[2];
            double rating = (row[3] != null) ? ((BigDecimal) row[3]).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0;
            int roundedRating = (int) Math.ceil(rating);
            ApiRestaurantDto restaurantDto = new ApiRestaurantDto(restaurantId, name, priceRange, roundedRating);
            return Optional.of(restaurantDto);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Finds restaurants based on the provided rating and price range.
     *
     * @param rating     The rating for filtering the restaurants.
     * @param priceRange The price range for filtering the restaurants.
     * @return A list of ApiRestaurantDto objects representing the selected restaurants.
     *         Each object contains the restaurant's ID, name, price range, and a rounded-up average rating.
     */
    public List<ApiRestaurantDto> findRestaurantsByRatingAndPriceRange(Integer rating, Integer priceRange) {
        List<Object[]> restaurants = restaurantRepository.findRestaurantsByRatingAndPriceRange(rating, priceRange);

        List<ApiRestaurantDto> restaurantDtos = new ArrayList<>();

            for (Object[] row : restaurants) {
                int restaurantId = (int) row[0];
                String name = (String) row[1];
                int range = (int) row[2];
                double avgRating = (row[3] != null) ? ((BigDecimal) row[3]).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0;
                int roundedAvgRating = (int) Math.ceil(avgRating);
                restaurantDtos.add(new ApiRestaurantDto(restaurantId, name, range, roundedAvgRating));
            }

            return restaurantDtos;
    }

    // TODO

    /**
     * Creates a new restaurant and returns its information.
     *
     * @param restaurant The data for the new restaurant.
     * @return An Optional containing the created restaurant's information as an ApiCreateRestaurantDto,
     *         or Optional.empty() if the user with the provided user ID does not exist or if an error occurs during creation.
     */
    @Transactional
    public Optional<ApiCreateRestaurantDto> createRestaurant(ApiCreateRestaurantDto dto) {
        return userRepository.findById(dto.getUserId()).map(user -> {
            // Create Address entity from DTO
            Address address = new Address();
            address.setStreetAddress(dto.getAddress().getStreetAddress());
            address.setCity(dto.getAddress().getCity());
            address.setPostalCode(dto.getAddress().getPostalCode());
            address = addressService.saveAddress(address);

            // Create Restaurant entity
            Restaurant restaurant = Restaurant.builder()
                    .userEntity(user)
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .phone(dto.getPhone())
                    .priceRange(dto.getPriceRange())
                    .address(address)
                    .build();

            restaurantRepository.save(restaurant);

            // Return DTO for API response
            return new ApiCreateRestaurantDto(restaurant);
        });
    }




    // TODO

    /**
     * Finds a restaurant by its ID.
     *
     * @param id The ID of the restaurant to retrieve.
     * @return An Optional containing the restaurant with the specified ID,
     *         or Optional.empty() if no restaurant is found.
     */
    public Optional<Restaurant> findById(int id) {
        return restaurantRepository.findById(id);
    }

    // TODO

    /**
     * Updates an existing restaurant by ID with the provided data.
     *
     * @param id                  The ID of the restaurant to update.
     * @param updatedRestaurantDto The updated data for the restaurant.
     * @return An Optional containing the updated restaurant's information as an ApiCreateRestaurantDto,
     *         or Optional.empty() if the restaurant with the specified ID is not found or if an error occurs during the update.
     */
    @Transactional
        public Optional<ApiCreateRestaurantDto> updateRestaurant(int id, ApiCreateRestaurantDto updatedDto) {
            return restaurantRepository.findById(id).map(existing -> {
                existing.setName(updatedDto.getName());
                existing.setEmail(updatedDto.getEmail());
                existing.setPhone(updatedDto.getPhone());
                existing.setPriceRange(updatedDto.getPriceRange());

                if (updatedDto.getAddress() != null) {
                    Address updatedAddress = existing.getAddress();
                    updatedAddress.setStreetAddress(updatedDto.getAddress().getStreetAddress());
                    updatedAddress.setCity(updatedDto.getAddress().getCity());
                    updatedAddress.setPostalCode(updatedDto.getAddress().getPostalCode());
                    updatedAddress = addressService.saveAddress(updatedAddress);
                    existing.setAddress(updatedAddress);
                }

                restaurantRepository.save(existing);
                return new ApiCreateRestaurantDto(existing); // uses the constructor we just added
            });
        }


    // TODO

    /**
     * Deletes a restaurant along with its associated data, including its product orders, orders and products.
     *
     * @param restaurantId The ID of the restaurant to delete.
     */
    @Transactional
    public void deleteRestaurant(int restaurantId) {
        restaurantRepository.findById(restaurantId).ifPresent(restaurant -> {
            // Delete associated product orders
            var products = productRepository.findByRestaurantId(restaurantId);
            products.forEach(product -> productOrderRepository.deleteByProductId(product.getId()));
            // Delete associated orders
            var orders = orderRepository.findByRestaurant_Id(restaurantId);
            orders.forEach(order -> orderRepository.delete(order));
            // Delete products
            products.forEach(productRepository::delete);
            // Delete restaurant
            restaurantRepository.delete(restaurant);
        });
    }}