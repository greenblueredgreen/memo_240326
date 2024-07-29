package com.memo.post.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import com.memo.post.domain.Post;

@Mapper
public interface PostMapper {
	
	public List<Map<String, Object>> selectPostListTest();
	
	public List<Post> selectPostListByUserId(
			
			@Param("userId")int userId,
			@Param("standardId") Integer standardId,
			@Param("direction") String direction,
			@Param("limit") int limit);
	
	//select 결과가 postId : 리턴 타입은 postId(int)
	// 마지막 페이지 아이디 판별
	public int selectPostIdByUserIdAsSort(
			@Param("userId")int userId,
			@Param("sort") String sort
			);
	
	/**
	 * 글 생성 메소드
	 * @param userId
	 * @param subject
	 * @param content
	 * @param imagePath
	 */
	public void insertPost(
			@Param("userId") int userId, 
			@Param("subject") String subject, 
			@Param("content") String content, 
			@Param("imagePath") String imagePath);
			//multipart는 DB에 못들어간다
			//String 으로 바꿔줘야한다. 
	
	/**
	 * 글 수정, 삭제 메소드에서 post있나 확인 후 수정, 삭제할 글 가져오는 메소드 
	 * @param userId
	 * @param postId
	 * @return Post (글 가져오기 때문에 Post로 리턴)
	 */
	public Post selectPostByPostIdUserId(
			@Param("userId") int userId, 
			@Param("postId") int postId);
	
	/**
	 * 글 수정 메소드 
	 * @param postId
	 * @param subject
	 * @param content
	 * @param imagePath
	 */
	public void updatePostByPostId(
			@Param("postId") int postId, 
			@Param("subject") String subject, 
			@Param("content") String content, 
			@Param("imagePath") String imagePath);
	
	/**
	 * 글 삭제 메소드
	 * @param postId
	 * @return
	 */
	public int deletePostByPostId(int postId);
}
