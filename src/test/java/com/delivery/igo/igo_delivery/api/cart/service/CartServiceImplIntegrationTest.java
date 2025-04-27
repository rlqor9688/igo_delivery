package com.delivery.igo.igo_delivery.api.cart.service;

import com.delivery.igo.igo_delivery.IgoDeliveryApplication;
import com.delivery.igo.igo_delivery.api.cart.dto.request.UpdateCartItemRequestDto;
import com.delivery.igo.igo_delivery.api.cart.dto.response.UpdateCartItemResponseDto;
import com.delivery.igo.igo_delivery.api.cart.entity.CartItemQuantityType;
import com.delivery.igo.igo_delivery.api.cart.entity.CartItems;
import com.delivery.igo.igo_delivery.api.cart.entity.Carts;
import com.delivery.igo.igo_delivery.api.cart.repository.CartItemsRepository;
import com.delivery.igo.igo_delivery.api.cart.repository.CartRepository;
import com.delivery.igo.igo_delivery.api.menu.entity.MenuStatus;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.menu.repository.MenuRepository;
import com.delivery.igo.igo_delivery.api.store.entity.StoreStatus;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.UserStatus;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = IgoDeliveryApplication.class)
@Transactional
@ActiveProfiles("test")
class CartServiceImplIntegrationTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MenuRepository menuRepository;

    private Users user1;
    private Users user2;
    private Carts carts;
    private Menus menu;
    private CartItems cartItem;
    private AuthUser authUser;

    @BeforeEach
    void init() {
        user1 = userRepository.save(Users.builder()
                .email("test1@example.com")
                .nickname("테스트유저1")
                .password("encodedPassword123")
                .phoneNumber("010-2222-33333")
                .address("테스트 동네")
                .userRole(UserRole.CONSUMER)
                .userStatus(UserStatus.LIVE)
                .build());

        user2 = userRepository.save(Users.builder()
                .email("test2@example.com")
                .nickname("테스트유저2")
                .password("encodedPassword123")
                .phoneNumber("010-2222-33333")
                .address("테스트 동네")
                .userRole(UserRole.OWNER)
                .userStatus(UserStatus.LIVE)
                .build());

        authUser = new AuthUser(
                user1.getId(),
                user1.getEmail(),
                user1.getNickname(),
                user1.getUserRole()
        );

        Stores store = Stores.builder()
                .users(user2)
                .storeName("테스트가게")
                .storeAddress("테스트동네")
                .storePhoneNumber("010-2222-3333")
                .openTime(Time.valueOf(LocalTime.of(8, 0)))
                .endTime(Time.valueOf(LocalTime.of(22, 0)))
                .minOrderPrice(10000)
                .storeStatus(StoreStatus.OPEN)
                .avgRating(0.0)
                .reviewCount(0)
                .build();

        Stores saveStore = storeRepository.save(store);

        carts = cartRepository.save(Carts.builder()
                .users(user1)
                .build());

        menu = menuRepository.save(Menus.builder()
                .menuName("pizza")
                .price(10000L)
                .menuStatus(MenuStatus.LIVE)
                .stores(saveStore)
                .build());

        cartItem = cartItemsRepository.save(CartItems.builder()
                .carts(carts)
                .menus(menu)
                .cartPrice(10000L)
                .cartQuantity(2)
                .build());
    }

    @Test
    void 장바구니아이템의_수량을_2개에서_3개로_증가_성공() {
        // given
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(CartItemQuantityType.INCREASE);

        // when
        UpdateCartItemResponseDto responseDto = cartService.updateCartItem(cartItem.getId(), authUser, requestDto);

        // then
        CartItems updatedCartItem = cartItemsRepository.findById(cartItem.getId()).orElseThrow();
        assertEquals(3, updatedCartItem.getCartQuantity());
        assertEquals(responseDto.getId(), cartItem.getId());
        assertEquals(3, responseDto.getQuantity());
    }

    @Test
    void 장바구니아이템의_수량을_1개에서_0개로_감소하면_삭제_성공() {
        // given
        cartItem = cartItemsRepository.save(CartItems.builder()
                .carts(carts)
                .menus(menu)
                .cartPrice(10000L)
                .cartQuantity(1)
                .build());

        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(CartItemQuantityType.DECREASE);

        // when
        cartService.updateCartItem(cartItem.getId(), authUser, requestDto);

        // then
        boolean result = cartItemsRepository.findById(cartItem.getId()).isPresent();
        assertFalse(result);
    }


    @Test
    void 실패하면_장바구니아이템_수량_변경되지_않음_예외_CART_ITEM_NOT_FOUND() {
        // given
        long wrongCartItemId = 999999L;

        // when & then
        GlobalException exception = assertThrows(GlobalException.class,
                () -> cartService.updateCartItem(wrongCartItemId, authUser, new UpdateCartItemRequestDto(CartItemQuantityType.INCREASE)));

        CartItems findCartItem = cartItemsRepository.findById(cartItem.getId()).orElseThrow();

        assertEquals(ErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());
        assertEquals(findCartItem.getCartQuantity(), cartItem.getCartQuantity());
    }
}