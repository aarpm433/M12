package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiCreateRestaurantDto {
    private int id;

    @JsonProperty("user_id")
    private int userId;

    private String name;

    @JsonProperty("price_range")
    @Min(1)
    @Max(3)
    private int priceRange;

    private String phone;

    @Email
    private String email;

    private ApiAddressDto address;

    public ApiCreateRestaurantDto(Restaurant restaurant) {
    this.id = restaurant.getId();
    this.userId = restaurant.getUserEntity().getId();
    this.name = restaurant.getName();
    this.email = restaurant.getEmail();
    this.phone = restaurant.getPhone();
    this.priceRange = restaurant.getPriceRange();
    if (restaurant.getAddress() != null) {
        this.address = new ApiAddressDto(
            restaurant.getAddress().getId(),
            restaurant.getAddress().getStreetAddress(),
            restaurant.getAddress().getCity(),
            restaurant.getAddress().getPostalCode()
        );
    }
}

}

