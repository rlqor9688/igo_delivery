package com.delivery.igo.igo_delivery.api.cart.service;

import com.delivery.igo.igo_delivery.api.cart.dto.CartRequest;
import com.delivery.igo.igo_delivery.api.cart.dto.CartResponse;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;

public interface CartService {

    CartResponse addCart(AuthUser authUser, CartRequest request);

}
