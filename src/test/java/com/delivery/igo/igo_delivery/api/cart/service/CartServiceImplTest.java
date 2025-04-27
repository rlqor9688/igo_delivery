package com.delivery.igo.igo_delivery.api.cart.service;

import com.delivery.igo.igo_delivery.api.cart.dto.request.CreateCartRequestDto;
import com.delivery.igo.igo_delivery.api.cart.dto.response.CreateCartResponseDto;
import com.delivery.igo.igo_delivery.api.cart.dto.response.FindAllCartsResponseDto;
import com.delivery.igo.igo_delivery.api.cart.entity.CartItems;
import com.delivery.igo.igo_delivery.api.cart.entity.Carts;
import com.delivery.igo.igo_delivery.api.cart.repository.CartItemsRepository;
import com.delivery.igo.igo_delivery.api.cart.repository.CartRepository;
import com.delivery.igo.igo_delivery.api.menu.entity.MenuStatus;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.menu.repository.MenuRepository;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
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


import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

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

        CreateCartRequestDto request = new CreateCartRequestDto(menusId, quantity);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(users));
        given(cartRepository.findByUsers(users)).willReturn(Optional.of(carts));
        given(menuRepository.findById(anyLong())).willReturn(Optional.of(menus));
        given(cartItemsRepository.findByCartsAndMenus(carts, menus)).willReturn(Optional.empty());

        // when
        CreateCartResponseDto response = cartService.addCart(authUser, request);

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

        Stores store = Stores.builder().id(1L).build();

        Menus menus = Menus.builder()
                .id(menusId)
                .menuName("피자")
                .price(10000L)
                .menuStatus(MenuStatus.LIVE)
                .stores(store)
                .build();

        CartItems existingCartItem = CartItems.builder()
                .menus(menus)
                .carts(carts)
                .cartPrice(menus.getPrice())
                .cartQuantity(originalQuantity)
                .build();


        CreateCartRequestDto request = new CreateCartRequestDto(menusId, addQuantity);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(users));
        given(cartRepository.findByUsers(users)).willReturn(Optional.of(carts));
        given(menuRepository.findById(anyLong())).willReturn(Optional.of(menus));
        given(cartItemsRepository.findByCartsAndMenus(carts, menus)).willReturn(Optional.of(existingCartItem));


        // when
        CreateCartResponseDto response = cartService.addCart(authUser, request);

        // then
        assertNotNull(response);
        assertEquals(originalQuantity + addQuantity, existingCartItem.getCartQuantity());

        verify(cartItemsRepository).findByCartsAndMenus(carts, menus);
    }


    @Test
    void 다른_매장_상품이_존재하면_기존_장바구니_물건_삭제_후_추가() {
        // given
        Long usersId = 1L;
        Long menusId = 200L;

        Users users = Users.builder().id(usersId).build();
        Carts carts = Carts.builder().users(users).build();

        Stores oldStore = Stores.builder().id(1L).build();
        Stores newStore = Stores.builder().id(2L).build();
        Menus oldMenu = Menus.builder().stores(oldStore).build();
        Menus newMenu = Menus.builder().id(menusId).stores(newStore).price(1500L).build();

        CartItems oldItem = CartItems.builder().menus(oldMenu).carts(carts).cartQuantity(1).build();
        CreateCartRequestDto request = new CreateCartRequestDto(menusId, 1);
        AuthUser authUser = new AuthUser(usersId, "email", "nickname", UserRole.CONSUMER);

        given(userRepository.findById(usersId)).willReturn(Optional.of(users));
        given(cartRepository.findByUsers(users)).willReturn(Optional.of(carts));
        given(menuRepository.findById(menusId)).willReturn(Optional.of(newMenu));
        given(cartItemsRepository.findByCartsAndMenus(carts, newMenu)).willReturn(Optional.of(oldItem));

        // when
        CreateCartResponseDto response = cartService.addCart(authUser, request);

        // then
        assertNotNull(response);
        verify(cartItemsRepository).deleteAll(List.of(oldItem));
        verify(cartItemsRepository).save(any(CartItems.class));
    }

    @Test
    void 장바구니_전체_조회_성공() {
        // given
        Long usersId = 1L;

        AuthUser authUser = new AuthUser(usersId, "test123@gmail.com", "테스트용", UserRole.CONSUMER);
        Users users = Users.builder()
                .id(usersId)
                .email("test@test.com")
                .nickname("tester")
                .build();

        Carts carts = Carts.builder().users(users).build();

        Stores store = Stores.builder().id(1L).build();

        Menus menu1 = Menus.builder()
                .id(1L)
                .menuName("피자")
                .price(10000L)
                .menuStatus(MenuStatus.LIVE)
                .stores(store)
                .build();
        Menus menu2 = Menus.builder()
                .id(2L)
                .menuName("햄버거")
                .price(5000L)
                .menuStatus(MenuStatus.LIVE)
                .stores(store)
                .build();

        CartItems cartItem1 = CartItems.builder()
                .carts(carts)
                .menus(menu1)
                .cartPrice(menu1.getPrice())
                .cartQuantity(2)
                .build();

        CartItems cartItem2 = CartItems.builder()
                .carts(carts)
                .menus(menu2)
                .cartPrice(menu2.getPrice())
                .cartQuantity(3)
                .build();

        given(userRepository.findById(usersId)).willReturn(Optional.of(users));
        given(cartRepository.findByUsers(users)).willReturn(Optional.of(carts));
        given(cartItemsRepository.findAllByCarts(carts)).willReturn(List.of(cartItem1, cartItem2));

        // when
        FindAllCartsResponseDto response = cartService.findAllCarts(authUser);

        // then
        assertNotNull(response);
        assertEquals(carts.getId(), response.getCartsId());
        assertEquals(10000L * 2 + 5000L * 3, response.getTotalPrice());
        assertEquals(2, response.getItems().size());

        verify(userRepository).findById(usersId);
        verify(cartRepository).findByUsers(users);
        verify(cartItemsRepository).findAllByCarts(carts);
    }
}