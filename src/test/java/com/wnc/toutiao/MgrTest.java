package com.wnc.toutiao;

import java.io.IOException;

import org.jsoup.Jsoup;

public class MgrTest {
	public static void main(String[] args) throws IOException {
		String page = "http://manage.taotao.com/rest/content?categoryId=31&page=1&rows=20";
		System.out.println(Jsoup.connect(page).ignoreContentType(true).execute().body());
	}
}
