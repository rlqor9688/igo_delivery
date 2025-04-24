package com.delivery.igo.igo_delivery.api.review.dto;

import com.delivery.igo.igo_delivery.api.review.entity.Reviews;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long userId;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;

    public static ReviewResponseDto of(Reviews reviews) {
        return new ReviewResponseDto(
                reviews.getUsers().getId(),
                reviews.getRating(),
                reviews.getContent(),
                reviews.getCreatedAt()
        );
    }
}
