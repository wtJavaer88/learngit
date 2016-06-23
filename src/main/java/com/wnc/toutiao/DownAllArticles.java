package com.wnc.toutiao;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.wnc.basic.BasicFileUtil;
import com.wnc.toutiao.db.DbExecMgr;
import com.wnc.toutiao.util.HtmlModifySoup;
import com.wnc.toutiao.util.TextFormatUtil;

public class DownAllArticles {
	private static final String ErrLOG_TXT = "down_err.txt";
	private static final String LOG_TXT = "down_log.txt";
	private static final String FOLDER = "E:\\Downloads\\toutiao\\articles\\";

	public void download() {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);

		Map map = DbExecMgr
				.getSelectAllSqlMap("SELECT REAL_URL,TITLE,ID FROM article WHERE REAL_URL IS NOT NULL order by id asc");
		int size = map.size();
		Map rowMap;
		for (int i = 1; i <= size; i++) {
			rowMap = (Map) map.get(i);
			final String realUrl = String.valueOf(rowMap.get("REAL_URL"));
			final String title = String.valueOf(rowMap.get("TITLE"));
			final int id = Integer.parseInt(String.valueOf(rowMap.get("ID")));
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						String path = getPath(title, id);
						HtmlModifySoup.docModifyAndSaveFile(path, realUrl);
						log(id);
					} catch (Exception ex) {
						logErr(realUrl + " 异常:" + ex.getMessage());
					}
				}

				private String getPath(final String title, final int id) {
					StringBuilder accum = new StringBuilder(64);
					accum.append(FOLDER);
					accum.append("[" + id + "]");
					accum.append(TextFormatUtil.getValidTitle(title));
					accum.append(".html");
					return accum.toString();
				}

				public void logErr(String msg) {
					BasicFileUtil.writeFileString(ErrLOG_TXT, "Exception:" + msg + "\r\n", "GBK", true);
				}

				public void log(int id) {
					BasicFileUtil.writeFileString(LOG_TXT, id + " ", "GBK", true);
				}
			});
		}
	}

	public static void main(String[] args) {
		BasicFileUtil.deleteFile(ErrLOG_TXT);
		BasicFileUtil.deleteFile(LOG_TXT);
		new DownAllArticles().download();
	}
}
