package com.memo.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memo.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer>{
	
	// JPQL
	// bo가 String loginId 넘겨준다
	/**
	 * 아이디 중복확인 -> 객체 UserEntity 로 user 행을 리턴해준다. 
	 * @param loginId
	 * @return
	 */
	public UserEntity findByLoginId(String loginId);
	
	/**
	 * 로그인메소드 : 아이디, 비번으로 로그인하기
	 * @param loginId
	 * @param password
	 * @return
	 */
	public UserEntity findByLoginIdAndPassword(String loginId, String password);
}
