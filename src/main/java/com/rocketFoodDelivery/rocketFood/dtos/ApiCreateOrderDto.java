package com.rocketFoodDelivery.rocketFood.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ApiCreateOrderDto {
    @NotNull
    private Integer customerId;

    @NotNull
    private Integer restaurantId;

    private Integer courierId; // optional

    @NotNull
    private Integer statusId;

    @NotNull
    private List<ProductOrderDto> products; // list of products

    @Getter
    @Setter
    public static class ProductOrderDto {
        @NotNull
        private Integer id;

        @NotNull
        private Integer quantity;
    }
}
