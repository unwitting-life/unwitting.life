package life.unwitting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("unused")
public class date4j {
    public static final String format = "yyyy/MM/dd HH:mm:ss";

    public static String now() {
        return date4j.now(date4j.format);
    }

    public static String now(String format) {
        String now = string4j.empty;
        try {
            if (lib.notNullOrEmpty(format)) {
                now = new SimpleDateFormat(format).format(new Date());
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return now;
    }

    public static Date fromGMT(String gmt, String format) {
        Date startTime = new Date();
        if (lib.notNullOrEmpty(gmt) && lib.notNullOrEmpty(format)) {
            try {
                startTime = new SimpleDateFormat(format).parse(gmt.substring(0, gmt.length() - 6).replace("T", " "));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return startTime;
    }
}
