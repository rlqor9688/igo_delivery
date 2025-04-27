package com.delivery.igo.igo_delivery.api.cart.controller;


import com.delivery.igo.igo_delivery.api.cart.dto.request.CreateCartRequestDto;
import com.delivery.igo.igo_delivery.api.cart.dto.request.UpdateCartItemRequestDto;
import com.delivery.igo.igo_delivery.api.cart.dto.response.CreateCartResponseDto;
import com.delivery.igo.igo_delivery.api.cart.dto.response.FindAllCartsResponseDto;
import com.delivery.igo.igo_delivery.api.cart.dto.response.UpdateCartItemResponseDto;
import com.delivery.igo.igo_delivery.api.cart.service.CartService;
import com.delivery.igo.igo_delivery.common.annotation.Auth;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CreateCartResponseDto> addCart(
            @Auth AuthUser authUser,
            @Valid @RequestBody CreateCartRequestDto request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addCart(authUser,request));
    }


    @GetMapping
    public ResponseEntity<FindAllCartsResponseDto> findAllCarts(@Auth AuthUser authUser){

        FindAllCartsResponseDto responseDto = cartService.findAllCarts(authUser);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }

    @PostMapping("/items/{cartItemId}")
    public ResponseEntity<UpdateCartItemResponseDto> updateCartItem(@PathVariable Long cartItemId,
                                                                    @Auth AuthUser authUser,
                                                                    @Valid @RequestBody UpdateCartItemRequestDto requestDto) {

        UpdateCartItemResponseDto responseDto = cartService.updateCartItem(cartItemId, authUser, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
