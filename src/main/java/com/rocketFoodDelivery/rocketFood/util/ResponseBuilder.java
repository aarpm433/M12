package com.rocketFoodDelivery.rocketFood.util;

import org.springframework.http.ResponseEntity;
import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDTO;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling API responses.
 * Success responses -> ApiResponseDTO
 * Error responses -> Map<String, Object>
 */
public class ResponseBuilder {

    // --- SUCCESS RESPONSES ---

    public static ResponseEntity<Object> buildOkResponse(String message, Object data) {
        ApiResponseDTO response = new ApiResponseDTO();
        response.setMessage(message);
        response.setData(data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static ResponseEntity<Object> buildCreatedResponse(String message, Object data) {
        ApiResponseDTO response = new ApiResponseDTO();
        response.setMessage(message);
        response.setData(data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // --- ERROR RESPONSES ---

    public static ResponseEntity<Map<String, Object>> buildBadRequest(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", message);
        return ResponseEntity.badRequest().body(body);
    }


    public static ResponseEntity<Object> buildNotFound(String details) {
        return new ResponseEntity<>(
            Map.of(
                "error", "Resource not found",
                "details", details
            ),
            HttpStatus.NOT_FOUND
        );
    }
}
