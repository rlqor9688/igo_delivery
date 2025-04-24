package com.delivery.igo.igo_delivery.api.cart.service;

import com.delivery.igo.igo_delivery.api.cart.dto.CartRequest;
import com.delivery.igo.igo_delivery.api.cart.dto.CartResponse;
import com.delivery.igo.igo_delivery.api.cart.entity.CartItems;
import com.delivery.igo.igo_delivery.api.cart.entity.Carts;
import com.delivery.igo.igo_delivery.api.cart.repository.CartItemsRepository;
import com.delivery.igo.igo_delivery.api.cart.repository.CartRepository;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.menu.repository.MenuRepository;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;

    //todo :  해당 service 관련 에러 코드 추가
    @Override
    @Transactional
    public CartResponse addCart(AuthUser authUser, CartRequest request) {

        //로그인한 유저 호출
        Users users = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        //해당 유저의 장바구니 호출
        Carts carts = cartRepository.findByUsers(users)
                .orElseThrow(()-> new GlobalException(ErrorCode.NOT_FOUND));

        //요청 들어온 메뉴 호출 -> 존재하지 않는 메뉴일시 에러 출력
        Menus menus = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        // 해당 메뉴가 장바구니에 존재하는지 여부 확인 후 메뉴 및 수량 추가
        Optional<CartItems> cartItems = cartItemsRepository.findByCartsAndMenus(carts, menus);
        if (cartItems.isPresent()) {
            // 해당 메뉴 이미 존재할 경우 입력받은 만큼 수량 추가
            CartItems existingItem = cartItems.get();
            existingItem.addQuantity(request.getCartQuantity());
        } else {
            // 새로운 메뉴+수량 장바구니에 추가
            CartItems newItem = new CartItems(menus, carts, menus.getPrice(), request.getCartQuantity());
            cartItemsRepository.save(newItem);
        }
        return CartResponse.of(carts);
    }


}
