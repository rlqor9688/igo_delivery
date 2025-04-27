package com.delivery.igo.igo_delivery.api.review.repository;
import com.delivery.igo.igo_delivery.api.review.entity.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {
    List<Reviews> findAllByStores_IdAndRatingBetweenOrderByCreatedAtDesc(Long storesId, int minRating, int maxRating);
}
