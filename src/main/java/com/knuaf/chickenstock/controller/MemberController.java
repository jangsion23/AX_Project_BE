package com.knuaf.chickenstock.controller;

import com.knuaf.chickenstock.dto.ResponseDto;
import com.knuaf.chickenstock.dto.SignInDto;
import com.knuaf.chickenstock.dto.SignUpDto;
import com.knuaf.chickenstock.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody SignUpDto signUpDto) throws Exception {

        ResponseDto responseDto = memberService.join(signUpDto);

        return ResponseEntity.ok(responseDto);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignInDto signInDto) throws Exception {
        // 🚨 검문소: 데이터가 무사히 도착했는지 콘솔 창에 출력해서 확인합니다!
        System.out.println("========== 로그인 시도 ==========");
        System.out.println("입력받은 학번: " + signInDto.getStudentId());
        System.out.println("입력받은 비밀번호: " + signInDto.getPassword());
        System.out.println("===============================");

        ResponseDto responseDto = memberService.login(signInDto);
        return ResponseEntity.ok(responseDto);
    }
}