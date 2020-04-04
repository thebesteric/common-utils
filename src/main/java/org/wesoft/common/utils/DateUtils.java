package org.wesoft.common.utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 *
 * @author Eric Joe
 * @info
 * @build 2015年11月20日
 */
public class DateUtils {

    private static final Integer RECENT_DAYS = 14;

    /**
     * 将日期转化为 PrettyTime
     *
     * @param date 日期
     */
    public static String prettyTime(Date date) {
        String formatDate = "";

        PrettyTime p = new PrettyTime();
        formatDate = StringUtils.spaceEnter2quot(p.format(date));

        return formatDate;
    }

    /**
     * 解析字符串日期
     *
     * @param strDate 开始事件
     * @param pattern 日志格式
     */
    public static Date parseDate(String strDate, String... pattern) {
        String myPattern = "yyyy-MM-dd HH:mm";

        if (pattern != null && pattern.length > 0) {
            myPattern = pattern[0];
        }

        SimpleDateFormat sdf = new SimpleDateFormat(myPattern);

        Date formatDate = null;

        if (StringUtils.isEmpty(strDate)) {
            return formatDate;
        }

        try {
            formatDate = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formatDate;
    }

    /**
     * 得到指定日期的周日
     *
     * @param date 日期
     */
    public static Date getWeekSunday(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);

        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        return DateUtils.getEndDate(calendar.getTime(), 0);
    }

    /**
     * 得到指定日期的周一
     *
     * @param date 日期
     */
    public static Date getWeekMonday(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);

        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        return getStartDate(calendar.getTime(), 0);
    }

    /**
     * 获取指定日期下的下周几
     *
     * @param date    日期
     * @param weekDay 下周几
     */
    public static Date getNextWeekSpecifiedDay(Date date, Integer weekDay) {
        Calendar calendar = Calendar.getInstance();
        Date next = new Date(date.getTime() + 7 * 24 * 60 * 60 * 1000);

        calendar.setTime(next);

        if (weekDay == 1) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }

        calendar.set(Calendar.DAY_OF_WEEK, weekDay);
        return calendar.getTime();
    }

    /**
     * 获取指定日期下的本周几
     *
     * @param date    日期
     * @param weekDay 周几
     */
    public static Date getCurrentWeekSpecifiedDay(Date date, Integer weekDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (weekDay == 1) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }

        calendar.set(Calendar.DAY_OF_WEEK, weekDay);
        return calendar.getTime();
    }

    /**
     * 获取星期几
     *
     * @param date 日期
     */
    public static int getWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week < 0) {
            week = 0;
        }
        return week;

    }

    /**
     * 获取当前小时（24小时制）
     *
     * @param date 日期
     */
    public static int getHour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前分钟
     *
     * @param date 日期
     */
    public static int getMinute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }

    /**
     * 获取当前秒
     *
     * @param date 日期
     */
    public static int getSecond(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.SECOND);
    }

    /**
     * 获取当前毫秒
     *
     * @param date 日期
     */
    public static long getMillis(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTimeInMillis();
    }


    /**
     * 获取日期的年
     *
     * @param date 日期
     */
    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * 获取日期的月
     *
     * @param date 日期
     */
    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取日期的日
     *
     * @param date 日期
     */
    public static int getDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DATE);
    }

    /**
     * 是否上午
     *
     * @param date 日期
     */
    public static boolean isAM(Date date) {
        boolean isAm = false;
        if (date == null) {
            return isAm;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hours = cal.get(Calendar.HOUR_OF_DAY);

        if (hours < 12) {
            isAm = true;
        }

        return isAm;
    }

    /**
     * 是否下午
     *
     * @param date 日期
     */
    public static boolean isPM(Date date) {
        boolean isPm = false;
        if (date == null) {
            return isPm;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hours = cal.get(Calendar.HOUR_OF_DAY);

        if (hours >= 12 && hours < 18) {
            isPm = true;
        }

        return isPm;
    }

    /**
     * 今天
     *
     * @param pattern 日期格式
     */
    public static String getToday(String pattern) {
        Date currentDate = new Date();
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        return sf.format(currentDate);
    }

    /**
     * 今天
     */
    public static Date getToday() {
        Date currentDate = new Date();
        return currentDate;
    }

    /**
     * 最近的一日
     */
    public static Date getRecentDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, RECENT_DAYS * -1);
        return cal.getTime();
    }

    /**
     * 指定偏移的最近的一日
     *
     * @param offset 偏移量
     */
    public static Date getRecentDate(Integer offset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, offset * -1);
        return cal.getTime();
    }

    /**
     * 当前时对应的日期
     */
    public static Date getCurrSharpHour() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY),
                0, 0);
        long m = cal.getTimeInMillis() / 1000 * 1000;
        cal.setTimeInMillis(m);

        return cal.getTime();
    }

    /**
     * 指定日期的当前时
     *
     * @param date 日期
     */
    public static Date getSharpHour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY),
                0, 0);
        long m = cal.getTimeInMillis() / 1000 * 1000;
        cal.setTimeInMillis(m);

        return cal.getTime();
    }

    /**
     * 开始日期
     *
     * @param cal    日期
     * @param offset 偏移量
     */
    public static Date getStartDate(Calendar cal, int offset) {
        cal.add(Calendar.DAY_OF_MONTH, offset);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
        long m = cal.getTimeInMillis() / 1000 * 1000;
        cal.setTimeInMillis(m);

        return cal.getTime();
    }

    /**
     * 开始日期
     *
     * @param date   日期
     * @param offset 偏移量
     */
    public static Date getStartDate(Date date, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return getStartDate(cal, offset);
    }

    /**
     * 结束日期
     *
     * @param cal    日期
     * @param offset 偏移量
     */
    public static Date getEndDate(Calendar cal, int offset) {
        cal.add(Calendar.DAY_OF_MONTH, offset);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);
        return cal.getTime();
    }

    /**
     * 结束日期
     *
     * @param date   日期
     * @param offset 偏移量
     */
    public static Date getEndDate(Date date, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return getEndDate(cal, offset);
    }

    /**
     * 格式化
     *
     * @param date    日期
     * @param pattern 格式
     */
    public static String format(Date date, String pattern) {
        String myPattern = "yyyy-MM-dd HH:mm";

        if (date == null) {
            return null;
        }

        if (!StringUtils.isEmpty(pattern)) {
            myPattern = pattern;
        }

        SimpleDateFormat df = new SimpleDateFormat(myPattern);
        return df.format(date);
    }

    /**
     * 格式化
     *
     * @param date    日期
     * @param pattern 格式
     */
    public static String format(Date date, String... pattern) {
        String myPattern = "yyyy-MM-dd HH:mm";

        if (date == null) {
            return null;
        }

        if (pattern != null && pattern.length > 0) {
            myPattern = pattern[0];
        }

        SimpleDateFormat df = new SimpleDateFormat(myPattern);
        return df.format(date);
    }

    /**
     * 格式化
     *
     * @param date    日期
     * @param pattern 格式
     */
    public static String format(Timestamp date, String pattern) {
        String myPattern = "yyyy-MM-dd HH:mm";

        if (date == null) {
            return null;
        }

        if (!StringUtils.isEmpty(pattern)) {
            myPattern = pattern;
        }

        SimpleDateFormat df = new SimpleDateFormat(myPattern);
        return df.format(date);
    }

    /**
     * 格式化
     *
     * @param date    日期
     * @param pattern 格式
     */
    public static String format(Timestamp date, String... pattern) {
        String myPattern = "yyyy-MM-dd HH:mm";

        if (date == null) {
            return null;
        }

        if (pattern != null && pattern.length > 0) {
            myPattern = pattern[0];
        }

        SimpleDateFormat df = new SimpleDateFormat(myPattern);
        return df.format(date);
    }

    /**
     * 格式化
     *
     * @param pattern 格式
     */
    public static String formatCurrDate(String pattern) {
        Date currentDate = new Date();
        String myPattern = "yyyy-MM-dd HH:mm";

        if (pattern != null && pattern.length() > 0) {
            myPattern = pattern;
        }
        SimpleDateFormat sf = new SimpleDateFormat(myPattern);
        return sf.format(currentDate);
    }

    /**
     * 短日期格式
     *
     * @param date 日期
     */
    public static String shortFormat(Date date) {
        String myPattern = "M月d日";

        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hours = cal.get(Calendar.HOUR_OF_DAY);

        String amPm = "上午";
        if (hours >= 12 && hours < 18) {
            amPm = "下午";
        } else if (hours >= 18) {
            amPm = "晚上";
        }

        SimpleDateFormat df = new SimpleDateFormat(myPattern);
        return df.format(date) + " " + amPm;
    }

    /**
     * 获取天差
     *
     * @param cal1 日期
     * @param cal2 日期
     */
    public static Long getDayDiff(Calendar cal1, Calendar cal2) {
        long dayMillis = 1000L * 60 * 60 * 24;
        long diffMillis = getStartDate(cal1, 0).getTime() - getStartDate(cal2, 0).getTime();

        BigDecimal bd = new BigDecimal(diffMillis * 1.0 / dayMillis);

        //  - 1L;
        return bd.setScale(0, BigDecimal.ROUND_UP).longValue();
    }

    /**
     * 获取天差
     *
     * @param date1 日期
     * @param date2 日期
     */
    public static Long getDayDiff(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return getDayDiff(cal1, cal2);
    }

    /**
     * 偏移日期
     *
     * @param date   日期
     * @param offset 毫秒值
     */
    public static Date getOffsetDateByMilliSecond(Date date, long offset) {
        Calendar cal = Calendar.getInstance();

        long currTime = date.getTime();
        long offsetTime = currTime + offset;

        cal.setTimeInMillis(offsetTime);

        return cal.getTime();
    }

    /**
     * 获取秒差
     *
     * @param cal1 日期
     * @param cal2 日期
     */
    public static Long getSecondDiff(Calendar cal1, Calendar cal2) {
        long secondMilis = 1000L;
        long milis = cal1.getTimeInMillis() - cal2.getTimeInMillis();

        BigDecimal bd = new BigDecimal(milis * 1.0 / secondMilis);

        return bd.setScale(0, BigDecimal.ROUND_UP).longValue();
    }

    /**
     * 获取秒差
     *
     * @param date1 日期
     * @param date2 日期
     */
    public static Long getSecondDiff(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return getSecondDiff(cal1, cal2);
    }

    /**
     * 获取分钟差
     *
     * @param cal1 日期
     * @param cal2 日期
     */
    public static Long getMinutesDiff(Calendar cal1, Calendar cal2) {
        return getSecondDiff(cal1, cal2) / 60;
    }

    /**
     * 获取分钟差
     *
     * @param date1 日期
     * @param date2 日期
     */
    public static Long getMinutesDiff(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return getSecondDiff(cal1, cal2) / 60;
    }

    /**
     * 获取小时差
     *
     * @param cal1 日期
     * @param cal2 日期
     */
    public static Long getHourDiff(Calendar cal1, Calendar cal2) {
        return getSecondDiff(cal1, cal2) / (60 * 60);
    }

    /**
     * 获取小时差
     *
     * @param date1 日期
     * @param date2 日期
     */
    public static Long getHourDiff(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return getSecondDiff(cal1, cal2) / (60 * 60);
    }

    /**
     * 获取年龄
     *
     * @param birthday 日期
     */
    public static Integer getAge(Date birthday) {
        Integer age = null;
        if (birthday != null) {
            Long dayDiff = getDayDiff(new Date(), birthday);
            BigDecimal bd = new BigDecimal(dayDiff / 365L);
            age = bd.setScale(0, BigDecimal.ROUND_DOWN).intValue();
        }
        return age;
    }

    /**
     * 获取当前时间到次日凌晨的剩余秒数
     */
    public static long getSecondsNextEarlyMorning() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }

    /**
     * 获取本地时间
     *
     * @param date 日期
     */
    public static LocalDateTime getLocalDateTime(Date date) {
        return date != null ? date.toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime() : null;
    }

    /**
     * 格式化本地时间
     *
     * @param localDateTime 日期
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format == null ? "yyyy-MM-dd HH:mm:ss" : format);
        return localDateTime.format(formatter);
    }

    /**
     * 格式化本地时间
     *
     * @param date 日期
     */
    public static String getLocalDateTimeFormat(Date date, String format) {
        return formatLocalDateTime(getLocalDateTime(date), format);
    }

    /**
     * 获取指定月的第一天
     *
     * @param month 月份
     */
    public static Date getFirstDayOfMonth(int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month - 1);
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        return getStartDate(cal.getTime(), 0);
    }

    /**
     * 获取指定月的最后一天
     *
     * @param month 月份
     */
    public static Date getLastDayOfMonth(int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month - 1);
        int lastDay;
        if (month == 2) {
            lastDay = cal.getLeastMaximum(Calendar.DAY_OF_MONTH);
        } else {
            lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        return getEndDate(cal.getTime(), 0);
    }

    /**
     * 获取当前月的第一天
     */
    public static Date getFirstDayOfMonth() {
        return getFirstDayOfMonth(getMonth(new Date()));
    }

    /**
     * 获取当前月的第一天
     */
    public static Date getLastDayOfMonth() {
        return getLastDayOfMonth(getMonth(new Date()));
    }

}