package com.knuaf.chickenstock.service;

import com.knuaf.chickenstock.dto.*;
import com.knuaf.chickenstock.entity.Member;
import com.knuaf.chickenstock.entity.RefreshToken;
import com.knuaf.chickenstock.jwt.JwtTokenProvider;
import com.knuaf.chickenstock.repository.MemberRepository;
import com.knuaf.chickenstock.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public ResponseDto join(SignUpDto signUpDto) throws Exception {
        // 중복 학번 검사
        if (memberRepository.findByStudentId(signUpDto.getStudentId()).isPresent()) {
            throw new Exception("이미 가입된 학번 입니다.");
        }

        // 비밀번호 일치 확인
        if (!signUpDto.getPassword().equals(signUpDto.getCheckPassword())) {
            throw new Exception("비밀번호가 일치 하지 않습니다.");
        }

        // Member 엔티티 생성 (비밀번호는 반드시 encode해서 저장!)
        Member member = Member.builder()
                .studentId(signUpDto.getStudentId())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .name(signUpDto.getName())
                .college1(signUpDto.getCollege1())
                .college2(signUpDto.getCollege2())
                .major1(signUpDto.getMajor1())
                .major2(signUpDto.getMajor2())
                .profile_image("NULL")
                .roles(Collections.singletonList("ROLE_USER"))
                .build();

        memberRepository.save(member);

        return ResponseDto.builder()
                .status(200)
                .responseMessage("회원가입 성공")
                .data(null)
                .build();
    }

    public ResponseDto login(SignInDto signInDto) {
        // 1. 유저 확인
        Member member = memberRepository.findByStudentId(signInDto.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 학번입니다."));

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(signInDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 토큰 생성 (TokenDto인지 TokenInfo인지 Provider와 이름을 꼭 맞추세요!)
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getStudentId(), member.getRoles());

        // 4. RefreshToken 업데이트 로직
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByStudentId(member.getStudentId());

        if (refreshToken.isPresent()) {
            refreshTokenRepository.save(refreshToken.get().updateToken(tokenInfo.getRefreshToken()));
        } else {
            RefreshToken newToken = new RefreshToken(tokenInfo.getRefreshToken(), member.getStudentId());
            refreshTokenRepository.save(newToken);
        }

        return ResponseDto.builder()
                .status(200)
                .responseMessage("로그인 성공")
                .data(tokenInfo)
                .build();
    }
}