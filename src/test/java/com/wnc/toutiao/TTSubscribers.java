package com.wnc.toutiao;

import org.jsoup.nodes.Document;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.string.PatternUtil;
import com.wnc.toutiao.util.NestParser;

public class TTSubscribers {
	public static void main(String[] args) {
		try {
			String indexPage = "http://toutiao.io/subjects/4/subscribers";
			doJob(indexPage);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	private static void doJob(String indexPage) throws Exception {
		new NestParser(indexPage, "http://toutiao.io/subjects/4/subscribers?page=%d") {
			@Override
			public void parseDoc(Document doc) {
				System.out.println(doc.title());
			}

			@Override
			public int getMaxPages(Document doc) {
				System.out.println(doc.select(".last"));
				try {
					String max = PatternUtil.getLastPattern(doc.select(".last a").first().attr("href"), "\\d+");
					return BasicNumberUtil.getNumber(max);
				} catch (Exception ex) {
					throw new RuntimeException("请检查节点匹配是否正确,找最大页异常:" + ex.getMessage());
				}
			}

		}.start();
	}
}
