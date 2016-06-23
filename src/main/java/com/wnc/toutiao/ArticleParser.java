package com.wnc.toutiao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wnc.basic.BasicFileUtil;
import com.wnc.string.PatternUtil;
import com.wnc.tools.SoupUtil;
import com.wnc.toutiao.db.DbExecMgr;
import com.wnc.toutiao.util.DbSaveDao;

public class ArticleParser {
	public static void main(String[] args) {
		new ArticleParser().parse();
	}

	List<String> articleUrls = new ArrayList<String>();

	public ArticleParser() {
		Map map = DbExecMgr.getSelectAllSqlMap("SELECT * FROM article order by id asc");
		for (int i = 1; i <= map.size(); i++) {
			Map rowMap = (Map) map.get(i);
			String articleUrl = String.valueOf(rowMap.get("URL"));
			articleUrls.add(articleUrl);
		}
	}

	public void parse() {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);

		Map map = DbExecMgr.getSelectAllSqlMap("SELECT * FROM author order by id asc");
		int size = map.size();
		Map rowMap;
		for (int i = 1; i <= size; i++) {
			rowMap = (Map) map.get(i);
			final String indexPage = String.valueOf(rowMap.get("URL"));
			executor.execute(new Runnable() {
				private int author_id;

				@Override
				public void run() {
					author_id = Integer.parseInt(PatternUtil.getLastPattern(indexPage, "\\d+"));
					Document doc = SoupUtil.getDoc(indexPage);
					Elements lastPageNode = doc.select(".last a");
					int maxPage = checkNode(lastPageNode) ? 0 : getMaxPage(lastPageNode);
					parseDoc(doc);
					for (int i = 2; i <= maxPage; i++) {
						doc = SoupUtil.getDoc(indexPage + "?page=" + i);
						parseDoc(doc);
					}
				}

				private boolean checkNode(Elements lastPageNode) {
					return lastPageNode == null || lastPageNode.first() == null;
				}

				private void parseDoc(Document doc) {
					Elements subjects = doc.select(".post");
					if (subjects == null) {
						logErr(indexPage + "找不到.post的节点");
					}
					for (Element e : subjects) {
						try {
							save(e);
						} catch (RuntimeException ex) {
							logErr(indexPage + " 数据库执行错误!" + ex.getMessage());
						} catch (Exception ex) {
							ex.printStackTrace();
							logErr(indexPage + " 页面解析错误!" + ex.getMessage());
						}
					}
				}

				private void save(Element e) {
					Element titleNode = e.select(".title a").first();
					String url = titleNode.absUrl("href");
					String title = StringEscapeUtils.escapeSql(titleNode.text());
					int agree_num = Integer.parseInt(e.select(".upvote span").first().text());
					int post_num = Integer.parseInt(e.select(".meta span").first().text());
					if (!articleUrls.contains(url))
						DbSaveDao.saveToArticle(url, title, author_id, agree_num, post_num);
				}

				private int getMaxPage(Elements lastPageNode) {
					return Integer.parseInt(PatternUtil.getLastPattern(lastPageNode.first().attr("href"), "\\d+"));
				}
			});
		}
		executor.shutdown();
	}

	public void logErr(String msg) {
		BasicFileUtil.writeFileString("article_err.txt", "Exception:" + msg + "\r\n", "GBK", true);
	}

}
