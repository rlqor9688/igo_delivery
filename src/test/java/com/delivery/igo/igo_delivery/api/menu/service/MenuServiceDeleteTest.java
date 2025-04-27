package com.delivery.igo.igo_delivery.api.menu.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
public class MenuServiceDeleteTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    private AuthUser authUser;

    private Users user;

    private Stores store;

    private Menus menu1;

    private Menus menu2;

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

        menu1 = Menus.builder()
                .id(1L)
                .stores(store)
                .menuName("Test Menu 1")
                .price(1000L)
                .menuStatus(MenuStatus.LIVE)
                .build();

        menu2 = Menus.builder()
                .id(2L)
                .stores(store)
                .menuName("Test Menu 2")
                .price(2000L)
                .menuStatus(MenuStatus.INACTIVE)
                .build();
    }

    @Test
    void menus_메뉴_삭제에_성공한다() {

        Long userId = 1L;
        Long storeId = 1L;
        Long menuId = 1L;

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(menuRepository.findByIdAndStoresId(menuId, storeId)).willReturn(Optional.of(menu1));

        menuService.deleteMenu(authUser, storeId, menuId);

        verify(userRepository).findById(userId);
        verify(storeRepository).findById(storeId);
        verify(menuRepository).findByIdAndStoresId(menuId, storeId);
    }

    @Test
    void menus_이미_삭제된_메뉴를_삭제하려는_경우_메뉴_삭제에_실패한다() {

        Long userId = 1L;
        Long storeId = 1L;
        Long menuId = 2L;

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(menuRepository.findByIdAndStoresId(menuId, storeId)).willReturn(Optional.of(menu2));

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            menuService.deleteMenu(authUser, storeId, menuId);
        });

        assertEquals(ErrorCode.DELETED_MENU, exception.getErrorCode());

        verify(userRepository).findById(userId);
        verify(storeRepository).findById(storeId);
        verify(menuRepository).findByIdAndStoresId(menuId, storeId);
    }
}
