package com.memo.post;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.memo.post.bo.PostBO;
import com.memo.post.domain.Post;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/post")
@Controller
public class PostController {
	
	@Autowired
	private PostBO postBO;

	@GetMapping("/post-list-view")
	public String postListView(HttpSession session, Model model) {
		//로그인 여부 확인
		Integer userId = (Integer)session.getAttribute("userId");  //int는 null 저장 불가, Integer은 null 가능
		if(userId == null) {
		// 비로그인 상태 : null => 로그인 페이지로 redirect 이동
		return "redirect:/user/sign-in-view";  //메소드종료
		}
		
		//db조회 - 글목록
		List<Post> postList = postBO.getPostListByUserId(userId);
		
		// model 에 담기 (controller 하고 view는 끊어져 있기 때문에 model 이 연결해준다.)
		// 화면 같은 html 에서만 model이 존재
		model.addAttribute("postList", postList);
		
		return "post/postList";
	}
	
	@GetMapping("/post-create-view")
	public String postCreateView() {
		return "post/postCreate";
	}
}
