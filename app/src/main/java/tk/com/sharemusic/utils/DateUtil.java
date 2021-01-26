package tk.com.sharemusic.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private static final long ONE_SECOND = 1000;
    private static final long ONE_MINITE = ONE_SECOND * 60;
    private static final long ONE_HOUR = ONE_MINITE * 60;
    private static final long ONE_DAY = ONE_HOUR * 24;
    private static final long TWO_DAY = ONE_DAY * 2;

    public static final int TYPE_YEAR = 0;
    public static final int TYPE_MONTH = 1;
    public static final int TYPE_DAY = 2;
    public static final int TYPE_HOUR = 3;

    private static final SimpleDateFormat allTime = new SimpleDateFormat("yyyy年MM月dd HH:mm");
    private static final SimpleDateFormat year = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat month = new SimpleDateFormat("MM");
    private static final SimpleDateFormat day = new SimpleDateFormat("dd");
    private static final SimpleDateFormat ourMiniteTime = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat our = new SimpleDateFormat("HH");
    private static final SimpleDateFormat miniteTime = new SimpleDateFormat("mm");
    public static final SimpleDateFormat birthdayDate = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat ymd = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public static String getPublicTime(long timesnap){
        Date date = new Date(timesnap);
        long time = System.currentTimeMillis()-timesnap;
        if (time>TWO_DAY){
            return allTime.format(date);
        }else if (time>ONE_DAY){
            return "昨天 "+ourMiniteTime.format(date);
        }else if (time>ONE_HOUR){
            return time/ONE_HOUR+"小时前";
        }else if (time>ONE_MINITE){
            return time/ONE_MINITE+"分钟前";
        }else {
            return "刚刚";
        }
    }

    public static String getReviewTime(long timesnap){
        Date date = new Date(timesnap);
        long time = System.currentTimeMillis()-timesnap;
        if (time>TWO_DAY){
            return "2天前";
        }else if (time>ONE_DAY){
            return "昨天 "+ourMiniteTime.format(date);
        }else if (time>ONE_HOUR){
            return time/ONE_HOUR+"小时前";
        }else if (time>ONE_MINITE){
            return time/ONE_MINITE+"分钟前";
        }else {
            return "刚刚";
        }
    }

    public static int getCurrentDay(int type){
        Date date = new Date();
        String str="1800";
        switch (type){
            case TYPE_YEAR:
                str = year.format(date);
                break;
            case TYPE_MONTH:
                str = month.format(date);
                break;
            case TYPE_DAY:
                str = day.format(date);
                break;
            case TYPE_HOUR:
                str = our.format(date);
                break;
        }
        return Integer.valueOf(str);
    }

    public static String getChatTime(long timesnap){
        Date date = new Date(timesnap);
        Date nowDate = new Date();

        int nowDay = Integer.valueOf(day.format(nowDate));
        int chatDay = Integer.valueOf(day.format(date));
        if (nowDay == chatDay){
            return ourMiniteTime.format(date);
        }else if ((nowDay - chatDay)==1){
            return "昨天 "+ourMiniteTime.format(date);
        }else {
            return ymd.format(date);
        }
    }
}
