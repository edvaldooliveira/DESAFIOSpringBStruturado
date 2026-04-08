package com.DESAFIOSpringBootestruturado.repositories;

import com.DESAFIOSpringBootestruturado.entities.OrderItem;
import com.DESAFIOSpringBootestruturado.entities.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {

}
