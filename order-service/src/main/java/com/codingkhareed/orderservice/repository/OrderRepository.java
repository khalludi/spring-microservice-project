package com.codingkhareed.orderservice.repository;

import com.codingkhareed.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
