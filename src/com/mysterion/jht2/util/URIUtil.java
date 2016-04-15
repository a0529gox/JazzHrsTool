package com.mysterion.jht2.util;

import java.net.URI;

import com.mysterion.jht2.log.AnnoyLogger;

public class URIUtil {

	public URIUtil() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static URI get(String str) {
		try {
			return new URI(str);
		} catch (Exception e) {
			AnnoyLogger.severe(e);
		}
		return null;
	}
}
