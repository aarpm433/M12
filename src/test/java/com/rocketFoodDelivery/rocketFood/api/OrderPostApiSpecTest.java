package com.rocketFoodDelivery.rocketFood.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateOrderDto;
import com.rocketFoodDelivery.rocketFood.models.*;
import com.rocketFoodDelivery.rocketFood.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class OrderPostApiSpecTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private OrderRepository orderRepository;
    @MockBean private ProductOrderRepository productOrderRepository;
    @MockBean private ProductRepository productRepository;
    @MockBean private CustomerRepository customerRepository;
    @MockBean private RestaurantRepository restaurantRepository;
    @MockBean private OrderStatusRepository orderStatusRepository;

    private Customer customer;
    private Restaurant restaurant;
    private Product product1;
    private Product product2;
    private OrderStatus orderStatus;

    @BeforeEach
    void setup() {
        // Customer
        customer = new Customer();
        customer.setId(3);

        // Restaurant
        restaurant = new Restaurant();
        restaurant.setId(1);

        // Products
        product1 = new Product();
        product1.setId(2);
        product1.setCost(10);

        product2 = new Product();
        product2.setId(3);
        product2.setCost(5);

        // Order status
        orderStatus = new OrderStatus();
        orderStatus.setName("in progress");
    }

    @Test
    void testCreateOrder_SpecRequest() throws Exception {
        ApiCreateOrderDto dto = new ApiCreateOrderDto();
        dto.setCustomerId(3);
        dto.setRestaurantId(1);
        dto.setProducts(List.of(
                new ApiCreateOrderDto.ProductOrderDto(2, 1),
                new ApiCreateOrderDto.ProductOrderDto(3, 3)
        ));

        when(customerRepository.findById(3)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(productRepository.findById(2)).thenReturn(Optional.of(product1));
        when(productRepository.findById(3)).thenReturn(Optional.of(product2));
        when(orderStatusRepository.findByName("in progress")).thenReturn(Optional.of(orderStatus));

        // Mock order save
        when(orderRepository.save(Mockito.any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(1);
            return o;
        });

        // Mock product order save
        when(productOrderRepository.save(Mockito.any(ProductOrder.class))).thenAnswer(i -> i.getArgument(0));
        when(productOrderRepository.findByOrderId(1)).thenReturn(List.of(
                new ProductOrder() {{ setOrder(new Order() {{ setId(1); }}); setProduct(product1); setQuantity(1); }},
                new ProductOrder() {{ setOrder(new Order() {{ setId(1); }}); setProduct(product2); setQuantity(3); }}
        ));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Order created successfully"))
                .andExpect(jsonPath("$.data.customer_id").value(3))
                .andExpect(jsonPath("$.data.restaurant_id").value(1))
                .andExpect(jsonPath("$.data.products[0].product_id").value(2))
                .andExpect(jsonPath("$.data.products[0].quantity").value(1))
                .andExpect(jsonPath("$.data.products[1].product_id").value(3))
                .andExpect(jsonPath("$.data.products[1].quantity").value(3))
                .andExpect(jsonPath("$.data.total_cost").value(10*1 + 5*3));
    }
}
