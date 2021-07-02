package com.casualgame.reversi.game;

import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Reversi {
	// Black side :'B'
	// White side :'W'
	char[][] chessBoard = new char[8][8];

	public void newGame() {
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				chessBoard[i][j] = 0;
		chessBoard[3][3] = 'W';
		chessBoard[4][3] = 'B';
		chessBoard[4][4] = 'W';
		chessBoard[3][4] = 'B';
	}

	public int currentScore(char side) {
		int sum = 0;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				if (chessBoard[i][j] == side)
					sum++;
		return sum;
	}

	public char currentTurn = 'B';
	//dir U,D,L,R,LU,RU,RD,LD
	int dir_x[] = {+0,+0,-1,+1,-1,+1,+1,-1};
	int dir_y[] = {-1,+1,+0,+0,-1,+1,-1,+1};
	public boolean place(char side, int x, int y) {
		char opponent=0;if(side == 'W')opponent = 'B';else opponent = 'W';
		int checked_dir = 0;
		if (currentTurn != side)
			return false;
		if ((checked_dir = ifMoveValid(side, x, y)) == 0)
			return false;
		chessBoard[x][y] = side;
		int current_x = 0;int current_y =0;
		for(int i=0;i<8;i++) {
			if((checked_dir & 1<<i)==0)continue;
			current_x = x;
			current_y = y;
			current_x += dir_x[i];
			current_y += dir_y[i];
			if(!inBoard(current_x,current_y))continue;
			if(chessBoard[current_x][current_y]!=opponent)continue;
			while(inBoard(current_x,current_y)) {
				chessBoard[current_x][current_y]=side;
				current_x += dir_x[i];
				current_y += dir_y[i];
				if(chessBoard[current_x][current_y] == side)break;
			}
		}
		
		currentTurn = (currentTurn == 'B' ? 'W' : 'B');
		if(ifHasMove(currentTurn)) {
			
		}else {
			currentTurn = (currentTurn == 'B' ? 'W' : 'B');
		}
		return true;
	}

	public int ifMoveValid(char side, int x, int y) {
		//Check eight direction
		//Only valid if immediate follow by series of opponent and followed by an side.
		int ret = 0;
		char opponent=0;
		if(chessBoard[x][y]!=0)return 0;
		if(side == 'W')opponent = 'B';else opponent = 'W';
		int current_x = 0;int current_y =0;
		for(int i=0;i<8;i++) {
			current_x = x;
			current_y = y;
			current_x += dir_x[i];
			current_y += dir_y[i];
			if(!inBoard(current_x,current_y))continue;
			if(chessBoard[current_x][current_y]!=opponent)continue;
			while(inBoard(current_x,current_y)) {
				current_x += dir_x[i];
				current_y += dir_y[i];
				if(inBoard(current_x,current_y) && chessBoard[current_x][current_y] == side) {
					ret |= 1<<i;
					break;
				}
			}
		}
		return ret;
	}
	public boolean inBoard(int x,int y) {
		if(0<=x && x<=7 && 0<=y && y <=7)return true;
		return false;
	}
	public boolean ifHasMove(char side) {
		for(int i=0;i<8;i++)
			for (int j=0;j<8;j++) {
				if(ifMoveValid(side,i,j)!=0)return true;
			}
		return false;
	}

	public boolean ifHasMove() {
		return ifHasMove('B') || ifHasMove('W');
	}

	public char[][] getChessboard() {
		return chessBoard;
	}
	public String toString() {
		String ret = "";
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				if(chessBoard[i][j]!=0) {
					ret=ret+chessBoard[i][j];
					ret=ret+',';
				}else {
					ret=ret+'^';
					ret=ret+',';
				}
				
			}
			ret = ret.substring(0, ret.length()-1) + ';';
		}
		return ret;
	}
	
	
}