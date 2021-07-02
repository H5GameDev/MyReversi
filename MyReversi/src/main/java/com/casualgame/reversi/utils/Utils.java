package com.casualgame.reversi.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Utils {
	private static double random() {
		double random = Math.random();
		return random;
	}

	public static String genUID() throws NoSuchAlgorithmException {

		return new BigInteger(1, MessageDigest.getInstance("md5")
				.digest((new Date().toString() + " " + String.valueOf(random())).getBytes())).toString(16);
	}
}
