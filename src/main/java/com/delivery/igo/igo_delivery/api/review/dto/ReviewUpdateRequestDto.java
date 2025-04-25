package com.delivery.igo.igo_delivery.api.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateRequestDto {

    @NotBlank(message = "{review.content.notblank}")
    private String content;

    @NotNull(message = "{review.rating.notnull}")
    @Range(min = 1, max = 5, message = "{review.rating.outofband}")
    private Integer rating;
}
