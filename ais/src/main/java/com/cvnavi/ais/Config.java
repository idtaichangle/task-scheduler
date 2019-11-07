package com.cvnavi.ais;

import com.cvnavi.schduler.base.KeyValue;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

@Log4j2
public class Config {

	public static String shipxyUser;
	public static String shipxyPassword;

    static {
        Properties p = new Properties();
        try {
            InputStream is = Config.class.getResourceAsStream("/shipxy.properties");
            p.load(is);
			shipxyUser = p.getProperty("shipxy.user");
			shipxyPassword = p.getProperty("shipxy.password");
            is.close();
        } catch (IOException e) {
            log.error(e);
        }
    }
    public static KeyValue<String> getRandomShipxyAccount(){
        String [] array=shipxyUser.split(",");
        int index=new Random().nextInt(array.length);
        String userName=array[index];
        String password=shipxyPassword.split(",")[index];
        return new KeyValue<String>(userName, password);
    }
}
