package com.delivery.igo.igo_delivery.common.validation;

import com.delivery.igo.igo_delivery.api.store.entity.Stores;
import com.delivery.igo.igo_delivery.api.store.repository.StoreRepository;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoreValidator {

    private final StoreRepository storeRepository;

    public Stores validateStoreOwner(Long storesId, Users user) {

        Stores store = storeRepository.findById(storesId)
                .orElseThrow(() -> new GlobalException(ErrorCode.STORE_NOT_FOUND));

        if (!Objects.equals(user.getId(), store.getUsers().getId())) {
            throw new GlobalException(ErrorCode.STORE_OWNER_MISMATCH);
        }

        return store;
    }
}
