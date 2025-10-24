package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductOrderDto {
    private int id;
    private int quantity;

    public ProductOrderDto() {}

    public ProductOrderDto(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }
}
