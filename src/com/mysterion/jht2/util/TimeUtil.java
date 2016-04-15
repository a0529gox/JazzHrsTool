package com.mysterion.jht2.util;

public class TimeUtil {

	public TimeUtil() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static long getMiliSecByHr(double hr) {
		if (hr % 0.5 != 0) {
			throw new RuntimeException();
		}
		Double time = hr * 60 * 60 * 1000;
		return time.longValue();
	}

}
