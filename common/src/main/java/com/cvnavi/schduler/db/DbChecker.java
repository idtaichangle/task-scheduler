package com.cvnavi.schduler.db;

import com.cvnavi.schduler.util.ResourceReader;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Log4j2
public class DbChecker{

    public static void checkDatabase(){
        if(DBConnection.getInstance() instanceof MysqlConnection){
            ScriptRunner runner = new ScriptRunner(DBConnection.getInstance().get(), false, false);
            byte[] b= ResourceReader.readFile("/create_table_mysql.sql");
            String s=new String(b);
            try {
                runner.runScript(new BufferedReader(new StringReader(s)));
            } catch (IOException ex) {
                log.error(ex);
            } catch (SQLException ex) {
                log.error(ex);
            }
        }else if(DBConnection.getInstance() instanceof DerbyConnection){
            try {
                if(!existTable("alive_proxy")){
                    Statement st=DBConnection.getInstance().get().createStatement();
                    String sql= new String(ResourceReader.readFile("/create_table_derby.sql"));
                    st.execute(sql);
                    st.close();
                }
            }catch (Exception ex){
                log.error(ex);
            }
        }
    }

    public static void closeDatabase(){
        DBConnection.getInstance().close();
    }

    private static boolean existTable(String name) throws Exception{
        ResultSet rs=DBConnection.getInstance().get().getMetaData().getTables(null,null,null,null);
        while(rs.next()){
            String s=rs.getString("TABLE_NAME");
            if(s .equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
}
