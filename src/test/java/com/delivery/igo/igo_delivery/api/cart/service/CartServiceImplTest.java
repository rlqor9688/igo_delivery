package com.delivery.igo.igo_delivery.api.cart.service;

import com.delivery.igo.igo_delivery.api.cart.dto.CartRequest;
import com.delivery.igo.igo_delivery.api.cart.dto.CartResponse;
import com.delivery.igo.igo_delivery.api.cart.entity.CartItems;
import com.delivery.igo.igo_delivery.api.cart.entity.Carts;
import com.delivery.igo.igo_delivery.api.cart.repository.CartItemsRepository;
import com.delivery.igo.igo_delivery.api.cart.repository.CartRepository;
import com.delivery.igo.igo_delivery.api.menu.entity.MenuStatus;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.menu.repository.MenuRepository;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private CartItemsRepository cartItemsRepository;

    @Test
    void 장바구니_신규_메뉴_추가() {
        // given
        Long usersId = 1L;
        Long menusId = 1L;
        int quantity = 2;

        AuthUser authUser = new AuthUser(usersId, "test123@gmail.com", "테스트용", UserRole.CONSUMER);
        Users users = Users.builder()
                .id(usersId)
                .email("test@test.com")
                .nickname("tester")
                .build();
        Carts carts = Carts.builder()
                .users(users)
                .build();
        Menus menus = Menus.builder()
                .id(menusId)
                .menuName("피자")
                .price(10000L)
                .menuStatus(MenuStatus.LIVE)
                .build();

        CartRequest request = new CartRequest(menusId, quantity);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(users));
        given(cartRepository.findByUsers(users)).willReturn(Optional.of(carts));
        given(menuRepository.findById(anyLong())).willReturn(Optional.of(menus));
        given(cartItemsRepository.findByCartsAndMenus(carts, menus)).willReturn(Optional.empty());

        // when
        CartResponse response = cartService.addCart(authUser, request);

        // then
        assertNotNull(response);
        verify(cartItemsRepository).save(any(CartItems.class));
    }

    @Test
    void 장바구니_기존_메뉴_수량_증가() {
        // given
        Long usersId = 1L;
        Long menusId = 1L;
        int originalQuantity = 2;
        int addQuantity = 4;

        AuthUser authUser = new AuthUser(usersId, "test123@gmail.com", "테스트용", UserRole.CONSUMER);

        Users users = Users.builder()
                .id(usersId)
                .email("test@test.com")
                .nickname("tester")
                .build();

        Carts carts = Carts.builder()
                .users(users)
                .build();

        Menus menus = Menus.builder()
                .id(menusId)
                .menuName("피자")
                .price(10000L)
                .menuStatus(MenuStatus.LIVE)
                .build();

        CartItems existingCartItem = CartItems.builder()
                .menus(menus)
                .carts(carts)
                .cartPrice(menus.getPrice())
                .cartQuantity(originalQuantity)
                .build();

        CartRequest request = new CartRequest(menusId, addQuantity);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(users));
        given(cartRepository.findByUsers(users)).willReturn(Optional.of(carts));
        given(menuRepository.findById(anyLong())).willReturn(Optional.of(menus));
        given(cartItemsRepository.findByCartsAndMenus(carts, menus)).willReturn(Optional.of(existingCartItem));


        // when
        CartResponse response = cartService.addCart(authUser, request);

        // then
        assertNotNull(response);
        assertEquals(originalQuantity + addQuantity, existingCartItem.getCartQuantity());

        verify(cartItemsRepository).findByCartsAndMenus(carts, menus);
    }

}