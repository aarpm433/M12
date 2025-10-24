package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ApiProductDTO {
    private int id;
    private String name;
    private long cost;

    public ApiProductDTO(int id, String name, long cost) {
        this.id = id;
        this.name = name;
        this.cost = cost;
    }
}

