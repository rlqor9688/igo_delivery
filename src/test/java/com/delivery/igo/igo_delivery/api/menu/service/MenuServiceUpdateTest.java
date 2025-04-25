package com.delivery.igo.igo_delivery.api.menu.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.delivery.igo.igo_delivery.api.menu.dto.request.MenuRequestDto;
import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuResponseDto;
import com.delivery.igo.igo_delivery.api.menu.entity.MenuStatus;
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
public class MenuServiceUpdateTest {

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

    private Menus menu;

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

        menu = Menus.builder()
                .id(1L)
                .stores(store)
                .menuName("기존 메뉴")
                .price(500L)
                .build();
    }

    @Test
    void menus_메뉴_수정에_성공한다() {

        Long storesId = 1L;
        Long menuId = 1L;
        MenuRequestDto requestDto = new MenuRequestDto("수정된 메뉴", 1000L);

        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(storeRepository.findById(store.getId())).willReturn(Optional.of(store));
        given(menuRepository.findByIdAndStoresId(menuId, storesId)).willReturn(Optional.of(menu));

        MenuResponseDto responseDto = menuService.updateMenu(authUser, storesId, menuId, requestDto);

        assertNotNull(responseDto);

        verify(userRepository).findById(authUser.getId());
        verify(storeRepository).findById(storesId);
        verify(menuRepository).findByIdAndStoresId(menuId, storesId);

        assertEquals("수정된 메뉴", menu.getMenuName());
        assertEquals(1000L, menu.getPrice());
    }

    @Test
    void menus_해당_매장_주인이_아닌_경우_메뉴_수정에_실패한다() {

        AuthUser otherAuthUser = new AuthUser(2L, "test@gmail.com", "nickname", UserRole.OWNER);

        Users otherUser = Users.builder()
                .id(2L)
                .userRole(UserRole.OWNER)
                .build();

        Long storeId = 1L;
        Long menuId = 1L;
        MenuRequestDto requestDto = new MenuRequestDto("수정된 메뉴", 1000L);

        given(userRepository.findById(otherAuthUser.getId())).willReturn(Optional.of(otherUser));
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            menuService.updateMenu(otherAuthUser, storeId, menuId, requestDto);
        });

        assertEquals(ErrorCode.STORE_OWNER_MISMATCH, exception.getErrorCode());

        verify(userRepository).findById(otherAuthUser.getId());
        verify(storeRepository).findById(storeId);
        verify(menuRepository, never()).findByIdAndStoresId(menuId, storeId);
    }

    @Test
    void menus_해당_매장의_메뉴가_아닌_경우_메뉴_수정에_실패한다() {

        Long storesId = 1L;
        Long otherMenuId = 2L;
        MenuRequestDto requestDto = new MenuRequestDto("수정된 메뉴", 1000L);

        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(storeRepository.findById(store.getId())).willReturn(Optional.of(store));
        given(menuRepository.findByIdAndStoresId(otherMenuId, storesId)).willReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            menuService.updateMenu(authUser, storesId, otherMenuId, requestDto);
        });

        assertEquals(ErrorCode.MENU_NOT_FOUND, exception.getErrorCode());

        verify(userRepository).findById(authUser.getId());
        verify(storeRepository).findById(storesId);
        verify(menuRepository).findByIdAndStoresId(otherMenuId, storesId);
    }

    @Test
    void menus_삭제된_메뉴를_수정하려는_경우_메뉴_수정에_실패한다() {

        Menus deletedMenu = Menus.builder()
                .id(2L)
                .stores(store)
                .menuStatus(MenuStatus.INACTIVE)
                .build();

        Long storesId = 1L;
        Long menuId = 2L;
        MenuRequestDto requestDto = new MenuRequestDto("수정된 메뉴", 1000L);

        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(storeRepository.findById(store.getId())).willReturn(Optional.of(store));
        given(menuRepository.findByIdAndStoresId(menuId, storesId)).willReturn(Optional.of(deletedMenu));

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            menuService.updateMenu(authUser, storesId, menuId, requestDto);
        });

        assertEquals(ErrorCode.DELETED_MENU, exception.getErrorCode());

        verify(userRepository).findById(authUser.getId());
        verify(storeRepository).findById(storesId);
        verify(menuRepository).findByIdAndStoresId(menuId, storesId);
    }
}
