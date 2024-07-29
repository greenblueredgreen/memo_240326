package com.memo.post;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.memo.post.bo.PostBO;
import com.memo.post.domain.Post;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/post")
@Controller
public class PostController {
	
	@Autowired
	private PostBO postBO;

	/**
	 * 글 목록 화면 (이전, 다음)
	 * @param prevIdParam
	 * @param nextIdParam
	 * @param session
	 * @param model
	 * @return
	 */
	@GetMapping("/post-list-view")
	public String postListView(
			@RequestParam(value="prevId", required = false) Integer prevIdParam, //이전
			@RequestParam(value="nextId", required = false) Integer nextIdParam, //다음
			HttpSession session, Model model) {
		
		//로그인 여부 확인
		Integer userId = (Integer)session.getAttribute("userId");  //int는 null 저장 불가, Integer은 null 가능
		if(userId == null) {
			// 비로그인 상태 : null => 로그인 페이지로 redirect 이동
			return "redirect:/user/sign-in-view";  //메소드종료
		}
		
		//db조회 - 글목록
		List<Post> postList = postBO.getPostListByUserId(userId, prevIdParam, nextIdParam);
		int prevId = 0;
		int nextId = 0;
	    if(postList.isEmpty() == false) { // 글 목록이 비어있지 않을 때 페이징 정보
	    	prevId = postList.get(0).getId(); // 첫번째 칸 id
	    	nextId = postList.get(postList.size() -1 ).getId(); //마지막칸 id
	    	
	    	//이전 방향의 끝인가? 그러면 0
	    	//prevId  테이블의 제일 큰 숫자와 같으면 이전의 끝 페이지
	    	if(postBO.isPrevLastPageByUserId(userId, prevId)) {
	    		prevId = 0;
	    	}
	    	
	    	//다음 방향의 끝인가? 그러면 0 
	    	//nextId와 테이블의 제일 작은 숫자가 같으면 다음의 끝페이지
	    	if(postBO.IsNextLastPageByUserId(userId, nextId)) {
	    		nextId = 0;
	    	}
	    }
		
		// model 에 담기 (controller 하고 view는 끊어져 있기 때문에 model 이 연결해준다.)
		// 화면 같은 html 에서만 model이 존재
	    model.addAttribute("prevId", prevId);
	    model.addAttribute("nextId", nextId);
		model.addAttribute("postList", postList);
	
		return "post/postList";
	}
	
	/**
	 * 글쓰기 화면
	 * @return
	 */
	@GetMapping("/post-create-view")
	public String postCreateView() {
		return "post/postCreate";
	}
	
	@GetMapping("/post-detail-view")
	public String postDetailView(
			@RequestParam("postId") int postId,
			Model model, HttpSession session) {
		
		//db조회 - userId, postId
		int userId = (int)session.getAttribute("userId");
		Post post = postBO.getPostByPostIdUserId(userId, postId);
		
		//model 에 담기
		model.addAttribute("post", post);
		
		//화면이동
		return "post/postDetail";
	}
}
