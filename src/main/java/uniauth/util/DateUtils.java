package uniauth.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static String FORMAT_PERIOD = "M月dd日";
    public static String FORMAT_YMD = "yyyy-MM-dd";

    public static String getStringDate(Date d, String format) {
        SimpleDateFormat sd = new SimpleDateFormat(format);
        return sd.format(d);
    }

    /**
     * 获取今天是星期几
     * 1, 2,... 7对应星期一，星期二，...星期日
     *
     * @return
     */
    public static Integer getWeekDay(Date today) {
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        int weekday = c.get(Calendar.DAY_OF_WEEK) - 1;
        return weekday == 0 ? 7 : weekday;
    }

    /**
     * 获取指定日期前后几天的日期
     *
     * @param d   指定日期
     * @param day 前后天数间隔（正数为向后，负数为向前）
     * @return 间隔后日期
     */
    public static Date getDateByDelta(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * 把某个日期转换为当周周几的日期，如2019/4/9转为当周周四的日期为2019/4/11
     *
     * @param d
     * @param weekday
     * @return
     */
    public static Date convertByWeekDay(Date d, int weekday) {
        int minusDays = weekday - getWeekDay(d);
        return getDateByDelta(d, minusDays);
    }

    public static boolean isDateEquals(Date date1, Date date2, String format) {
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        return fmt.format(date1).equals(fmt.format(date2));
    }

    public static String getPeriodDateSuffix() {
        SimpleDateFormat fmt = new SimpleDateFormat(FORMAT_PERIOD);
        return "-" + fmt.format(new Date());
    }
}
