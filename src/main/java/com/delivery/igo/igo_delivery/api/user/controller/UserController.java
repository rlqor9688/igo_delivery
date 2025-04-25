package com.delivery.igo.igo_delivery.api.user.controller;

import com.delivery.igo.igo_delivery.api.user.dto.request.UpdateUserRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.resonse.UserResponseDto;
import com.delivery.igo.igo_delivery.api.user.service.UserService;
import com.delivery.igo.igo_delivery.common.annotation.Auth;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<UserResponseDto> getMyInfo(@PathVariable Long id, @Auth AuthUser authUser) {
        UserResponseDto responseDto = userService.findUserById(id, authUser);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> modifyMyInfo(@PathVariable Long id,
                                                        @Auth AuthUser authUser,
                                                        @Valid @RequestBody UpdateUserRequestDto requestDto) {

        UserResponseDto responseDto = userService.updateUserById(id, authUser, requestDto);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
