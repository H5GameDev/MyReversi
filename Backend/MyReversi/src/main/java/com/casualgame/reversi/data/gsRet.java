package com.casualgame.reversi.data;

public class gsRet {
	public int status;
	/*
	 * status:
	 * -1 uid not valid
	 * -2 session full
	 * -3 session id not valid
	 * -4 not valid move
	 */ 
	public String message;
	public String uid;
	public String sid;
	public String userSide;
	public String winner;
	public gStatus gstatus = new gStatus();
	
}
