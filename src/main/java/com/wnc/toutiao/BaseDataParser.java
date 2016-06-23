package com.wnc.toutiao;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.string.PatternUtil;
import com.wnc.tools.SoupUtil;
import com.wnc.toutiao.util.DbSaveDao;

/**
 * 保存精华主题和作者列表
 *
 */
public class BaseDataParser {
	final static String format = "http://toutiao.io/prev/%s";
	static String today = BasicDateUtil.getCurrentDateFormatString("yyyy-MM-dd");
	final static String bDate = "2014-09-26";
	static CopyOnWriteArrayList authorList = new CopyOnWriteArrayList();
	static CopyOnWriteArrayList subjectList = new CopyOnWriteArrayList();

	public static void main(String[] args) throws InterruptedException {
		String path = "D:\\Users\\wnc\\Programs\\sts-bundle\\projects\\toutiao\\";
		BasicFileUtil.deleteFile(path + "log.txt");
		BasicFileUtil.deleteFile(path + "err.txt");

		String vDate = bDate;
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
		while (vDate.compareTo(today) < 1) {
			vDate = getNextDate(vDate);
			executor.execute(new Task(vDate));
		}
		executor.awaitTermination(1, TimeUnit.HOURS);
		System.out.println("作者总数:" + authorList.size());
	}

	private static

	class Task implements Runnable {
		String vDate;
		String web;

		public Task(String vDate) {
			this.vDate = vDate;
			web = String.format(format, vDate);
		}

		@Override
		public void run() {
			Document doc = null;
			try {
				doc = SoupUtil.getDoc(web);
			} catch (Exception ex) {
				logErr("other err:" + web + "  " + ex.getMessage());
			}
			if (doc == null) {
				logErr("io err:" + web);
			} else {
				// System.out.println(doc.toString());
				Elements subjects = doc.select(".post");
				if (subjects == null) {
					logErr(web + "找不到.post的节点");
				}
				for (Element e : subjects) {
					try {

						save(e);
					} catch (RuntimeException ex) {
						logErr(web + " 数据库执行错误!" + ex.getMessage());
					} catch (Exception ex) {
						ex.printStackTrace();
						logErr(web + " 页面解析错误!" + ex.getMessage());
					}
				}
			}
		}

		private void save(Element e) {
			Element titleNode = e.select(".title a").first();
			Element userNode = e.select(".subject-name a").last();

			String url = titleNode.absUrl("href");
			String title = StringEscapeUtils.escapeSql(titleNode.text());
			int agree_num = Integer.parseInt(e.select(".upvote span").first().text());
			int post_num = Integer.parseInt(e.select(".meta span").first().text());

			String author_url = userNode.absUrl("href");
			int author_id = Integer.parseInt(PatternUtil.getLastPattern(author_url, "\\d+"));
			String author_name = StringEscapeUtils.escapeSql(userNode.text());
			if (!subjectList.contains(url)) {
				subjectList.add(url);
				DbSaveDao.saveToSubject(url, title, author_id, agree_num, post_num, vDate);
			}
			if (!authorList.contains(author_id)) {
				authorList.add(author_id);
				DbSaveDao.saveToAuthor(author_id, author_url, author_name);
			}
		}

	}

	public static void log(String msg) {
		BasicFileUtil.writeFileString("log.txt", "Exception:" + msg + "\r\n", "GBK", true);
	}

	public static void logErr(String msg) {
		BasicFileUtil.writeFileString("err.txt", "Exception:" + msg + "\r\n", "GBK", true);
	}

	public static String getNextDate(String vDate) {
		String newDate = BasicDateUtil.getDateAfterDayDateString(vDate.replace("-", ""), 1);
		newDate = newDate.substring(0, 4) + "-" + newDate.substring(4, 6) + "-" + newDate.substring(6, 8);
		return newDate;
	}

	static Connection.Response response = null;

	private static int getState(String path) {
		Connection con = null;
		try {
			con = Jsoup.connect(path)
					.userAgent(
							"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
					.timeout(10000);

			response = con.execute();
		} catch (IOException e) {
			System.out.println("io - " + e);
		}
		return response == null ? 404 : response.statusCode();
	}
}
