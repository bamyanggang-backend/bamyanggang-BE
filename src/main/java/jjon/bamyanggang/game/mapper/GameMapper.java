package jjon.bamyanggang.game.mapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import jjon.bamyanggang.model.MafiaRole;
import jjon.bamyanggang.model.RoomUserInfo;

@Mapper
public interface GameMapper {
	
	// [게임시작 버튼] 게임 중 상태로 변경
	public void udtIsOnGame(int roomNo);
	
	// [게임시작 버튼] is_on_game 조회
	public int getIsOnGame(int roomNo);
	
	// [게임시작 버튼] 기준시간 설정
	public void setTime(int roomNo);

	//	[게임시작] mafia_vote 중복 확인
	public int cntRoomNoExists(int roomNo);
	
	// [게임시작] mafia_role 조회
	public List<RoomUserInfo> getUserInfo(int roomNo);
	
	// [게임시작] 역할 부여
	public void setRole(Map<String, Object> setParameter);
	
	// [게임시작] mafia_vote table 초기세팅
	public void initVote(int roomNo);
	
	// [게임시작] 기준시간 조회
	public Timestamp getTime(int roomNo);
	
	// [투표]
	public void votePlus(MafiaRole mafiaRole);
		
	// [인게임] 최대 투표자 수 조회
	public int cntMaxUser(int roomNo);
	
	// [인게임] 최대 투표자의 정보
	public RoomUserInfo getMaxRole(int roomNo);
	
	// [인게임] die
	public void setRoleSt(RoomUserInfo roomUserInfo);
	
	// [인게임] 생존자 수 조회
	public int cntSurvivor(int roomNo);
	
	// [인게임] 승패가 결정남
	public void delVote(int roomNo);
	
	// [인게임] 승패가 결정 안남
	public void resetVote(int roomNo);
	
	// [게임나가기] 정보삭제
	public void delInfo(MafiaRole mafiaRole);
	
	// [게임나가기] - join_cnt
	public void joinCntMinus(MafiaRole mafiaRole);
	
	// [게임나가기] join_cnt 조회
	public int getJoinCnt(MafiaRole mafiaRole);
	
	// [게임나가기] 방 삭제
	public void delRoom(MafiaRole mafiaRole);
	
}
