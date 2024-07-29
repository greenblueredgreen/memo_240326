package com.memo.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/user")
@Controller
public class UserController {
	
	/**
	 * 회원가입 화면
	 * @return
	 */
	@GetMapping("/sign-up-view")
	public String signUpView() {
		// 가운데 레이아웃 조각만 내려주면 전체 레이아웃으로 구성된다.
		return "user/signUp";
	}
	
	/**
	 * 로그인 화면
	 * @return
	 */
	@GetMapping("/sign-in-view")
	public String signInView() {
		return "user/signIn";
	}
	
	/**
	 * 로그아웃 API
	 * @param session
	 * @return
	 */
	@RequestMapping("/sign-out")
	public String signOut(HttpSession session) {
		//session 비우기
		//session에 넣은 값들 모두 다 각각 비워줘야한다.
		session.removeAttribute("userId");
		session.removeAttribute("userloginId");
		session.removeAttribute("userName");
		
		// 로그인 페이지로 이동
		// 위 화면들은 여기서 요청못함. 밖에서 다시 요청해서 들어와야함
		// 그래서 redirect : 이미 있는 페이지로 이동시키는 것
		return "redirect:/user/sign-in-view";
	}
	
}
