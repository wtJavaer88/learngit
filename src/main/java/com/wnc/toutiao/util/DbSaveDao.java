package com.wnc.toutiao.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.wnc.toutiao.db.DBconnectionMgr;
import com.wnc.toutiao.db.DbExecMgr;
import com.wnc.toutiao.db.DbField;
import com.wnc.toutiao.db.DbFieldSqlUtil;

public class DbSaveDao {
	static Connection con = DBconnectionMgr.getConnection();

	public static void saveToSubject(String url, String title, int author_id, int agree_num, int post_num,
			String recordTime) {
		DbFieldSqlUtil util = new DbFieldSqlUtil("CREAMSUBJECT", "");
		util.addInsertField(new DbField("URL", url, "STRING"));
		util.addInsertField(new DbField("TITLE", title, "STRING"));
		util.addInsertField(new DbField("AUTHOR_ID", "" + author_id, "NUMBER"));
		util.addInsertField(new DbField("AGREE_NUM", "" + agree_num, "NUMBER"));
		util.addInsertField(new DbField("POST_NUM", "" + post_num, "NUMBER"));
		util.addInsertField(new DbField("RECORDTIME", recordTime, "STRING"));
		String sql = util.getInsertSql();
		try {
			DbExecMgr.execOnlyOneUpdate(sql);
		} catch (SQLException ex) {
			throw new RuntimeException(sql + " " + ex.getMessage());
		}
	}

	public static void saveToAuthor(int author_id, String url, String name) {
		DbFieldSqlUtil util = new DbFieldSqlUtil("AUTHOR", "");
		util.addInsertField(new DbField("URL", url, "STRING"));
		util.addInsertField(new DbField("NAME", name, "STRING"));
		util.addInsertField(new DbField("ID", "" + author_id, "NUMBER"));
		String sql = util.getInsertSql();
		try {
			DbExecMgr.execOnlyOneUpdate(sql);
		} catch (SQLException ex) {
			throw new RuntimeException(sql + " " + ex.getMessage());
		}
	}

	static int count = 0;

	public static void saveToArticle(String url, String title, int author_id, int agree_num, int post_num) {
		DbFieldSqlUtil util = new DbFieldSqlUtil("ARTICLE", "");
		util.addInsertField(new DbField("URL", url, "STRING"));
		util.addInsertField(new DbField("TITLE", title, "STRING"));
		util.addInsertField(new DbField("AUTHOR_ID", "" + author_id, "NUMBER"));
		util.addInsertField(new DbField("AGREE_NUM", "" + agree_num, "NUMBER"));
		util.addInsertField(new DbField("POST_NUM", "" + post_num, "NUMBER"));
		String sql = util.getInsertSql();

		try {
			DbExecMgr.execOnlyOneUpdate(sql);
			count++;
			System.out.println(count + ": " + sql);
		} catch (SQLException ex) {
			throw new RuntimeException(sql + " " + ex.getMessage());
		}
	}

	public static void updateArticle(String real_url, int id) {
		DbFieldSqlUtil util = new DbFieldSqlUtil("ARTICLE", "");
		util.addUpdateField(new DbField("REAL_URL", real_url, "STRING"));
		util.addWhereField(new DbField("ID", "" + id, "NUMBER"));
		String sql = util.getUpdateSql();

		try {
			DbExecMgr.execOnlyOneUpdate(sql);
			count++;
			System.out.println(count + ": " + sql);
		} catch (SQLException ex) {
			throw new RuntimeException(sql + " " + ex.getMessage());
		}
	}

}
