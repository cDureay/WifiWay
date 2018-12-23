package yu.com.Wifi.wifiway;
import java.sql.Date;
import   java.text.SimpleDateFormat;

public class Time {
    public static String TimeOut(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }
}
