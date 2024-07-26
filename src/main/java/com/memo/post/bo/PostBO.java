package com.memo.post.bo;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.domain.Post;
import com.memo.post.mapper.PostMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j  //lombok
@Service
public class PostBO {
	
	//SLF4J 로거로 선택!! LoggerFactory, Logger
	//private Logger log = LoggerFactory.getLogger(PostBO.class);
	//private Logger log = LoggerFactory.getLogger(this.getClass());
	//lombok 어노테이션
	
	@Autowired
	private PostMapper postMapper;
	
	@Autowired
	private FileManagerService fileManagerService;
	
	//페이징 정보 필드(limit)
	private static final int POST_MAX_SIZE = 3;
	
	
	// input: 로그인 된 사람의 userId + 이전, 다음
	// output: List<Post>
	public List<Post> getPostListByUserId(int userId, Integer prevId, Integer nextId) {
		// 게시글 번호 10/9/8| 7/6/5 | 4/3/2 | 1
		// 만약 4 3 2 페이지에 있을 때
		// 1) 다음 : 2보다 작은 3개 DESC
		// 2) 이전 : 4보다 큰 3개 ASC => 5 6 7  => BO에서 REVERSE 7 6 5
		// 3) 페이징 없을 때 : 최신순 3 DESC
		
		Integer standardId = null; //기준 postId
		String direction = null; //방향
		if(prevId != null) { // 2) 이전
			standardId = prevId;
			direction = "prev";
			
			List<Post> postList = postMapper.selectPostListByUserId(userId, standardId, direction, POST_MAX_SIZE);
			// [5,6,7] => [7,6,5]
			Collections.reverse(postList); //뒤집고 저장
			
			return postList;
					
		} else if(nextId != null) { // 1) 다음
			standardId = nextId;
			direction = "next";
		} 
		
		// 3) 페이징 x, 1) 다음
		return postMapper.selectPostListByUserId(userId, standardId, direction, POST_MAX_SIZE);
	}
	
	//이전 페이지의 마지막인가?
	public boolean isPrevLastPageByUserId(int userId, int prevId) {
		// 가장 큰 아이디
		int maxPostId = postMapper.selectPostIdByUserIdAsSort(userId, "DESC");
		return maxPostId == prevId; //같으면 마지막!
	}
	
	//다음 페이지의 마지막인가?
	public boolean IsNextLastPageByUserId(int userId, int nextId) {
		// 가장 작은 아이디
		int minPostId = postMapper.selectPostIdByUserIdAsSort(userId, "ASC");
		return minPostId == nextId;  //같으면 마지막!
	}
	
	//input : userId, postId
	//output : Post or null
	public Post getPostByPostIdUserId(int userId, int postId) {
		return postMapper.selectPostByPostIdUserId(userId, postId);
	}
	
	// input: 파라미터들
	// output: X
	public void addPost(int userId, String userLoginId, 
			String subject, String content, MultipartFile file) {
		
		String imagePath = null;
		
		if (file != null) {
			// 업로드 할 이미지가 있을 때에만 업로드
			imagePath = fileManagerService.uploadFile(file, userLoginId);
		}
		
		postMapper.insertPost(userId, subject, content, imagePath);
	}
	
	//input : 파라미터들
	//output : x
	public void updatePostByPostId(
			int userId, String loginId, 
			int postId, String subject, 
			String content, MultipartFile file) {
		
		//기존글 가져오기(1. 이미지 교체시 삭제하기 위해 2. 업데이트 대상이 있는지 확인하기 위해서)
		Post post = postMapper.selectPostByPostIdUserId(userId, postId);
		if (post == null) {
			log.warn("[글 수정] post is null. userId:{}, postId:{}", userId, postId);
			return;
		}
		
		// 파일이 있으면
		// 1) 새 이미지를 업로드
		// 2) 1번 단계가 성공하면 기존 이미지가 있을 때 삭제
		String imagePath = null;
		
		if(file != null) {
			// 새 이미지 업로드
			imagePath = fileManagerService.uploadFile(file,loginId);
		
			//업로드 성공 시(imagePath != null) 기존 이미지가 있으면 제거
			if(imagePath != null && post.getImagePath() != null) {
				//폴더와 이미지 제거(서버에서)
				fileManagerService.deleteFile(post.getImagePath());
				
			}
		}
		
		// db update
		postMapper.updatePostByPostId(postId, subject, content, imagePath);
		
	}
	
	//input : postId, userId
	//output : x
	public void deletePostByPostIdUserId(int postId, int userId) {
		// 기존 글 가져오기 (이미지 존재시 이미지를 삭제하기 위해 기존 글 가져와야만 한다)
		Post post = postMapper.selectPostByPostIdUserId(userId, postId);  //이미 기존에 있다
		// 이미 있는 글을 가져오기 때문에 무조건 존재한다
		if(post == null) { //삭제할 대상(글)이 없을 때
			log.info("[글 삭제] post is null. postId : {}, userId : {}", postId, userId);
			return;
		}
		
		// post db delete
		// 삭제한 행 개수 리턴(성공한 행 개수) -> 그 후 이미지 삭제
		int rowCount = postMapper.deletePostByPostId(postId);
		
		// TODO 이미지가 존재하면 삭제 and 삭제된 행도 1일 때
		if(rowCount > 0 && post.getImagePath() != null) { 
			fileManagerService.deleteFile(post.getImagePath());
		}
	}
}
