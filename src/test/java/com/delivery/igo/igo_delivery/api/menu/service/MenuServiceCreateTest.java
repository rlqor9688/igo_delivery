package com.delivery.igo.igo_delivery.api.menu.service;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.delivery.igo.igo_delivery.api.menu.dto.request.MenuRequestDto;
import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuResponseDto;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.menu.repository.MenuRepository;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import com.delivery.igo.igo_delivery.common.validation.StoreValidator;
import com.delivery.igo.igo_delivery.common.validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceCreateTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private StoreValidator storeValidator;

    @InjectMocks
    private MenuServiceImpl menuService;

    private AuthUser authUser;

    private Users user;

    private Stores store;

    @BeforeEach
    void setUp() {
        authUser = new AuthUser(1L, "test@gmail.com", "nickname", UserRole.OWNER);

        user = Users.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("nickname")
                .userRole(UserRole.OWNER)
                .build();

        store = Stores.builder()
                .id(1L)
                .users(user)
                .storeName("가게")
                .build();
    }

    @Test
    void menus_메뉴_생성에_성공한다() {

        MenuRequestDto requestDto = new MenuRequestDto("메뉴 이름", 1000L);
        Menus menu = Menus.of(store, requestDto);
        Long storeId = 1L;

        given(userValidator.validateOwner(authUser.getId())).willReturn(user);
        given(storeValidator.validateStoreOwner(anyLong(), eq(user))).willReturn(store);
        given(menuRepository.save(any())).willReturn(menu);

        MenuResponseDto responseDto = menuService.createMenu(authUser, storeId, requestDto);

        assertNotNull(responseDto);

        verify(userValidator).validateOwner(authUser.getId());
        verify(storeValidator).validateStoreOwner(storeId, user);
        verify(menuRepository).save(any(Menus.class));
    }

    @Test
    void menus_해당_매장_주인이_아닌_경우_메뉴_생성에_실패한다() {

        AuthUser otherAuthUser = new AuthUser(2L, "test@gmail.com", "nickname", UserRole.OWNER);

        Users otherUser = Users.builder()
                .id(2L)
                .email("test@gmail.com")
                .nickname("nickname")
                .userRole(UserRole.OWNER)
                .build();

        MenuRequestDto requestDto = new MenuRequestDto("메뉴 이름", 1000L);
        Menus menu = Menus.of(store, requestDto);
        Long storeId = 1L;

        given(userValidator.validateOwner(otherAuthUser.getId())).willReturn(otherUser);
        given(storeValidator.validateStoreOwner(anyLong(), eq(otherUser))).willThrow(new GlobalException(ErrorCode.STORE_OWNER_MISMATCH));

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            menuService.createMenu(otherAuthUser, storeId, requestDto);
        });

        assertEquals("해당 가게의 사장님만 접근할 수 있습니다.", exception.getMessage());

        verify(userValidator).validateOwner(otherAuthUser.getId());
        verify(storeValidator).validateStoreOwner(storeId, otherUser);
        verify(menuRepository, never()).save(any(Menus.class));
    }

    @Test
    void menus_해당_회원이_매장_주인이_아닌_경우_메뉴_생성에_실패한다() {

        AuthUser otherAuthUser = new AuthUser(2L, "test@gmail.com", "nickname", UserRole.CONSUMER);

        Users otherUser = Users.builder()
                .id(2L)
                .email("test@gmail.com")
                .nickname("nickname")
                .userRole(UserRole.CONSUMER)
                .build();

        MenuRequestDto requestDto = new MenuRequestDto("메뉴 이름", 1000L);
        Menus menu = Menus.of(store, requestDto);
        Long storeId = 1L;

        given(userValidator.validateOwner(otherAuthUser.getId())).willThrow(new GlobalException(ErrorCode.ROLE_OWNER_FORBIDDEN));

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            menuService.createMenu(otherAuthUser, storeId, requestDto);
        });

        assertEquals("매장 사장님이 아닙니다.", exception.getMessage());

        verify(userValidator).validateOwner(otherAuthUser.getId());
        verify(storeValidator, never()).validateStoreOwner(storeId, otherUser);
    }
}