package com.citroen.ledp.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期工具类
 *
 * @creator zhangqiang
 * @create-time Mar 5, 2015 9:29:49 PM
 * @version 1.0
 */
public class DateUtil {
	private final static long MIllISECOND=172800000;
	public static Date parse(String date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		Date d = null;
		try {
			d = df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}

	public static Date parse(String date) {
		return parse(date, "yy-M-d H:m:s");
	}

	public static String format(Date date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}

	public static String format(Date date) {
		return format(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static String format(Timestamp stamp, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(stamp);
	}

	public static String format(Timestamp stamp) {
		return format(stamp, "yyyy-MM-dd HH:mm:ss");
	}

	public static Date stampToDate(Timestamp stamp) {
		return parse(format(stamp));
	}

	public static String getCurrentDate() {
		return format(new Date(), "yyyy-MM-dd");
	}

	public static String getCurrentDateTime() {
		return format(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 获取两个日期之间的天数
	 * @param d1 开始日期
	 * @param d2 结束日期
	 * @return
	 */
	public static int getDaysBetween(java.util.Calendar d1,java.util.Calendar d2) {
		int days = d2.get(java.util.Calendar.DAY_OF_YEAR) - d1.get(java.util.Calendar.DAY_OF_YEAR);
		int y2 = d2.get(java.util.Calendar.YEAR);
		if (d1.get(java.util.Calendar.YEAR) != y2) {
			d1 = (java.util.Calendar) d1.clone();
			int i = 0;
			do {
				if(i > 1000){
					//加载循环此处，防止出现死循环
					break;
				}
				days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
				d1.add(java.util.Calendar.YEAR, 1);
				i++;
			} while (d1.get(java.util.Calendar.YEAR) != y2);
		}
		return days;
	}
	/**
	 * 得到本周周一
	 */
	public static Date getMondayOfThisWeek() {
		Calendar c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + 1);
		String strDate=format(c.getTime(),"yyyy-MM-dd 00:00:00");
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=null;
		try {
			date=sdf.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 得到本周周日
	 */
	public static Date getSundayOfThisWeek() {
		Calendar c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + 7);
		return c.getTime();
	}
	/**
	 * 判断时间差是否小于48小时
	 */
	public static boolean isDate(Date oldDate,Date newDate){
		long old=getMillionSeconds(oldDate);
		long newd=getMillionSeconds(newDate);
		if((newd-old)>=MIllISECOND){
			return false;
		}
		return true;
	}
	/**
	 * 时间转换成毫秒
	 */
	public static long getMillionSeconds (Date date){
		String str=format(date,"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		long millionSeconds = 0;
		try {
			millionSeconds = sdf.parse(str).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//毫秒
		return millionSeconds;
	}
	/**
	 * 上一个月第一天和最后一天
	 * @return first,last
	 */
	public static Map<String, String> getFirstday_Lastday_Month() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		Date theDate = calendar.getTime();

		// 上个月第一天
		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		gcLast.setTime(theDate);
		gcLast.set(Calendar.DAY_OF_MONTH, 1);
		String day_first = df.format(gcLast.getTime());
		StringBuffer str = new StringBuffer().append(day_first).append(
				" 00:00:00");
		day_first = str.toString();

		// 上个月最后一天
		calendar.add(Calendar.MONTH, 1); // 加一个月
		calendar.set(Calendar.DATE, 1); // 设置为该月第一天
		calendar.add(Calendar.DATE, -1); // 再减一天即为上个月最后一天
		String day_last = df.format(calendar.getTime());
		StringBuffer endStr = new StringBuffer().append(day_last).append(
				" 23:59:59");
		day_last = endStr.toString();

		Map<String, String> map = new HashMap<String, String>();
		map.put("first", day_first);
		map.put("last", day_last);
		return map;
	}

	/**
	 * 当月第一天
	 *
	 * @return
	 */
	public static String getFirstDay() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date theDate = calendar.getTime();

		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		gcLast.setTime(theDate);
		gcLast.set(Calendar.DAY_OF_MONTH, 1);
		String day_first = df.format(gcLast.getTime());
		StringBuffer str = new StringBuffer().append(day_first).append(
				" 00:00:00");
		return str.toString();

	}

	/**
	 * 当月最后一天
	 *
	 * @return
	 */
	public static String getLastDay() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date theDate = calendar.getTime();
		String s = df.format(theDate);
		StringBuffer str = new StringBuffer().append(s).append(" 23:59:59");
		return str.toString();

	}
	public static Date getAfterDay(Date date){
		Calendar   calendar   =   new   GregorianCalendar();
	    calendar.setTime(date);
	    calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
	    return calendar.getTime();   //这个时间就是日期往后推一天的结果
	}

	/**
	 * 获取指定日期那周的日期列表
	 * @param mdate
	 * @return
	 */
	public static List<Date> dateToWeek(Date mdate) {
		int b = mdate.getDay();
		Date fdate;
		List<Date> list = new ArrayList<Date>();
		Long fTime = mdate.getTime() - b * 24 * 3600000;
		for (int a = 1; a <= 7; a++) {
			fdate = new Date();
			fdate.setTime(fTime + (a * 24 * 3600000));
			System.out.println("setTime"+DateUtil.format(fdate,"yyyy-MM-dd"));
			list.add(a - 1, fdate);
		}
		return list;
	}
	public static List<Date> dateToWeek1(Date mdate) {
		int b = mdate.getDay();
		Date fdate;
		List<Date> list = new ArrayList<Date>();
		Long fTime = mdate.getTime() - b * 24 * 3600000;
		fdate = getMondayOfThisWeek();

		Calendar   calendar   =   new   GregorianCalendar();
		for (int a = 1; a <= 7; a++) {
			//fdate.setTime(fTime + (a * 24 * 3600000));
			if(a==1){
				list.add(a - 1, fdate);
			}else{
				calendar.setTime(fdate);
				calendar.add(calendar.DATE,1);
				fdate=calendar.getTime();
				list.add(a - 1, fdate);
			}
		}
		return list;
	}

	/**
	 * 得到本周周一
	 */
	public static Date getMondayOfLastWeek() {
		Calendar cal = Calendar.getInstance();
		//n为推迟的周数，1本周，-1向前推迟一周，2下周，依次类推
		int n = -1;
		String monday;
		cal.add(Calendar.DATE, n*7);
		//想周几，这里就传几Calendar.MONDAY（TUESDAY...）
		cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
		monday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()) + " 00:00:00";
		return parse(monday,"yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到本周周日
	 */
	public static Date getSundayOfLastWeek() {
		Calendar cal = Calendar.getInstance();
		//n为推迟的周数，1本周，-1向前推迟一周，2下周，依次类推
		//因为将周一取为第一天所以此处是0
		int n = 0;
		String monday;
		cal.add(Calendar.DATE, n*7);
		//想周几，这里就传几Calendar.MONDAY（TUESDAY...）
		cal.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
		monday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()) + " 00:00:00";
		return parse(monday, "yyyy-MM-dd HH:mm:ss");
	}

	public static void main(String[] args) {
		/*String date = DateUtil.format(new Date(),"yyyy-MM-01 00:00:00");
		System.out.println(date);
		String dd = "08/3/4 12:3:5";
		Date d = parse(dd, "yy/M/d H:m:s");*/
		/* System.out.println(getAfterDay(new Date()));
		dateToWeek1(new Date());
		System.out.println(DateUtil.format(new Date(),"yyyy-MM-dd")+1); */
		// System.out.println(getMondayOfLastWeek());
		// System.out.println(getSundayOfLastWeek());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startTime = parse("2015-11-01 00:00:00","yyyy-MM-dd HH:mm:ss");
		Date endTime = parse("2015-11-19 00:00:00","yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
        for (int i = 1; i <= 30; i++) {
            cal.add(Calendar.MINUTE, 864);//24小时制
            System.out.println("第"+i+"个奖品出奖事件："+format.format(cal.getTime()));
        }
	}
}
