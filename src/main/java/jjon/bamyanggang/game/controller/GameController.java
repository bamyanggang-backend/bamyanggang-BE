package jjon.bamyanggang.game.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jjon.bamyanggang.game.service.GameService;
import jjon.bamyanggang.model.MafiaRole;
import jjon.bamyanggang.model.RoomUserInfo;

@RestController
public class GameController {

	@Autowired
	private GameService gameService;
	
	// [게임시작 버튼]
	// int형 room_no 값을 담아 요청 (Front-End)
	// int형 is_on_game 값을 담아 응답 (Back-End)
	@GetMapping("/api/getIsOnGame")
	public ResponseEntity<Map<String, Object>> getIsOnGame(@RequestParam("roomNo") int roomNo) {
		System.out.println("[게임시작 버튼] Controller 시작!");
		System.out.println("roomNo : " + roomNo);
		Map<String, Object> responseBody =new HashMap<String, Object>();
		try {
			int getIsOnGame = gameService.getIsOnGame(roomNo);
			responseBody.put("msg", "[게임시작 버튼] 성공!");
			responseBody.put("isOnGame", getIsOnGame);
			
			// 200
			return ResponseEntity.status(HttpStatus.OK).body(responseBody);
		} catch (Exception e) {
			responseBody.put("msg", "[게임시작 버튼] 실패ㅠ");
			
			// 500
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
		}
	}
	
	
	// [게임시작] 
	// int형 room_no 값을 담아 요청 (Front-End)
	// RoomUserInfo 객체에 방에 입장한 사용자들의 정보 값을 담아 응답 (Back-End)
	@GetMapping("/api/gameStart")
	public ResponseEntity<Map<String, Object>> gameStart(@RequestParam("roomNo") int roomNo) {
		System.out.println("[게임시작] Controller 시작!");
		Map<String, Object> responseBody = new HashMap<String, Object>();
		try {
			List<RoomUserInfo> getUserInfo = gameService.gameStart(roomNo);
			responseBody.put("msg", "[게임시작] 성공!");
			responseBody.put("사용자정보", getUserInfo);
			// 200
			return ResponseEntity.status(HttpStatus.OK).body(responseBody); 
		} catch (Exception e) {
			responseBody.put("msg", "[게임시작] 실패ㅠ");
			
			// 500
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody); 
		}
	}
	
	// [게임시작] 기준시간 조회
	// int형 room_no 값을 담아 요청 (Front-End)
	// Timestamp 객체에 저장된 시간 값을 담아 응답 (Back-End)
	@GetMapping("/api/getTime")
	public ResponseEntity<Map<String, Object>> getTime(@RequestParam("roomNo") int roomNo) {
		Map<String, Object> responseBody = new HashMap<String, Object>();
		try {
			Timestamp startTime = gameService.getTime(roomNo);
			responseBody.put("msg", "[게임시작] 시간 가져오기 성공!");
			responseBody.put("기준시간", startTime);
			
			return ResponseEntity.status(HttpStatus.OK).body(responseBody); 
		} catch (Exception e) {
			// TODO: handle exception
			responseBody.put("msg", "[게임시작] 시간 가져오기 실패ㅠ");
			
			// 500
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody); 
		}
	}
	
	// [투표]
	// MafiaRole 객체에 room_no, user_id 값을 담아 요청 (Front-End)
	@PostMapping("/api/vote")
	public ResponseEntity<Map<String, Object>> vote(@RequestBody MafiaRole mafiaRole) {
		System.out.println("[투표] Controller 시작!");
		Map<String, Object> responseBody =new HashMap<String, Object>();
		try {
			gameService.votePlus(mafiaRole);
			responseBody.put("msg", "[투표] 성공!");
			
			// 200
			return ResponseEntity.status(HttpStatus.OK).body(responseBody);
		} catch (Exception e) {
			responseBody.put("msg", "[투표] 실패ㅠ");
			
			// 500
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
		}
	}
	
	// [인게임]
	// int형 room_no 값을 담아 요청 (Front-End)
	// Map객체에 result, user_id, user_nicknm 값을 담아 응답 (Back-End)
	@GetMapping("/api/resultVote")
	public ResponseEntity<Map<String, Object>> resultVote (@RequestParam("roomNo") int roomNo) {
		System.out.println("[인게임] Controller 시작!");
		Map<String, Object> responseBody = new HashMap<String, Object>();
		try {
			Map<String, Object> resultVote = gameService.resultVote(roomNo);
			responseBody.put("msg", "[인게임] 성공!");
			responseBody.put("resultList", resultVote);
			
			// 200
			return ResponseEntity.status(HttpStatus.OK).body(responseBody);
		} catch (Exception e) {
			responseBody.put("msg", "[인게임] 실패ㅠ");
			
			// 500
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
		}
	}
	
	// [게임나가기]
	// MafiaRole 객체에 room_no, user_id 값을 담아 요청 (Front-End)
	@PostMapping("/api/gameOut")
	public ResponseEntity<Map<String, Object>> gameOut(@RequestBody MafiaRole mafiaRole) {
		System.out.println("[게임나가기] Controller 시작!");
		Map<String, Object> responseBody = new HashMap<String, Object>();
		try {
			gameService.gameOut(mafiaRole);
			responseBody.put("msg", "[게임나가기] 성공!");
			
			// 200
			return ResponseEntity.status(HttpStatus.OK).body(responseBody);
		} catch (Exception e) {
			responseBody.put("msg", "[게임나가기] 실패ㅠ");
			
			// 500
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
		}
	}
}
