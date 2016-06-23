package com.wnc.toutiao;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.wnc.string.PatternUtil;
import com.wnc.tools.SoupUtil;

public class PageTest {
	public static void main(String[] args) {
		String indexPage = "http://toutiao.io/subjects/48828";
		Document doc = SoupUtil.getDoc(indexPage);
		Elements lastPageNode = doc.select(".last a");

		int maxPage = lastPageNode == null ? 0 : getMaxPage(lastPageNode);
		parseDoc(doc);
		for (int i = 2; i <= maxPage; i++) {
			doc = SoupUtil.getDoc(indexPage + "?page=" + i);
			parseDoc(doc);
		}
	}

	private static void parseDoc(Document doc) {
		System.out.println(doc.text());
	}

	private static int getMaxPage(Elements lastPageNode) {
		return Integer.parseInt(PatternUtil.getLastPattern(lastPageNode.first().attr("href"), "\\d+"));
	}
}
