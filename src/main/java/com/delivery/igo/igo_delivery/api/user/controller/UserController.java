package com.delivery.igo.igo_delivery.api.user.controller;

import com.delivery.igo.igo_delivery.api.user.dto.resonse.FindUserResponseDto;
import com.delivery.igo.igo_delivery.api.user.service.UserService;
import com.delivery.igo.igo_delivery.common.annotation.Auth;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원 조회가 아닌 내 정보 조회이므로 getMyInfo()로 메서드 명을 작성함
     *
     * @param id 조회할 유저의 id
     * @param authUser 인증된 사용자 정보
     * @return 조회된 유저의 응답 DTO, 성공시 OK응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<FindUserResponseDto> getMyInfo(@PathVariable Long id, @Auth AuthUser authUser) {
        FindUserResponseDto responseDto = userService.findUserById(id, authUser);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
