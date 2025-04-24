package com.delivery.igo.igo_delivery.api.cart.controller;


import com.delivery.igo.igo_delivery.api.cart.dto.CartRequest;
import com.delivery.igo.igo_delivery.api.cart.dto.CartResponse;
import com.delivery.igo.igo_delivery.api.cart.service.CartService;
import com.delivery.igo.igo_delivery.common.annotation.Auth;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> addCart(
            @Auth AuthUser authUser,
            @RequestBody CartRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addCart(authUser,request));
    }
}
