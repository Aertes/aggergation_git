package com.citroen.wechat.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {
	public static enum Format{
		MONTHDAY("MM-dd")
		,YEARMONTH("yyyy-MM")
		,DATE("yyyy-MM-dd")
		,DATETIME("yyyy-MM-dd HH:mm:ss");
		private String val;
		private Format(String val){this.val =val;}
	}
	
	public static void main(String[] args){
		Date   date = new Date();
		System.out.println(getYear(date));
		String string = DateUtil.convert(new Date(),Format.DATE);
		System.out.println(string);
		System.out.println(DateUtil.format(string,Format.MONTHDAY));
		
		System.out.println(DateUtil.convert(date, Format.DATETIME));
		System.out.println(DateUtil.convert(DateUtil.format(date,Format.DATE),Format.DATETIME));
		System.out.println(DateUtil.convert(new Date(),"yyyy年M月d日"));
		System.out.println(DateUtil.convert(new Date(),"HH:mm:ss"));
	}
	static public String convert(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat(Format.DATETIME.val);
		return dateFormat.format(date);
	}
	static public String convert(Date date,Format format){
		SimpleDateFormat dateFormat = new SimpleDateFormat(format.val);
		return dateFormat.format(date);
	}
	static public String convert(Date date,String format){
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	static public Date convert(String date,Format format){
		return convert(date,format.val);
	}
	static public Date convert(String date,String format){
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	static public String format(String date,Format format){
		return format(date,format.val);
	}
	static public String format(String date,String format){
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			return dateFormat.format(dateFormat.parse(date));
		} catch (ParseException e) {
			return date;
		}
	}
	static public Date format(Date date,Format format){
		return format(date,format.val);
	}
	static public Date format(Date date,String format){
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			return dateFormat.parse(dateFormat.format(date));
		} catch (ParseException e) {
			return null;
		}
	}
	/**
	 * string转为date类型"yyyy-MM-dd"
	 * @param format
	 * @return
	 * @throws Exception
	 */
	static public Date stringTodate(String format) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(format);
		return date;
	}
	/**
	 * string转为date类型"yyyy-MM-dd HH:mm:ss"
	 * @param format
	 * @return
	 * @throws Exception
	 */
	static public Date diffTodate(String format) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(format);
		return date;
	}
	/**
	 * 获取两日期之间相差的月数
	 * @param startDate,被减日期;endDate,减去日期;
	 * @return 操作之后的月数
	 * @throws ParseException 
	 * */
	static public int diffMonth(Date startDate, Date endDate){
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(startDate);
		int startMonth = calStart.get(Calendar.YEAR)*12+calStart.get(Calendar.MONTH);
		Calendar endStart = Calendar.getInstance();
		endStart.setTime(endDate);
		int endMonth   = endStart.get(Calendar.YEAR)*12+endStart.get(Calendar.MONTH);
		return endMonth - startMonth;
	}
	/**
	 * 获取两日期之间相差的天数
	 * @param startDate,被减日期;endDate,减去日期;
	 * @return 操作之后的天数
	 * @throws ParseException 
	 * */
	static public int diffDay(Date startDate, Date endDate){
		return Math.round((endDate.getTime() - startDate.getTime()) /(24*60*60*1000));
	}
	/**
	 * 两个日期时间之间的时长
	 */
	static public int diffHours(Date dateStart,Date dateEnd){
		return Math.round((dateEnd.getTime() - dateStart.getTime()) /(60*60*1000)%24);
	}
	/**
	 * 获取日期所在月第一天的Date对象
	 * @param year,日期所在年;month,日期所在月;
	 * @return 操作之后的Date对象
	 * @throws ParseException 
	 * */
	static public Date getFirstDay(int year, int month){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH,month-1);
		calendar.set(Calendar.DATE, 1);
		return calendar.getTime();
	}
	/**
	 * 获取日期所在月第一天的Date对象
	 * @param date,进行操作的Date对象
	 * @return 操作之后的Date对象
	 * @throws ParseException 
	 * */
	static public Date getFirstDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, 1);
		return calendar.getTime();
	}
	/**
	 * 获取日期所在月最后一天的Date对象
	 * @param year,日期所在年;month,日期所在月;
	 * @return 操作之后的Date对象
	 * @throws ParseException 
	 * */
	static public Date getLastDay(int year, int month){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH,month);
		calendar.set(Calendar.DATE, 1);
		calendar.add(Calendar.DATE, -1);
		return calendar.getTime();
	}
	/**
	 * 获取日期所在月最后一天的Date对象
	 * @param date,进行操作的Date对象
	 * @return 操作之后的Date对象
	 * */
	static public Date getLastDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, 1);
		calendar.add(Calendar.MONTH,1);
		calendar.add(Calendar.DATE,-1);
		return calendar.getTime();
	}
	/**
	 * 对日期添加或减少秒
	 * @param date,进行操作的Date对象
	 * @param hour,添加或减少秒
	 * @return 操作之后的Date对象
	 * */
	static public Date addMilliSecond(Date date,int milliSecond){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MILLISECOND,milliSecond);
		return calendar.getTime();
	}
	/**
	 * 对日期添加或减少秒
	 * @param date,进行操作的Date对象
	 * @param hour,添加或减少秒
	 * @return 操作之后的Date对象
	 * */
	static public Date addSecond(Date date,int second){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.SECOND,second);
		return calendar.getTime();
	}
	/**
	 * 对日期添加或减少分钟
	 * @param date,进行操作的Date对象
	 * @param hour,添加或减少分钟
	 * @return 操作之后的Date对象
	 * */
	static public Date addMinute(Date date,int minute){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE,minute);
		return calendar.getTime();
	}
	/**
	 * 对日期添加或减少小时
	 * @param date,进行操作的Date对象
	 * @param hour,添加或减少小时
	 * @return 操作之后的Date对象
	 * */
	static public Date addHour(Date date,int hour){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR,hour);
		return calendar.getTime();
	}
	/**
	 * 对日期添加或减少天数
	 * @param date,进行操作的Date对象
	 * @param day,添加或减少天数
	 * @return 操作之后的Date对象
	 * */
	static public Date addDay(Date date,int day){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE,day);
		return calendar.getTime();
	}
	/**
	 * 对日期添加或减少月数
	 * @param date,进行操作的Date对象
	 * @param day,添加或减少天数
	 * @return 操作之后的Date对象
	 * */
	static public Date addMonth(Date date,int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, month);
		return calendar.getTime();
	}
	/**     
	 * 获取多少号
	 * @param date 进行操作的Date对象
	 * @return 当前日期是多少号   
     * */     
	public static int getDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DATE);
		return day;
	}
	/**     
	 * 获取日期是几月
	 * @param date 进行操作的Date对象
	 * @return 日期的月份
     * */    
	public static int getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH)+1;
		return month;
	}
	/**     
	 * 获取日期年份
	 * @param date 进行操作的Date对象
	 * @return 日期的年份
     * */    
	public static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.YEAR);
		return month;
	}
	/**     
	 * 获取当前日期是星期几
	 * @param date 进行操作的Date对象
	 * @return 当前日期是星期几   
     * */
	public static int getWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
		return week==0?7:week;
	}
	/**     
	 * 获取时间段中的月份
	 * @param date 进行操作的Date对象
	 * @return 当前日期是星期几   
     * */     
	public static String[] getMonths(String date1,String date2) {
		Date value1 = convert(date1,Format.YEARMONTH);
		Date value2 = convert(date2,Format.YEARMONTH);
		if(value1.getTime()>value2.getTime()){
			return new String[0];
		}
		
		StringBuilder months = new StringBuilder();
		months.append(convert(value1,Format.YEARMONTH));
		while(value1.getTime()<value2.getTime()){
			value1 = addMonth(value1,1);
			months.append(",").append(convert(value1,Format.YEARMONTH));
		}
		String result = months.toString();
		return result.split(",");     
	} 
	
	/**     
	 * 切分取时区间,取到下一天之前
     * */   
	public static Map<String,String> hour_(Date date){
		Date date1 = format(date,"yyyy-MM-dd HH:00:00");
		Map<String,String> hour = new HashMap<String,String>();
		date1 = (date1.getTime()<date.getTime())?DateUtil.addDay(date1,1):date1;
		hour.put("date1",convert(date,"yyyy-MM-dd HH:00:00"));
		hour.put("date2",convert(date1,"yyyy-MM-dd 00:00:00"));
		return hour;
	}
	/**     
	 * 切分取时区间,取到上一天之前
     * */   
	public static Map<String,String> _hour(Date date){
		Map<String,String> hour = new HashMap<String,String>();
		hour.put("date1",convert(date,"yyyy-MM-dd 00:00:00"));
		hour.put("date2",convert(date,"yyyy-MM-dd HH:00:00"));
		return hour;
	}
	/** 
	 * 切取整天区间,取到下一月之前
     * */
	public static Map<String,String> day_(Date date){
		Date date1 = format(date,"yyyy-MM-dd 00:00:00");
		Map<String,String> day = new HashMap<String,String>();
		date1 = (date1.getTime()<date.getTime())?DateUtil.addMonth(date1,1):date1;
		day.put("date1",convert(date,"yyyy-MM-dd 00:00:00"));
		day.put("date2",convert(date1,"yyyy-MM-01 00:00:00"));
		return day;
	}
	/** 
	 * 切取整天区间,取到上一月之前
     * */
	public static Map<String,String> _day(Date date,int month){
		Map<String,String> day = new HashMap<String,String>();
		day.put("date1",convert(date,"yyyy-MM-01 00:00:00"));
		day.put("date2",convert(date,"yyyy-MM-dd 00:00:00"));
		return day;
	}
	/**
	 * 切取整月区间
     * */
	public static Map<String,String> month(Date d1,Date d2){
		Date date1 = format(d1,"yyyy-MM-01 00:00:00");
		Date date2 = format(d2,"yyyy-MM-01 00:00:00");
		
		Map<String,String> month = new HashMap<String,String>();
		date1 = (date1.getTime()<d1.getTime())?DateUtil.addMonth(date1,1):date1;
		month.put("date1",convert(date1,"yyyy-MM-01 00:00:00"));
		month.put("date2",convert(date2,"yyyy-MM-01 00:00:00"));
		return month;
	}
	
}
