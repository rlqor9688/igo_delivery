package com.delivery.igo.igo_delivery.api.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {

    @NotNull
    private Long ordersId;

    @NotNull
    private Long storesId;

    @NotBlank
    private String content;

    @NotNull
    private Integer rating;
}
