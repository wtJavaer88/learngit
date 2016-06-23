package com.wnc.toutiao.util;

import org.jsoup.nodes.Document;

import com.wnc.tools.SoupUtil;

/**
 * 用于嵌套解析网页,给定一个首页和规则,自动解析同级的所有网页
 * 
 * @author cpr216
 *
 */
public abstract class NestParser {
	String indexPage;
	String urlFormat = "%d";
	int maxPage;

	public NestParser(String page, String urlFormat) {
		this.indexPage = page;
		this.urlFormat = urlFormat;
	}

	public void start() throws Exception {
		parseWebAndGetMaxPage(indexPage);
		for (int i = 2; i <= maxPage; i++) {
			parseWebAndGetMaxPage(getFormatUrl(i));
		}
	}

	private void parseWebAndGetMaxPage(String web) {
		Document doc = SoupUtil.getDoc(web);
		parseDoc(doc);
		if (maxPage == 0)
			maxPage = getMaxPages(doc);
	}

	/**
	 * 根据页数,格式化当前网页地址,可以被子类继承修改
	 * 
	 * @param i
	 *            当前页
	 * @return
	 */
	protected String getFormatUrl(int i) {
		return String.format(urlFormat, i);
	}

	public abstract int getMaxPages(Document doc);

	/**
	 * 获取当前页中所需内容,如a和img
	 * 
	 * @param doc
	 */
	public abstract void parseDoc(Document doc);

}
