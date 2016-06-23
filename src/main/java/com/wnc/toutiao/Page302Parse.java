package com.wnc.toutiao;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.toutiao.db.DbExecMgr;
import com.wnc.toutiao.util.DbSaveDao;
import com.wnc.toutiao.util.HttpLocationUtil;

public class Page302Parse {
	public void parse() {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);

		Map map = DbExecMgr.getSelectAllSqlMap("SELECT * FROM article order by id asc ");
		int size = map.size();
		Map rowMap;
		for (int i = 1; i <= size; i++) {
			rowMap = (Map) map.get(i);
			final String indexPage = String.valueOf(rowMap.get("URL"));
			final int id = Integer.parseInt(String.valueOf(rowMap.get("ID")));
			executor.execute(new Runnable() {
				@Override
				public void run() {
					String location = null;
					try {
						location = HttpLocationUtil.getLocation(indexPage);
						if (!BasicStringUtil.isNullString(location)) {
							DbSaveDao.updateArticle(location, id);
						} else {
							log302Err(indexPage + " 找不到Location");
						}
					} catch (Exception ex) {
						log302Err(indexPage + " 异常:" + ex.getMessage());
					}
				}

				public void log302Err(String msg) {
					BasicFileUtil.writeFileString("302_err.txt", "Exception:" + msg + "\r\n", "GBK", true);
				}
			});
		}
	}

	public static void main(String[] args) {
		BasicFileUtil.deleteFile("302_err.txt");
		new Page302Parse().parse();
	}
}
