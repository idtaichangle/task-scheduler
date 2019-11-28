package com.cvnavi.scheduler.db;

import java.sql.Connection;

import com.cvnavi.scheduler.config.Config;

public abstract class DBConnection{

	private static DBConnection inst;

	public static DBConnection getInstance(){
		if(inst==null){
			if (Config.dbDriver.contains("mysql")){
				inst= new MysqlConnection();
			}else if (Config.dbDriver.contains("derby")){
				inst= new DerbyConnection();
			}
		}

		return inst;
	}

	public abstract Connection get();

	public abstract void close();

}
