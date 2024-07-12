package com.memo.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.memo.common.EncryptUtils;
import com.memo.user.bo.UserBO;
import com.memo.user.entity.UserEntity;

//api만 모아놓음
@RequestMapping("/user")
@RestController
public class UserRestController {
	
		@Autowired
		private UserBO userBO;
		
		/**
		 * 아이디 중복확인 API
		 * @param loginId
		 * @return
		 */
		//http:localhost/user/is-duplicated-id
		@RequestMapping("/is-duplicated-id")
		public Map<String, Object> isDuplicatedId(
				@RequestParam("loginId") String loginId){
			
			//db조회
			UserEntity user = userBO.getUserEntityByLoginId(loginId); 
			
			//응답값
			Map<String, Object> result = new HashMap<>();
			result.put("code", 200);
			if(user != null) { //이미 존재한다면 (중복이라면)
				result.put("is_duplicated_id", true);
			} else {
				result.put("is_duplicated_id", false);
			}
			return result;
		}
		
		@PostMapping("/sign-up")
		public Map<String, Object> signUp(
				@RequestParam("loginId") String loginId,
				@RequestParam("password") String password,
				@RequestParam("name") String name,
				@RequestParam("email") String email){
			
			// 비밀번호를 db에 그대로 넣으면 안된다
			// 비밀번호 암호화 :  md5 알고리즘 => 해싱 hashing : 복호화 불가
			// aaaa => 74b8733745420d4d33f80c4663dc5e5
			// aaaa => 74b8733745420d4d33f80c4663dc5e5
			String hashedPassword  = EncryptUtils.md5(password);   
			
			// db insert
			// password 말고 hashedPassword 보내야한다!
			UserEntity user = userBO.addUser(loginId, hashedPassword, name, email);
			
			// 응답값
			Map<String, Object> result = new HashMap<>();
			if(user != null) {
				result.put("code", 200);
				result.put("result", "성공");
			} else {
				result.put("code", 500);
				result.put("error_message", "회원가입 실패했습니다");
			}
			return result;
		}
}
