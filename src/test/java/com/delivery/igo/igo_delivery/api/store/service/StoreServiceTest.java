package com.delivery.igo.igo_delivery.api.store.service;

import com.delivery.igo.igo_delivery.api.store.dto.StoreRequestDto;
import com.delivery.igo.igo_delivery.api.store.entity.StoreStatus;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreServiceImpl storeService;

    @Test
    void 매장_정상_생성() {

        // given
        Users owner = Users.builder()
                .id(1L)
                .email("testowner1@email.com")
                .nickname("초코라떼")
                .userRole(UserRole.OWNER)
                .build();

        StoreRequestDto request = new StoreRequestDto(
                "초코라떼가제일좋아",
                "인천시 미추홀구",
                "010-8282-8282",
                LocalTime.of(8, 0),
                LocalTime.of(22, 0),
                10000
        );

        // 이미 등록된 매장이 2개라고 가정
        when(storeRepository.countByUsersAndStoreStatusIsNot(owner, StoreStatus.CLOSED))
                .thenReturn(2L);

        // storeRepository.save() 호출 시, 실제 매장 엔티티 리턴하도록 설정
        Stores store = Stores.builder()
                .users(owner)
                .storeName("초코라떼가제일좋아")
                .storeAddress("인천시 미추홀구")
                .storePhoneNumber("010-8282-8282")
                .openTime(Time.valueOf(request.getOpenTime()))
                .endTime(Time.valueOf(request.getEndTime()))
                .minOrderPrice(request.getMinOrderPrice())
                .storeStatus(StoreStatus.LIVE)
                .build();

        when(storeRepository.save(any(Stores.class))).thenReturn(store);

        // when
        var response = storeService.createStore(request, owner.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStoreName()).isEqualTo("초코라떼가제일좋아");
        verify(storeRepository).save(any(Stores.class));
    }
}