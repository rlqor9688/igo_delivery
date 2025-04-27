package com.delivery.igo.igo_delivery.api.cart.service;

import com.delivery.igo.igo_delivery.api.cart.dto.request.CreateCartRequestDto;
import com.delivery.igo.igo_delivery.api.cart.dto.response.CreateCartResponseDto;
import com.delivery.igo.igo_delivery.api.cart.dto.response.FindAllCartsResponseDto;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;

public interface CartService {

    CreateCartResponseDto addCart(AuthUser authUser, CreateCartRequestDto request);

    FindAllCartsResponseDto findAllCarts(AuthUser authUser);
}
