package com.wnc.toutiao.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.tools.SoupUtil;

public class HtmlModifySoup {
	public static void main(String[] args) {
		String savaPath = "2.html";
		String url = "https://docs.npmjs.com/how-npm-works/npm3#npm-v3-dependency-resolution?hmsr=toutiao.io&utm_medium=toutiao.io&utm_source=toutiao.io";
		url = "https://github.com/blog/1992-eight-lessons-learned-hacking-on-github-pages-for-six-months?hmsr=toutiao.io&utm_medium=toutiao.io&utm_source=toutiao.io";
		docModifyAndSaveFile(savaPath, url);
	}

	public static void docModifyAndSaveFile(String savaPath, String url) {
		Document doc = null;
		System.out.println(Thread.currentThread().getName() + ":" + url);
		try {
			doc = SoupUtil.getDoc(url);
		} catch (Exception ex) {
			throw new RuntimeException("失败" + ex.getMessage());
		}
		if (doc == null) {
			throw new RuntimeException("连接失败");
		}
		Elements links = doc.select("[href],[src]");
		if (links == null) {
			throw new RuntimeException(url + " 无任何url链接");
		}

		for (Element e : links) {
			String tag = "href";
			String name = e.attr(tag);
			if (BasicStringUtil.isNullString(name)) {
				tag = "src";
				name = e.attr(tag);
			}
			if (name.trim().startsWith("/")) {
				e.attr(tag, e.absUrl(tag));
			}
		}
		BasicFileUtil.writeFileString(savaPath, doc.toString(), "UTF-8", false);
	}
}
