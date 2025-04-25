package com.delivery.igo.igo_delivery.api.menu.service;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.delivery.igo.igo_delivery.api.menu.dto.request.MenuRequestDto;
import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuResponseDto;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.menu.repository.MenuRepository;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import java.util.Optional;
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
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

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
        Menus menu = Menus.of(store, requestDto.getMenuName(), requestDto.getPrice());
        Long storeId = 1L;

        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(menuRepository.save(any())).willReturn(menu);

        MenuResponseDto responseDto = menuService.createMenu(authUser, storeId, requestDto);

        assertNotNull(responseDto);

        verify(userRepository).findById(authUser.getId());
        verify(storeRepository).findById(storeId);
        verify(menuRepository).save(any(Menus.class));

        assertEquals("메뉴 이름", menu.getMenuName());
        assertEquals(1000L, menu.getPrice());
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
        Menus menu = Menus.of(store, requestDto.getMenuName(), requestDto.getPrice());
        Long storeId = 1L;

        given(userRepository.findById(otherAuthUser.getId())).willReturn(Optional.of(otherUser));
        given(storeRepository.findById(storeId)).willThrow(new GlobalException(ErrorCode.STORE_OWNER_MISMATCH));

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            menuService.createMenu(otherAuthUser, storeId, requestDto);
        });

        assertEquals(ErrorCode.STORE_OWNER_MISMATCH, exception.getErrorCode());

        verify(userRepository).findById(otherAuthUser.getId());
        verify(storeRepository).findById(storeId);
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
        Menus menu = Menus.of(store, requestDto.getMenuName(), requestDto.getPrice());
        Long storeId = 1L;

        given(userRepository.findById(otherAuthUser.getId())).willThrow(new GlobalException(ErrorCode.ROLE_OWNER_FORBIDDEN));

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            menuService.createMenu(otherAuthUser, storeId, requestDto);
        });

        assertEquals(ErrorCode.ROLE_OWNER_FORBIDDEN, exception.getErrorCode());

        verify(userRepository).findById(otherAuthUser.getId());
        verify(storeRepository, never()).findById(storeId);
    }
}