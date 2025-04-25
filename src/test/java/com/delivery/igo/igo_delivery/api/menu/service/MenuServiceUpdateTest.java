package com.delivery.igo.igo_delivery.api.menu.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.delivery.igo.igo_delivery.api.menu.dto.request.MenuRequestDto;
import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuResponseDto;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.menu.repository.MenuRepository;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.validation.StoreValidator;
import com.delivery.igo.igo_delivery.common.validation.UserValidator;
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
    void menus_메뉴_수정에_성공한다() {

        Long storesId = 1L;
        Long menuId = 1L;
        MenuRequestDto requestDto = new MenuRequestDto("수정된 메뉴", 1000L);
        Menus menu = Menus.builder()
                .id(1L)
                .stores(store)
                .menuName("기존 메뉴")
                .price(500L)
                .build();

        given(userValidator.validateOwner(authUser.getId())).willReturn(user);
        given(storeValidator.validateStoreOwner(store.getId(), user)).willReturn(store);
        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        MenuResponseDto responseDto = menuService.updateMenu(authUser, storesId, menuId, requestDto);

        assertNotNull(responseDto);

        verify(userValidator).validateOwner(authUser.getId());
        verify(storeValidator).validateStoreOwner(storesId, user);

        assertEquals("수정된 메뉴", menu.getMenuName());
        assertEquals(1000L, menu.getPrice());
    }
}
