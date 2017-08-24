package com.citroen.ledp.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

public abstract class CommonUtil {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	
	private static String[] weeks = new String[]{"周日","周一","周二","周三","周四","周五","周六"};
	
	private static Calendar calendar = Calendar.getInstance(Locale.CHINA);
	private static final int BUFFER_SIZE = 2048000;
	
	/**
	 * String 转换 Long 数组
	 */
	public static Long[] stringToLongArray(String ids){
		String []idArr = ids.split("-");
		Long []idList = new Long[idArr.length];
		for(int i = 0;i<idArr.length;i++){
			if(StringUtils.isNotBlank(idArr[i])){
				try {
					idList[i] = Long.valueOf(idArr[i]);
				} catch (NumberFormatException e) {
					continue;
				}
			}
		}
		return idList;
	}
	/**
	 * 转义 特殊符
	 */
	public static String escapeSpecialSign(String condition){
		String bb = StringUtils.replace(condition, "/", "//");
		bb = StringUtils.replace(bb, "%", "/%");
		bb = StringUtils.replace(bb, "_", "/_");
		return bb;
	}
	
	/**
	 * 随机生成指定位数且不重复的字符串.去除了部分容易混淆的字符，如1和l，o和0等，
	 * 
	 * 随机范围1-9 a-z A-Z
	 * 
	 * @param length
	 *            指定字符串长度
	 * @return 返回指定位数且不重复的字符串
	 */
	public static String getRandomString(int length) {
		StringBuffer bu = new StringBuffer();
		String[] arr = { "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c",
				"d", "e", "f", "g", "h", "i", "j", "k", "m", "n", "p", "q",
				"r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C",
				"D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "P",
				"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		Random random = new Random();
		while (bu.length() < length) {
			String temp = arr[random.nextInt(57)];
			if (bu.indexOf(temp) == -1) {
				bu.append(temp);
			}
		}
		return bu.toString();
	}

	public static String formatDate(Date d){
		return d!=null?sdf.format(d):null;
	}
	
	public static String formatDate(Date d,String pattern){
		if(d == null){
			return "";
		}
		sdf.applyPattern(pattern);
		return sdf.format(d);
	}
	
	public static Date parseDate(String date, String pattern) {
		sdf.applyPattern(pattern);
		Date d = null;
		try {
			d = sdf.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return d;
	}
	
	public static Date parseDate(String date, String pattern,Locale locale) {
		Date result = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,locale);
			result = simpleDateFormat.parse(date);
			return result;
		} catch (ParseException e) {
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd",locale);
				result = simpleDateFormat.parse(date);
				return result;
			} catch (ParseException e1) {
				e1.printStackTrace();
				return result;
			}
		}
		
	}
	
	/**
	 * 获取两个日期之间的天数
	 * @param d1 开始日期
	 * @param d2 结束日期
	 * @return
	 */
	public static int getDaysBetween(java.util.Calendar d1,java.util.Calendar d2) {
		if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end   
			java.util.Calendar swap = d1;
			d1 = d2;
			d2 = swap;
		}
		int days = d2.get(java.util.Calendar.DAY_OF_YEAR) - d1.get(java.util.Calendar.DAY_OF_YEAR);
		int y2 = d2.get(java.util.Calendar.YEAR);
		if (d1.get(java.util.Calendar.YEAR) != y2) {
			d1 = (java.util.Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
				d1.add(java.util.Calendar.YEAR, 1);
			} while (d1.get(java.util.Calendar.YEAR) != y2);
		}
		return days;
	}
	
	/**
	 * 获取两个日期之间的天数
	 * @param beginDate 开始日期
	 * @param endDate 结束日期
	 * @return
	 */
	public static int getDaysBetween(Date beginDate,Date endDate) {
		int days = 0;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			long to = df.parse(df.format(beginDate)).getTime();
			long from = df.parse(df.format(endDate)).getTime();
			days = (int)((to - from) / (1000 * 60 * 60 * 24));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return days;
	}
	
	/**
	 * 设置天数
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date setDay(Date date,int day){
		calendar.setTime(date);
//		calendar.set(Calendar.HOUR_OF_DAY, 0);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return calendar.getTime();
	}
	
	/**
	 * 获取是周几 1表示周1 
	 * @param d
	 * @return
	 */
	public static int getChineseWeekNum(Date d){
		calendar.setTime(d);
		
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		
		week = week - 1;	//默认周日为1
		
		return week;
	}
	
	public static int getDay(Date date){
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	/**
	 * 获取小时
	 * @param d
	 * @return
	 */
	public static int getHour(Date d){
		calendar.setTime(d);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * 获取星期几
	 * @param num
	 * @return
	 */
	public static String getChineseWeekNameByNum(int num){
		return weeks[num];
	}
	
	/**
	 * 获取日期
	 * @param d
	 * @return
	 */
	public static String getDate(Date d) {
		return sdf2.format(d);
	}
	
	/**
	 * 获取当天起始时间 00:00
	 * @return
	 */
	public static Date getCurrenDateStart() {
		Date now = new Date();
		calendar.setTime(now);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND, 0);
		
		return calendar.getTime();
	}
	
	/**
	 * 获取当天结束时间 23:59:59
	 * @return
	 */
	public static Date getCurrenDateEnd() {
		Date now = new Date();
		calendar.setTime(now);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE,59);
		calendar.set(Calendar.SECOND, 59);
		
		return calendar.getTime();
	}
	
	/**
	 * 获取当周最后一天日期
	 * @param now
	 * @return
	 */
	public static Date getWeekEnd(Date now) {
		int addNum = 0;
		int nowWeek = getChineseWeekNum(now);
		if(nowWeek == 0){
			addNum = 1;
		}else{
			addNum = 7-nowWeek+1;
		}
		 
		calendar.setTime(now);
		calendar.add(Calendar.DAY_OF_YEAR, addNum);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * 获取当周第一天日期
	 * @param startDate
	 * @return
	 */
	public static Date getWeekStart(Date startDate) {
		int week = getChineseWeekNum(startDate);
		int subNum = 0;
		if(week == 0){
			subNum = -6;
		}else{
			subNum = -week + 1;
		}
		calendar.setTime(startDate);
		calendar.add(Calendar.DAY_OF_YEAR, subNum);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * 增加目标日期天数
	 * @param targetDate
	 * @param addDays
	 * @return
	 */
	public static Date addTargetDateDay(Date targetDate,int addDays){
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(targetDate);
		calendar.add(Calendar.DAY_OF_YEAR, addDays);
		return calendar.getTime();
	}
	
	/**
	 * 获取指定日期月份有多少天
	 * @return
	 */
	public static int getMonthHaveDayCount(Date date){
		int result = 0;
		Calendar calendar1 = Calendar.getInstance(Locale.CHINA);
		calendar1.setTime(date);
		
		Calendar calendar2 = Calendar.getInstance(Locale.CHINA);
		calendar2.clear(); 
		calendar2.set(Calendar.YEAR,calendar1.get(Calendar.YEAR)); 
		calendar2.set(Calendar.MONTH,calendar1.get(Calendar.MONTH));       
		result = calendar2.getActualMaximum(Calendar.DAY_OF_MONTH);
		return result;
	}
	
	/**
	 * 获取目标日期的第一个星期三
	 * @param date
	 * @return
	 */
	public static Date getTargetDateMonthFirstWednesday(Date date){
		date = parseDate(formatDate(date, "yyyyMM")+"01","yyyyMMdd");
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int day = 0;
		if(dayOfWeek < 4){
			day = 4 - dayOfWeek;
		}else if(dayOfWeek > 4){
			day = (7 - dayOfWeek) + 4;
		}
		calendar.add(Calendar.DAY_OF_MONTH, day);
		return calendar.getTime();
	}
	
	/**
	 * 获取单元格中的值
	 * @param cell
	 * @return
	 */
	public static String getCellValue(HSSFCell cell) {
		String val = null;
		if(null != cell){
			int cellType = cell.getCellType();
			switch(cellType){
			case HSSFCell.CELL_TYPE_BLANK:
				break;
			case HSSFCell.CELL_TYPE_FORMULA:
				if(HSSFDateUtil.isCellDateFormatted(cell)){
					val = CommonUtil.formatDate(cell.getDateCellValue(),"yyyy.M.d").trim();
				}else{
					val =cell.getCellFormula().trim();
				}
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				val = String.valueOf(cell.getBooleanCellValue());
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				if(HSSFDateUtil.isCellDateFormatted(cell)){
					val = CommonUtil.formatDate(cell.getDateCellValue(),"yyyy.M.d").trim();
				}else{
					BigDecimal bigDecimal = new BigDecimal(cell.getNumericCellValue());
					val = bigDecimal.toPlainString();
				}
				break;
			case HSSFCell.CELL_TYPE_STRING:
				val = cell.getStringCellValue().trim();
				break;
			default:
					break;
			}
		}
		
		return val;
	}
	
	/**
	 * 将字符串中包含"'"的字符替换为空白
	 * @param target		目标字符串
	 * @return
	 */
	public static String replaceTargetForBlank(String target){
		if(StringUtils.isBlank(target)){
			return "";
		}
		target = StringUtils.replace(target, "'","");
		return StringUtils.trim(target);
	}
	
    /**
     * 提供精确的加法运算。
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }
   
    /**
     * 提供精确的减法运算。
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    } 
   
    /**
     * 提供精确的乘法运算。
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        BigDecimal result = b1.multiply(b2);
        result =  result.setScale(2, BigDecimal.ROUND_HALF_UP);
        return result.doubleValue();
    }
    
    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1,double v2){
        return div(v1,v2,10);
    }
 
    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1,double v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
	/**
	 * 四舍五入,保留两位小数
	 * @param d
	 * @return
	 */
	public static Double roundHalfUp(Double d){
		if(null == d){
			return 0d;
		}
		BigDecimal bigDecimal = new BigDecimal(d);
		return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
