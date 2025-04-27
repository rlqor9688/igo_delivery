package com.delivery.igo.igo_delivery.api.menu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.delivery.igo.igo_delivery.api.menu.dto.response.MenuReadResponseDto;
import com.delivery.igo.igo_delivery.api.menu.entity.MenuStatus;
import com.delivery.igo.igo_delivery.api.menu.entity.Menus;
import com.delivery.igo.igo_delivery.api.menu.repository.MenuRepository;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MenuServiceReadTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    private Stores store;

    private Menus menu1;

    private Menus menu2;

    private Menus menu3;

    @BeforeEach
    void setUp() {

        store = Stores.builder()
                .id(1L)
                .storeName("Test Store")
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
                .menuStatus(MenuStatus.LIVE)
                .build();

        menu3 = Menus.builder()
                .id(2L)
                .stores(store)
                .menuName("Test Menu 3")
                .price(2000L)
                .menuStatus(MenuStatus.INACTIVE)
                .build();
    }

    @Test
    void menus_메뉴_전체_조회에_성공한다() {

        Long storeId = 1L;

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(menuRepository.findMenusByStoreIdOrderByCreatedAtDesc(storeId, MenuStatus.LIVE)).willReturn(
                List.of(menu1, menu2));

        List<MenuReadResponseDto> result = menuService.findAllMenu(storeId);

        assertThat(result).hasSize(2);
        assertEquals(result.get(0).getMenuName(), "Test Menu 1");
        assertEquals(result.get(1).getMenuName(), "Test Menu 2");

        verify(storeRepository).findById(storeId);
        verify(menuRepository).findMenusByStoreIdOrderByCreatedAtDesc(storeId, MenuStatus.LIVE);
    }

    @Test
    void menus_매장이_존재하지_않는_경우_메뉴_전체_조회에_실패한다() {

        Long storeId = 2L;

        given(storeRepository.findById(storeId)).willThrow(new GlobalException(ErrorCode.STORE_NOT_FOUND));

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            menuService.findAllMenu(storeId);
        });

        assertEquals(ErrorCode.STORE_NOT_FOUND, exception.getErrorCode());

        verify(storeRepository).findById(storeId);
        verify(menuRepository, never()).findMenusByStoreIdOrderByCreatedAtDesc(storeId, MenuStatus.LIVE);
    }
}
