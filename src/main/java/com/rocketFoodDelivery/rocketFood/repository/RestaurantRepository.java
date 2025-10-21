package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
    Optional<Restaurant> findByUserEntityId(int id);
    List<Restaurant> findAll();

    /**
     * Finds a restaurant by its ID along with the calculated average rating rounded up to the ceiling.
     */
    @Query(nativeQuery = true, value = """
        SELECT r.id, r.name, r.price_range, 
               COALESCE(CEIL(SUM(o.restaurant_rating) / NULLIF(COUNT(o.id), 0)), 0) AS rating
        FROM restaurants r
        LEFT JOIN orders o ON r.id = o.restaurant_id
        WHERE r.id = :restaurantId
        GROUP BY r.id
        """)
    List<Object[]> findRestaurantWithAverageRatingById(@Param("restaurantId") int restaurantId);

    /**
     * Finds restaurants based on rating and price range.
     */
    @Query(nativeQuery = true, value = """
        SELECT * FROM (
            SELECT r.id, r.name, r.price_range, 
                   COALESCE(CEIL(SUM(o.restaurant_rating) / NULLIF(COUNT(o.id), 0)), 0) AS rating
            FROM restaurants r
            LEFT JOIN orders o ON r.id = o.restaurant_id
            WHERE (:priceRange IS NULL OR r.price_range = :priceRange)
            GROUP BY r.id
        ) AS result
        WHERE (:rating IS NULL OR result.rating = :rating)
        """)
    List<Object[]> findRestaurantsByRatingAndPriceRange(@Param("rating") Integer rating,
                                                        @Param("priceRange") Integer priceRange);

    // ✅ Insert new restaurant
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        INSERT INTO restaurants (user_id, address_id, name, price_range, phone, email)
        VALUES (:userId, :addressId, :name, :priceRange, :phone, :email)
        """)
    void saveRestaurant(@Param("userId") long userId,
                        @Param("addressId") long addressId,
                        @Param("name") String name,
                        @Param("priceRange") int priceRange,
                        @Param("phone") String phone,
                        @Param("email") String email);

    // ✅ Update existing restaurant info
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        UPDATE restaurants
        SET name = :name, price_range = :priceRange, phone = :phone
        WHERE id = :restaurantId
        """)
    void updateRestaurant(@Param("restaurantId") int restaurantId,
                          @Param("name") String name,
                          @Param("priceRange") int priceRange,
                          @Param("phone") String phone);

    // ✅ Find restaurant by ID
    @Query(nativeQuery = true, value = """
        SELECT * FROM restaurants WHERE id = :restaurantId
        """)
    Optional<Restaurant> findRestaurantById(@Param("restaurantId") int restaurantId);

    // ✅ Get last inserted ID (MySQL only)
    @Query(nativeQuery = true, value = "SELECT LAST_INSERT_ID() AS id")
    int getLastInsertedId();

    // ✅ Delete restaurant by ID
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        DELETE FROM restaurants WHERE id = :restaurantId
        """)
    void deleteRestaurantById(@Param("restaurantId") int restaurantId);
}
