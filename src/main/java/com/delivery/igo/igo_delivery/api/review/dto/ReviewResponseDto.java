package com.delivery.igo.igo_delivery.api.review.dto;

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

}
