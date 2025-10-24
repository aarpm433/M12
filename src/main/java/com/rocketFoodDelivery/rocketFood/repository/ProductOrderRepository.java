package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

    // Delete all product orders linked to a specific order
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM product_orders WHERE order_id = :orderId")
    void deleteProductOrdersByOrderId(@Param("orderId") int orderId);

    // Delete all product orders linked to a specific product
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductOrder po WHERE po.product.id = :productId")
    void deleteByProductId(@Param("productId") int productId);

    Optional<ProductOrder> findById(int id);
    List<ProductOrder> findByOrderId(int id);
    List<ProductOrder> findByProductId(int id);

    @Override
    void deleteById(Integer productOrderId);
    
}
