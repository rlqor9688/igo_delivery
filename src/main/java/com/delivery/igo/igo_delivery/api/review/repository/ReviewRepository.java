package com.delivery.igo.igo_delivery.api.review.repository;

import com.delivery.igo.igo_delivery.api.review.entity.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {
}
