package com.delivery.igo.igo_delivery.api.user.entity;

import com.delivery.igo.igo_delivery.api.auth.dto.request.SignupRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.request.UpdatePasswordRequestDto;
import com.delivery.igo.igo_delivery.api.user.dto.request.UpdateUserRequestDto;
import com.delivery.igo.igo_delivery.common.dto.AuthUser;
import com.delivery.igo.igo_delivery.common.entity.BaseEntity;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.GlobalException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder // entity 생성 시 생성자가 아니라 Menus.builder().menus_id = "변경"
@Table(name = "users")
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Column
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    // 내 정보 수정
    public void updateBy(UpdateUserRequestDto requestDto) {
        this.nickname = requestDto.getNickname();
        this.phoneNumber = requestDto.getPhoneNumber();
        this.address = requestDto.getAddress();
        this.userRole = requestDto.getRole();
    }

    // 비밀번호 수정
    public void updatePassword(String newPassword) {
        password = newPassword;
    }

    // 삭제
    public void delete() {
        deletedAt = LocalDateTime.now();
        userStatus = UserStatus.INACTIVE;
    }

    // 삭제 검증, 삭제 되었다면 예외 발생
    public void validateDelete() {
        if (userStatus == UserStatus.INACTIVE) {
            throw new GlobalException(ErrorCode.DELETED_USER);
        }
    }

    // 접근 권한 검증, 로그인한 유저의 id와 id가 다르면 예외 발생
    public void validateAccess(AuthUser authUser) {
        if (!Objects.equals(authUser.getId(), id)) {
            throw new GlobalException(ErrorCode.FORBIDDEN);
        }
    }

    // 사업자 계정인지 검증
    public void validateOwner() {
        if (userRole != UserRole.OWNER) {
            throw new GlobalException(ErrorCode.ROLE_OWNER_FORBIDDEN);
        }
    }

    // 일반 고객인지 검증
    public void validateConsumer() {
        if (userRole != UserRole.CONSUMER) {
            throw new GlobalException(ErrorCode.ROLE_CONSUMER_FORBIDDEN);
        }
    }

    public static Users of(SignupRequestDto signupRequestDto, String encodedPassword) {
        UserRole userRole = UserRole.of(signupRequestDto.getUserRole());

        return Users.builder()
                .email(signupRequestDto.getEmail())
                .nickname(signupRequestDto.getNickname())
                .phoneNumber(signupRequestDto.getPhoneNumber())
                .password(encodedPassword)
                .address(signupRequestDto.getAddress())
                .userRole(userRole)
                .userStatus(UserStatus.LIVE)
                .build();

    }
}
