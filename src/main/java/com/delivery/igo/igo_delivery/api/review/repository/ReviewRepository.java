package com.delivery.igo.igo_delivery.api.review.repository;

import com.delivery.igo.igo_delivery.api.review.entity.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {

    @Query("SELECT r FROM Reviews r WHERE r.stores.id = :storeId AND r.rating BETWEEN :minRating AND :maxRating ORDER BY r.createdAt DESC")
    List<Reviews> findAllByStoresIdAndRatingRange(@Param("storeId") Long storeId,
                                                  @Param("minRating") int minRating,
                                                  @Param("maxRating") int maxRating);
}
