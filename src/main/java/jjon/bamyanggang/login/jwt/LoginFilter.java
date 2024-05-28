package jjon.bamyanggang.login.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jjon.bamyanggang.login.entity.RefreshEntity;
import jjon.bamyanggang.login.repository.RefreshRepository;

public class LoginFilter extends UsernamePasswordAuthenticationFilter { 

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final RefreshRepository refreshRepository;
	
	public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshRepository refreshRepository) 
	{		
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.refreshRepository = refreshRepository;
		setFilterProcessesUrl("/api/login");
		// api 로 url 변경
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		
		 try {
		        // 요청에서 JSON 데이터를 파싱하여 username과 password 추출
		        ObjectMapper objectMapper = new ObjectMapper();
		        JsonNode jsonNode = objectMapper.readTree(request.getInputStream());
		        String username = jsonNode.get("userId").textValue();
		        String password = jsonNode.get("userPw").textValue();
		        
		        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);
		        return authenticationManager.authenticate(authToken);
		       
		    } catch (IOException e) {
		        throw new RuntimeException("Could not read JSON request", e);
		    }
	}
	
		@Override
		protected void successfulAuthentication( HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication )
		{ 
		    String username = authentication.getName();
		    System.out.println(username);
		    
		    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		    GrantedAuthority auth = iterator.next();
		    String role = auth.getAuthority();

		    String access = jwtUtil.createJwt("access", username, role, 600000L);
		    String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);
		    addRefreshEntity(username, refresh, 86400000L);
		    response.setHeader("access", access);
		    response.addHeader("Authorization", access);
		    response.addCookie(createCookie("refresh", refresh) ); // 수정
		    response.addHeader("refresh", access);
		    response.setStatus(HttpStatus.OK.value());
		}
		@Override
		protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse reponse,  AuthenticationException failed )
		{	
			reponse.setStatus(401);
			// 로그인 실패시 에러.
		}
		
		private void addRefreshEntity(String username, String refresh, Long expiredMs) {
		
			Date date = new Date(System.currentTimeMillis() + expiredMs);		
			RefreshEntity refreshEntity = new RefreshEntity();
			refreshEntity.setUsername(username);
			refreshEntity.setRefresh(refresh);
			refreshEntity.setExpiration(date.toString());	
			refreshRepository.save(refreshEntity);
		}
		private Cookie createCookie(String key, String value ) { // 수정
			Cookie cookie = new Cookie(key, value);
		    cookie.setMaxAge(24*60*60);
		    cookie.setSecure(true);
		    cookie.setPath("/");
		    cookie.setAttribute("SameSite", "None");
		    cookie.setHttpOnly(true);
		   		    
		    return cookie;
		}	
}
