package com.citroen.ledp.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class ExcelUtil {

    private static Log logger = LogFactory.getLog(ExcelUtil.class);

    /**
     * 创建Excel行头
     *
     * @param titleList
     * @param sheet
     * @param headRowNumber
     * @return
     */
    public static HSSFRow createHSSFRow(List<String> titleList,
                                        List<Integer> titleWidthList,
                                        HSSFWorkbook wb,
                                        HSSFSheet sheet,
                                        Integer headRowNumber) {
        // 列头
        HSSFRow row = sheet.createRow(headRowNumber);
        if (titleList != null && titleList.size() > 0) {
            for (int i = 0; i < titleList.size(); i++) {
                sheet.setColumnWidth(i, titleWidthList.get(i) * 256);
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(titleList.get(i));

                HSSFCellStyle cellStyle = wb.createCellStyle();
                // 指定单元格居中对齐
                cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                // 指定单元格垂直居中对齐
                cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                // 指定单元格自动换行
                cellStyle.setWrapText(true);

                // 设置单元格字体
                HSSFFont font = wb.createFont();
                font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                cellStyle.setFont(font);

                // 设置单元格格式
                cell.setCellStyle(cellStyle);
            }
        }
        return row;
    }

    /**
     * 创建Excel行头
     *
     * @param titles
     * @param sheet
     * @param headRowNumber
     * @return
     */
    public static HSSFRow createHSSFRow(String[] titles,
                                        Integer[] titleWidths,
                                        HSSFWorkbook wb,
                                        HSSFSheet sheet,
                                        Integer headRowNumber) {
        // 列头
        HSSFRow row = sheet.createRow(headRowNumber);
        HSSFCellStyle cellStyle = wb.createCellStyle();
        if (titles != null && titles.length > 0) {
            for (int i = 0; i < titles.length; i++) {
                sheet.setColumnWidth(i, titleWidths[i] * 256);
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(titles[i]);
                // 指定单元格居中对齐
                cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                // 指定单元格垂直居中对齐
                cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                // 指定单元格自动换行
                cellStyle.setWrapText(true);

                // 设置单元格字体
                HSSFFont font = wb.createFont();
                font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                cellStyle.setFont(font);

                // 设置单元格格式
                cell.setCellStyle(cellStyle);
            }
        }
        return row;
    }

    /**
     * 创建Excel行头
     *
     * @param titles
     * @param sheet
     * @param headRowNumber
     * @return
     */
    public static Row createRow(String[] titles,
                                Integer[] titleWidths,
                                Workbook wb,
                                Sheet sheet,
                                Integer headRowNumber) {
        // 列头
        Row row = sheet.createRow(headRowNumber);
        CellStyle cellStyle = wb.createCellStyle();
        if (titles != null && titles.length > 0) {
            for (int i = 0; i < titles.length; i++) {
                sheet.setColumnWidth(i, titleWidths[i] * 256);
                Cell cell = row.createCell(i);
                cell.setCellValue(titles[i]);
                // 指定单元格居中对齐
                cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                // 指定单元格垂直居中对齐
                cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                // 指定单元格自动换行
                cellStyle.setWrapText(true);

                // 设置单元格字体
                Font font = wb.createFont();
                font.setBoldweight(Font.BOLDWEIGHT_BOLD);
                cellStyle.setFont(font);

                // 设置单元格格式
                cell.setCellStyle(cellStyle);
            }
        }
        return row;
    }

    public static void exportExcelData(String excelFileName,
                                       HttpServletResponse response,
                                       HttpServletRequest request,
                                       SXSSFWorkbook wb) {
        String fileName = new Date().getTime() + "";
        //获取文件名
        fileName = excelFileName + CommonUtil.formatDate(new Date(), "yyyyMMdd") + ".xlsx";
        OutputStream os = null;
        try {
            // 清空输出流
            response.reset();
            // 取得输出流
            os = response.getOutputStream();
            // 设定输出文件头
            response.setContentType("application/octet-stream;charset=utf-8");

            String userAgent = request.getHeader("User-Agent").toUpperCase();
            if (userAgent.indexOf("IE") > 0 || userAgent.contains("RV:11")) {
                //IE
                fileName = URLEncoder.encode(fileName, "utf-8");
            } else {
                fileName = new String(fileName.getBytes(), "ISO-8859-1");
            }
            response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        } catch (IOException ex) {
            // 捕捉异常
            ex.printStackTrace();
            logger.error("导出" + excelFileName + "发生异常:" + ex.getMessage());
        } finally {
            try {
                os.flush();
                wb.write(os);
                //wb.dispose();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("导出" + excelFileName + "发生异常:" + e.getMessage());
            }
        }
    }

    /**
     * 获取单元格自动高度
     *
     * @param str
     * @param fontCountInline
     * @return
     */
    public static float getExcelCellAutoHeight(String str, float fontCountInline) {
        str = StringUtils.defaultString(str, "");
        float defaultRowHeight = 12.50f;// 每一行的高度指定
        float defaultCount = 0.00f;
        for (int i = 0; i < str.length(); i++) {
            float ff = getregex(str.substring(i, i + 1));
            defaultCount = defaultCount + ff;
        }
        return ((int) (defaultCount / fontCountInline) + 1) * defaultRowHeight;// 计算
    }

    public static float getregex(String charStr) {
        if (charStr == " ") {
            return 0.5f;
        }
        // 判断是否为字母或字符
        if (Pattern.compile("^[A-Za-z0-9]+$").matcher(charStr).matches()) {
            return 0.5f;
        }
        // 判断是否为全角

        if (Pattern.compile("[\u4e00-\u9fa5]+$").matcher(charStr).matches()) {
            return 1.00f;
        }
        // 全角符号 及中文
        if (Pattern.compile("[^x00-xff]").matcher(charStr).matches()) {
            return 1.00f;
        }
        return 0.5f;

    }

    /**
     * 获取单元格中的值
     *
     * @param cell
     * @return
     */
    public static String getCellValue(HSSFCell cell) {
        String val = null;
        if (null != cell) {
            int cellType = cell.getCellType();
            switch (cellType) {
                case HSSFCell.CELL_TYPE_BLANK:
                    break;
                case HSSFCell.CELL_TYPE_FORMULA:
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        val = CommonUtil.formatDate(cell.getDateCellValue(), "yyyy.M.d").trim();
                    } else {
                        val = cell.getCellFormula().trim();
                    }
                    break;
                case HSSFCell.CELL_TYPE_BOOLEAN:
                    val = String.valueOf(cell.getBooleanCellValue());
                    break;
                case HSSFCell.CELL_TYPE_NUMERIC:
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        val = CommonUtil.formatDate(cell.getDateCellValue(), "yyyy.M.d").trim();
                    } else {
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

    //设置头
    public static void setHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
        // 设定输出文件头
        response.setContentType("application/octet-stream;charset=utf-8");

        String userAgent = request.getHeader("User-Agent").toUpperCase();
        try {
            if (userAgent.indexOf("IE") > 0 || userAgent.contains("RV:11")) {
                //IE
                fileName = URLEncoder.encode(fileName, "utf-8");
            } else {
                fileName = new String(fileName.getBytes(), "ISO-8859-1");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.addHeader("Content-Type","application/x-zip-compressed");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\".zip");
    }

    //生成excel文件
    public static void generateExcel(String excelFileName,
                                     HttpServletRequest request,
                                     HttpServletResponse response,
                                     SXSSFWorkbook wb) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(excelFileName);
            wb.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
