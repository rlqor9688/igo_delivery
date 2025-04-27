package com.delivery.igo.igo_delivery.api.store.service;

import com.delivery.igo.igo_delivery.api.store.dto.StoreListResponseDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreRequestDto;
import com.delivery.igo.igo_delivery.api.store.dto.StoreResponseDto;
import com.delivery.igo.igo_delivery.api.store.entity.StoreStatus;
import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.api.user.repository.UserRepository;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;


import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

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

        // userRepository.findById() 호출 시 owner 반환
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        // 이미 등록된 매장이 2개라고 가정
        when(storeRepository.countByUsersAndStoreStatusIsNot(owner, StoreStatus.CLOSED))
                .thenReturn(2L);

        // storeRepository.save() 호출 시, 실제 매장 엔티티 반환
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

        // storeRepository.save()가 호출되었는지 검증
        verify(storeRepository).save(any(Stores.class));
    }

    @Test
    void 매장_전체_조회_검색어_있음() {

        // given
        Users owner = Users.builder()
                .id(1L)
                .nickname("초코라떼")
                .build();

        Stores store = Stores.builder()
                .id(1L)
                .users(owner)
                .storeName("초코라떼가제일좋아")
                .storeAddress("인천시 미추홀구")
                .storePhoneNumber("010-8282-8282")
                .openTime(Time.valueOf(LocalTime.of(8, 0)))
                .endTime(Time.valueOf(LocalTime.of(22, 0)))
                .minOrderPrice(10000)
                .storeStatus(StoreStatus.OPEN)
                .build();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Stores> storePage = new PageImpl<>(List.of(store), pageable, 1);

        when(storeRepository.findByStoreNameContainingIgnoreCaseAndDeletedAtIsNull("초코라떼", pageable))
                .thenReturn(storePage);

        // when
        Page<StoreListResponseDto> result = storeService.getStores("초코라떼", pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getStoreName()).isEqualTo("초코라떼가제일좋아");
    }

    @Test
    void 매장_전체_조회_검색어_없음() {
        // given
        Stores store1 = Stores.builder().id(1L).storeName("초코라떼").minOrderPrice(10000).build();
        Stores store2 = Stores.builder().id(2L).storeName("설빙").minOrderPrice(19000).build();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Stores> storePage = new PageImpl<>(List.of(store1, store2), pageable, 2);

        when(storeRepository.findByStoreNameContainingIgnoreCaseAndDeletedAtIsNull("", pageable))
                .thenReturn(storePage);

        // when
        Page<StoreListResponseDto> result = storeService.getStores("", pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getStoreName()).isEqualTo("초코라떼");
        assertThat(result.getContent().get(1).getStoreName()).isEqualTo("설빙");
    }

    @Test
    void 매장_전체_조회_검색어_매장_없음() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Stores> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(storeRepository.findByStoreNameContainingIgnoreCaseAndDeletedAtIsNull("황금올리브", pageable))
                .thenReturn(emptyPage);

        // when
        Page<StoreListResponseDto> result = storeService.getStores("황금올리브", pageable);

        // then
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void 매장_단건_조회_성공() {

        // given
        Stores store = Stores.builder()
                .id(1L)
                .storeName("초코라떼가제일좋아")
                .storeAddress("인천시 미추홀구")
                .storePhoneNumber("010-8282-8282")
                .openTime(Time.valueOf(LocalTime.of(8, 0)))
                .endTime(Time.valueOf(LocalTime.of(22, 0)))
                .minOrderPrice(10000)
                .storeStatus(StoreStatus.LIVE)
                .build();

        given(storeRepository.findById(1L)).willReturn(Optional.of(store));

        // when
        StoreResponseDto response = storeService.getStore(1L);

        // then
        assertThat(response.getStoreName()).isEqualTo("초코라떼가제일좋아");
        assertThat(response.getStoreAddress()).isEqualTo("인천시 미추홀구");
        assertThat(response.getMinOrderPrice()).isEqualTo(10000);
    }

    @Test
    void 매장_단건_조회_매장_없음() {

        // given
        given(storeRepository.findById(9999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.getStore(9999L))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ErrorCode.STORE_NOT_FOUND.getMessage());
    }
}