package com.casualgame.reversi.service;

import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.casualgame.reversi.data.gsRet;
import com.casualgame.reversi.game.*;
import com.casualgame.reversi.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.*;

@Service
public class SessionService {
	public class Session {

		public String[] uId = new String[2]; // [0]:black [1]:white
		public boolean[] ready = new boolean[2];
		public Reversi reversi = new Reversi();
		public int status = 0;

		public static final int STATUS_NOT_ENOUGH_PLAYER = 0;
		public static final int STATUS_WAIT_READY = 1;
		public static final int STATUS_PLAYING = 2;
		public static final int STATUS_GAME_END = 3;
		/*
		 * status: 0: not enough player 1: waiting for ready 2: playing 3: game finished
		 */

	}

	class User {
		public String uId;
		public Date expired;
		public String sId = null;
	}

	private static final Logger LOG = LoggerFactory.getLogger(SessionService.class);
	HashMap<String, Session> hMap = new HashMap<String, Session>();
	List<User> userList = new ArrayList<User>();

	public int findUser(String uId) {
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).uId.equals(uId))
				return i;
		}
		return -1;
	}

	@SuppressWarnings("deprecation")
	public void expireTick() {
		Date currentTime = new Date();
		for (int i = 0; i < userList.size(); i++) {

			if (userList.get(i).expired.before(currentTime)) {
				// expired;
				for (Map.Entry<String, Session> entry : hMap.entrySet()) {
					for (int j = 0; j < 2; j++)
						if (entry.getValue().uId[j] == userList.get(i).uId) {
							entry.getValue().uId[j] = null;
						}
					if (entry.getValue().uId[0] == null && entry.getValue().uId[1] == null)
						hMap.remove(entry.getKey());
				}
				userList.remove(i);

			}
		}

		for (Map.Entry<String, Session> entry : hMap.entrySet()) {
			for (int i = 0; i < 2; i++)
				if (findUser(entry.getValue().uId[i]) < 0) {
					entry.getValue().uId[i] = null;
				}
			if (entry.getValue().uId[0] == null && entry.getValue().uId[1] == null) {
				hMap.remove(entry.getKey());
			}
		}

	}

	public String joinSession(String uId, String sId) {
		gsRet ret = new gsRet();
		int userIndex = findUser(uId);
		if (userIndex < 0 || uId == null) {
			// no ok
			ret.status = -1;
			ret.message = "UID EXPIRED / NOT EXIST";
		} else if (hMap.containsKey(sId)) {
			extendExpire(userIndex);
			if (hMap.get(sId).uId[0] != null && hMap.get(sId).uId[1] != null) {
				// full
				if (hMap.get(sId).uId[0].equals(uId) || hMap.get(sId).uId[1].equals(uId)) {
					ret.status = hMap.get(sId).status;
					ret.message = "ALREADY JOINED";
					ret.sid = sId;
				} else {
					ret.status = -2;
					ret.message = sId + " FULL";
					ret.sid = null;
				}
			} else {

				if (hMap.get(sId).uId[0] == null) {
					// join as black
					hMap.get(sId).uId[0] = uId;
					userList.get(userIndex).sId = sId;

					ret.userSide = "BLACK";
				} else if (hMap.get(sId).uId[1] == null && !hMap.get(sId).uId[0].equals(uId)) {
					// join as white
					hMap.get(sId).uId[1] = uId;
					userList.get(userIndex).sId = sId;
					ret.userSide = "WHITE";

				}

				ret.sid = sId;
				ret.status = Session.STATUS_WAIT_READY;

			}
		} else {
			// create session
			if (sId == null || sId.equals("")) {
				// not valid room id
				ret.status = -3;
				ret.message = "SESSION ID NOT VALID";
				ret.sid = null;
			} else {
				hMap.put(sId, new Session());
				hMap.get(sId).uId[0] = uId;
				hMap.get(sId).ready[0] = false;
				hMap.get(sId).ready[1] = false;
				ret.message = "NEW SESSION CREATED";
				userList.get(userIndex).sId = sId;
				ret.sid = sId;
				ret.userSide = "BLACK";
				ret.status = Session.STATUS_NOT_ENOUGH_PLAYER;
			}
		}
		Gson gson = new Gson();
		return gson.toJson(ret);

	}

	@SuppressWarnings("deprecation")
	public void extendExpire(int i) {

		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.MINUTE, 2);  
		date = calendar.getTime();  
		userList.get(i).expired = date;

	}

	public String ready(String uId, String sId) {
		gsRet ret = new gsRet();
		int userIndex = findUser(uId);
		if (userIndex < 0 || uId == null) {
			// no ok
			ret.status = -1;
			ret.message = "UID EXPIRED / NOT EXIST";
		} else if (hMap.containsKey(userList.get(userIndex).sId)) {
			Session session = hMap.get(userList.get(userIndex).sId);
			if (session.uId[0].equals(uId)) {
				session.ready[0] = !session.ready[0];
				ret.userSide = "BLACK";
			} else if (session.uId[1].equals(uId)) {
				session.ready[1] = !session.ready[1];
				ret.userSide = "WHITE";
			}
			extendExpire(userIndex);
			freshSessionStatus(userList.get(userIndex).sId);
			ret.status = session.status;
			ret.gstatus.bCount = session.reversi.currentScore('B');
			ret.gstatus.wCount = session.reversi.currentScore('W');
			ret.gstatus.chessBoard = session.reversi.toString();
			if(ret.status == Session.STATUS_PLAYING) {
				LOG.info("SESSION "+sId+" START PLAYING.");
			}
		}
		Gson gson = new Gson();
		return gson.toJson(ret);
	}

	public String login() throws NoSuchAlgorithmException {
		// generate a uId and return the uId
		String uid = Utils.genUID();
		User nUser = new User();
		nUser.uId = uid;
		nUser.expired = new Date();

		userList.add(nUser);
		extendExpire(userList.size() - 1);
		return uid;
	}

	public void freshSessionStatus(String sId) {
		if (!hMap.containsKey(sId)) {
			return;

		}
		Session session = hMap.get(sId);

		if (session.uId[0] == null || session.uId[1] == null)
			session.status = Session.STATUS_NOT_ENOUGH_PLAYER;
		else if (session.ready[0] != true && session.ready[1] != true)
			session.status = Session.STATUS_WAIT_READY;

		if (session.ready[0] && session.ready[1] && session.uId[0] != null && session.uId[1] != null) {
			if ((session.reversi.currentScore('B') + session.reversi.currentScore('W')) >= 64) {
				session.status = Session.STATUS_GAME_END;
			} else {
				if (session.status != Session.STATUS_PLAYING) {
					session.reversi.newGame();
				}
				session.status = Session.STATUS_PLAYING;
				
			}
		}
	}

	// status:
	// not enough player
	// waiting for ready
	// your turn
	// waiting for opponent
	// game finished
	public String getStatus(String uId) {
		gsRet ret = new gsRet();
		int userIndex = findUser(uId);
		if (userIndex < 0 || uId == null) {
			// no ok
			ret.status = -1;
			ret.message = "UID EXPIRED / NOT EXIST";
		}

		else if (hMap.containsKey(userList.get(userIndex).sId)) {

			freshSessionStatus(userList.get(userIndex).sId);
			Session session = hMap.get(userList.get(userIndex).sId);
			extendExpire(userIndex);
			ret.status = session.status;
			ret.gstatus.chessBoard = session.reversi.toString();
			ret.gstatus.bCount = session.reversi.currentScore('B');
			ret.gstatus.wCount = session.reversi.currentScore('W');
			ret.gstatus.currentSide = session.reversi.currentTurn == 'W'? "WHITE":"BLACK";
			if (ret.gstatus.bCount + ret.gstatus.wCount >= 64) {
				// game ended
				if (ret.gstatus.bCount > ret.gstatus.wCount) {
					ret.winner = session.uId[0];
				} else {
					ret.winner = session.uId[1];
				}
				session.ready[0] = false;
				session.ready[1] = false;
				
				
				
				if (ret.winner .equals(uId))
					ret.message = " YOU WON";
				else
					ret.message = " YOU LOST";

			}
		} else {
			extendExpire(userIndex);
			ret.status = -3;
			ret.message = "SESSION ID NOT FOUND";
		}
		Gson gson = new Gson();
		return gson.toJson(ret);

	}

	public String play(String sId, String uId, int x, int y) {
		gsRet ret = new gsRet();

		int userIndex = findUser(uId);
		if (userIndex < 0 || uId == null) {
			// no ok
			ret.status = -1;
			ret.message = "UID EXPIRED / NOT EXIST";
		} else if (hMap.containsKey(userList.get(userIndex).sId) && userList.get(userIndex).sId .equals(sId)) {
			freshSessionStatus(userList.get(userIndex).sId);
			extendExpire(userIndex);
			Session session = hMap.get(sId);
			char side = session.uId[0] .equals(uId )? 'B' : 'W';
			boolean valid = session.reversi.place(side, x, y);
			if (valid) {
				//
				ret.status = session.status;
			} else {
				ret.status = -4;
			}
			ret.gstatus.chessBoard = session.reversi.toString();
			ret.gstatus.bCount = session.reversi.currentScore('B');
			ret.gstatus.wCount = session.reversi.currentScore('W');
			if (ret.gstatus.bCount + ret.gstatus.wCount >= 64) {
				// game ended
				ret.status = Session.STATUS_GAME_END;
				if (ret.gstatus.bCount > ret.gstatus.wCount) {
					ret.winner = session.uId[0];
				} else {
					ret.winner = session.uId[1];
				}
				if (ret.winner .equals(uId))
					ret.message = "YOU WON";
				else
					ret.message = "YOU LOST";

			}
		}
		Gson gson = new Gson();
		return gson.toJson(ret);
	}

	public void printStatus() {
		// TODO Auto-generated method stub
		LOG.info("CURRENT SESSION: " + hMap.size() + "; CURRENT USER: " + userList.size());
	}
}
