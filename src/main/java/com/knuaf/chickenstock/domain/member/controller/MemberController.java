package com.knuaf.chickenstock.domain.member.controller;

import com.knuaf.chickenstock.domain.member.dto.ResponseDto;
import com.knuaf.chickenstock.domain.member.dto.SignInDto;
import com.knuaf.chickenstock.domain.member.dto.SignUpDto;
import com.knuaf.chickenstock.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpDto signUpDto) throws Exception {

        ResponseDto responseDto = memberService.signup(signUpDto);

        return ResponseEntity.ok(responseDto);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignInDto signInDto) throws Exception {

        ResponseDto responseDto = memberService.login(signInDto);
        return ResponseEntity.ok(responseDto);
    }
}
