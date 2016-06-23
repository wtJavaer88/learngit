package com.wnc.toutiao.util;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 如何通过HttpURLConnection得到http 302的跳转地址
 * 
 * @author javaniu
 * 
 */
public class HttpLocationUtil {

	public static void main(String[] args) {
		String url = "http://toutiao.io/j/cu55tf";
		getLocation(url);
	}

	public static String getLocation(String url) {
		String location = null;
		String user_agent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0";
		String host = "http://toutiao.io";
		try {
			// System.out.println("访问地址:" + url);
			URL serverUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
			conn.setRequestMethod("GET");
			// 必须设置false，否则会自动redirect到Location的地址
			conn.setInstanceFollowRedirects(false);

			conn.addRequestProperty("Accept-Charset", "UTF-8;");
			conn.addRequestProperty("User-Agent", user_agent);
			conn.addRequestProperty("Referer", host);
			conn.connect();
			location = conn.getHeaderField("Location");

			serverUrl = new URL(location);
			conn = (HttpURLConnection) serverUrl.openConnection();
			conn.setRequestMethod("GET");

			conn.addRequestProperty("Accept-Charset", "UTF-8;");
			conn.addRequestProperty("User-Agent", user_agent);
			conn.addRequestProperty("Referer", host);
			conn.connect();
			System.out.println("跳转地址:" + location);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return location;
	}

}
