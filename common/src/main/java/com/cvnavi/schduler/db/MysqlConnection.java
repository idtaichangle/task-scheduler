package com.cvnavi.schduler.db;

import com.cvnavi.schduler.config.Config;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class MysqlConnection extends DBConnection{

    protected static Connection con;
    /**
     * 获取数据库连接。使用完毕后，可以不用关闭连接。web app销毁时会关闭连接。
     * @return
     */
    @Override
    public Connection get() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName(Config.dbDriver);
                con = DriverManager.getConnection(Config.dbUrl, Config.dbUser, Config.dbPassword);
            }
        } catch (Exception e) {
            log.error(e);
            if(e.getMessage().contains("Unknown database")){
                try {
                    createDatabase();
                    con = DriverManager.getConnection(Config.dbUrl, Config.dbUser, Config.dbPassword);
                } catch (SQLException e1) {
                    log.error(e1);
                }

            }
        }

        return con;
    }

    @Override
    public void close() {
        try {
            AbandonedConnectionCleanupThread.shutdown();
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                DriverManager.deregisterDriver(driver);
                log.info(String.format("deregistering jdbc driver: %s", driver));
            }
        } catch (InterruptedException e) {
            log.error(e);
        } catch (SQLException e) {
            log.error(e);
        }
    }

    private void createDatabase() throws SQLException {
        String url=Config.dbUrl.substring(0,Config.dbUrl.lastIndexOf("/")+1);
        if(Config.dbUrl.contains("?")){
            url+=Config.dbUrl.substring(Config.dbUrl.indexOf("?"));
        }
        String dbName= "";
        Matcher m=Pattern.compile("\\w+").matcher(Config.dbUrl.substring(Config.dbUrl.lastIndexOf("/")+1));
        if(m.find()){
            dbName=m.group(0);
        }
        Connection c=DriverManager.getConnection(url,Config.dbUser,Config.dbPassword);
        Statement s= c.createStatement();
        s.execute("create database "+dbName);
        s.close();
        c.close();
    }
}
