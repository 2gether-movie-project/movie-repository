package com.movieproject.domain.user.service.internal;


import com.movieproject.domain.user.dto.request.LoginRequestDto;
import com.movieproject.domain.user.dto.request.PasswordChangeDto;
import com.movieproject.domain.user.dto.request.UserUpdateDto;
import com.movieproject.domain.user.dto.response.LoginResponseDto;
import com.movieproject.domain.user.dto.request.UserRequestDto;
import com.movieproject.domain.user.dto.response.UserResponseDto;
import com.movieproject.domain.user.entity.User;
import com.movieproject.domain.user.exception.UserException;
import com.movieproject.domain.user.exception.UserErrorCode;
import com.movieproject.domain.user.repository.UserRepository;
import com.movieproject.common.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserInternalService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    //회원가입
    @Transactional
    public UserResponseDto signup(UserRequestDto userRequestDto) {

        if (userRepository.existsByEmail(userRequestDto.email())) {
            throw new UserException(UserErrorCode.ALREADY_EXIST_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(userRequestDto.password());

        User user = User.create(
                userRequestDto.username(),
                userRequestDto.email(),
                encodedPassword,
                userRequestDto.role() //Role.USER

        );
        User newuser = userRepository.save(user);

        return UserResponseDto.from(newuser);
    }

    //로그인
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByEmail(loginRequestDto.email())
                .orElseThrow(() -> new UserException(UserErrorCode.INVALID_LOGIN_CREDENTIALS));

        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_LOGIN_CREDENTIALS);
        }

        String accessToken = jwtProvider.createAccessToken(user.getUserId(), user.getUsername(), user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());


        return LoginResponseDto.of(
                accessToken,
                refreshToken
        );
    }

    //토큰 갱신
    public LoginResponseDto refreshToken(String refreshToken) {
        // 리프레시 토큰 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new UserException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtProvider.createAccessToken(user.getUserId(), user.getUsername(), user.getRole());

        return new LoginResponseDto(
                newAccessToken,
                refreshToken
        );
    }

    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        return UserResponseDto.from(user);
    }

    // 내 정보 수정
    @Transactional
    public UserResponseDto updateMyInfo(Long userId, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 사용자명 중복 체크 (자신 제외)
        if (userUpdateDto.username() != null && !userUpdateDto.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userUpdateDto.username())) {
                throw new UserException(UserErrorCode.ALREADY_EXIST_USERNAME);
            }
        }

        // 이메일 중복 체크 (자신 제외)
        if (userUpdateDto.email() != null && !userUpdateDto.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userUpdateDto.email())) {
                throw new UserException(UserErrorCode.ALREADY_EXIST_EMAIL);
            }
        }

        user.updateProfile(userUpdateDto.username(), userUpdateDto.email());
        return UserResponseDto.from(user);
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(Long userId, PasswordChangeDto passwordChangeDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(passwordChangeDto.confirmPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_CURRENT_PASSWORD);
        }

        // 새 비밀번호와 확인 비밀번호 일치 여부 확인
        if (!passwordChangeDto.newPassword().equals(passwordChangeDto.confirmPassword())) {
            throw new UserException(UserErrorCode.PASSWORD_MISMATCH);
        }

        // 새 비밀번호 암호화 후 업데이트
        String encodedNewPassword = passwordEncoder.encode(passwordChangeDto.newPassword());
        user.changePassword(encodedNewPassword);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteAccount(Long userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        userRepository.delete(user);
    }

    // 로그아웃 (토큰 무효화)
    public void logout(String token) {
        jwtProvider.invalidateToken(token);
    }

}
