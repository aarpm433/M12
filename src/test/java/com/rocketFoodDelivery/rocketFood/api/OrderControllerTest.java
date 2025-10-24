package com.rocketFoodDelivery.rocketFood.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.controller.api.OrderController;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateOrderDto;
import com.rocketFoodDelivery.rocketFood.models.*;
import com.rocketFoodDelivery.rocketFood.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private OrderRepository orderRepository;
    @MockBean private ProductOrderRepository productOrderRepository;
    @MockBean private ProductRepository productRepository;
    @MockBean private CustomerRepository customerRepository;
    @MockBean private RestaurantRepository restaurantRepository;
    @MockBean private CourierRepository courierRepository;
    @MockBean private OrderStatusRepository orderStatusRepository;

    @Test
    public void testGetOrders_Success() throws Exception {
        // Customer setup
        UserEntity customerUser = UserEntity.builder().id(10).name("Cathy Spinka").build();
        Address customerAddress = Address.builder()
                .id(1)
                .streetAddress("7757 Darwin Causeway")
                .city("Gerlachfort")
                .postalCode("19822")
                .build();
        Customer customer = Customer.builder()
                .id(5)
                .userEntity(customerUser)
                .address(customerAddress)
                .email("cathy@example.com")
                .phone("555-1234")
                .build();

        // Restaurant setup
        UserEntity restaurantUser = UserEntity.builder().id(20).name("Fast Pub Owner").build();
        Address restaurantAddress = Address.builder()
                .id(2)
                .streetAddress("5398 Quigley Harbor")
                .city("North Lynelle")
                .postalCode("60808")
                .build();
        Restaurant restaurant = Restaurant.builder()
                .id(1)
                .userEntity(restaurantUser)
                .address(restaurantAddress)
                .name("Fast Pub")
                .priceRange(2)
                .phone("123-456-7890")
                .email("fastpub@example.com")
                .build();

        // Courier setup
        UserEntity courierUser = UserEntity.builder().id(30).name("Cathy Spinka").build();
        Courier courier = Courier.builder()
                .id(3)
                .userEntity(courierUser)
                .build();

        // Order status
        OrderStatus status = OrderStatus.builder()
                .id(1)
                .name("in progress")
                .build();

        // Order setup
        Order order = Order.builder()
                .id(3)
                .customer(customer)
                .restaurant(restaurant)
                .courier(courier)
                .order_status(status)
                .build();

        // Product setup
        Product product = Product.builder()
                .id(2)
                .restaurant(restaurant)
                .name("Vegetable Soup")
                .cost(1975)
                .description("Fresh vegetable soup")
                .build();

        // ProductOrder setup
        ProductOrder productOrder = ProductOrder.builder()
                .order(order)
                .product(product)
                .product_quantity(2)
                .build();

        // Mock repository behavior
        when(orderRepository.findByCustomer_Id(5)).thenReturn(List.of(order));
        when(productOrderRepository.findByOrderId(3)).thenReturn(List.of(productOrder));

        // Perform GET request
        mockMvc.perform(get("/api/orders")
                        .param("type", "customer")
                        .param("id", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].customer_id").value(5))
                .andExpect(jsonPath("$[0].status").value("in progress"))
                .andExpect(jsonPath("$[0].products[0].product_name").value("Vegetable Soup"))
                .andExpect(jsonPath("$[0].products[0].total_cost").value(3950));
    }

    @Test
    public void testGetOrders_MissingParams() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .param("type", "customer")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetOrders_InvalidType() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .param("type", "admin")
                        .param("id", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}



