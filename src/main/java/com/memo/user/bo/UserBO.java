package com.memo.user.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.memo.user.entity.UserEntity;
import com.memo.user.repository.UserRepository;

@Service
public class UserBO {
	
	@Autowired
	private UserRepository userRepository;

	/**
	 * 아이디 중복확인 DB조회 메소드
	 * @param loginId
	 * @return
	 */
	//input : login
	//output : UserEntity 채워져있거나 null
	public UserEntity getUserEntityByLoginId(String loginId) {
		return userRepository.findByLoginId(loginId);
	}
	

	/**
	 * 회원가입 메소드
	 * @param loginId
	 * @param password
	 * @param name
	 * @param email
	 * @return
	 */
	// input : 4개 파라미터 (password라 해도 된다-> 어차피 controller에서 해싱된 값을 넘겨주기 때문이다)
	// UserEntity
	public UserEntity addUser(String loginId, String password, String name, String email) {
		// save 메소드 -> DB에 없을 경우 insert문 실행
		return userRepository.save(UserEntity.builder()
				.loginId(loginId)
				.password(password)
				.name(name)
				.email(email)
				.build());
	}
	
	/**
	 * 로그인 DB 조회 메소드
	 * @param loginId
	 * @param password
	 * @return
	 */
	// input : loginId, password
	// output : UserEntity or null
	public UserEntity getUserEntityByLoginIdPassword(String loginId, String password) {
		return userRepository.findByLoginIdAndPassword(loginId, password);
	}
}
