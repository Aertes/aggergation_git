package com.citroen.wechat.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.util.DateUtil;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.ledp.util.MapUtil;
import com.citroen.wechat.domain.Fans;
import com.citroen.wechat.domain.FansGroup;
import com.citroen.wechat.domain.PublicNo;

/**
 * @author 杨少波
 * @ClassName: FansService
 * @Description: TODO(的大卫杜夫)
 * @date 2015年6月28日 下午3:21:00
 */
@Service
public class FansService {
    private static Logger logger = Logger.getLogger(FansService.class);
    @Autowired
    private MybaitsGenericDao<Long> mybaitsGenericDao;
    @Autowired
    private ConstantService constantService;
    @Autowired
    private FansGroupService fansGroupService;

    public List<Fans> getFans(long groupId, long publicnoId) throws LedpException {
        String sql = "select * from t_fans where fans_group=" + groupId + " and publicno=" + publicnoId + " and subscribe=1 order by id asc";
        return mybaitsGenericDao.executeQuery(Fans.class, sql);
    }

    public List<Fans> getFans(long publicnoId) throws LedpException {
        FansGroup fansGroup = fansGroupService.getFansGroup(publicnoId, "1");
        String sql = "select * from t_fans where publicno=" + publicnoId + " and fans_group !=" + fansGroup.getId() + " and subscribe=1 order by id asc";
        return mybaitsGenericDao.executeQuery(Fans.class, sql);
    }

    public List<Fans> getFansNoGoup(long publicnoId) throws LedpException {
        String sql = "select * from t_fans where fans_group is null and publicno=" + publicnoId + " order by id asc";
        return mybaitsGenericDao.executeQuery(Fans.class, sql);
    }

    public Fans fansInstance(Long fansId) throws LedpException {
        return mybaitsGenericDao.get(Fans.class, fansId);
    }

    public List<Fans> executeQuery(Map params) throws LedpException {
        Map<String, Object> condition = getCondition(params);
        Map namedParams = (Map) condition.get("namedParams");
        String sql = "select * from t_fans where subscribe=1 " + condition.get("namedSql");
        sql += " order by subscribe_time desc";
        Map paginateParams = new HashMap();
        int pageNumber = (Integer) params.get("pageNumber");
        int pageSize = (Integer) params.get("pageSize");
        int offset = (pageNumber - 1) * pageSize;
        paginateParams.put("max", pageSize);
        paginateParams.put("offset", offset);
        return mybaitsGenericDao.executeQuery(Fans.class, sql, namedParams, paginateParams);

    }

    public int getTotalRow(Map params) throws LedpException {
        Map<String, Object> condition = getCondition(params);
        Map namedParams = (Map) condition.get("namedParams");
        String sql = "select count(id) count from t_fans where subscribe=1 " + condition.get("namedSql");
        List<Map> list = mybaitsGenericDao.executeQuery(sql, namedParams);
        if (list.isEmpty()) {
            return 0;
        }
        Map map = list.get(0);
        return map.isEmpty() ? 0 : Integer.parseInt(map.get("count").toString());
    }

    private Map<String, Object> getCondition(Map<String, Object> params) {
        MapUtil<String, Object> mapUtil = new MapUtil<String, Object>(params);
        Map<String, Object> namedParams = new HashMap<String, Object>();
        StringBuilder namedSql = new StringBuilder();
        if (!mapUtil.isBlank("publicNo")) {
            Long publicNoId = Long.valueOf((String) params.get("publicNo"));
            namedParams.put("publicNoId", publicNoId);
            namedSql.append(" and publicno =:publicNoId");

        }
        if (!mapUtil.isBlank("parameter[name]")) {
            String name;
            try {
                name = new String(mapUtil.get("parameter[name]").toString().getBytes("ISO-8859-1"), "UTF-8");
                namedParams.put("name", "%" + name + "%");
                namedSql.append(" and nickname like :name ");
            } catch (UnsupportedEncodingException e) {
                logger.error("异常信息：" + e.getMessage());
            }

        }
        if (!mapUtil.isBlank("parameter[startTime]")) {
        	namedSql.append(" and subscribe_time >='"+params.get("parameter[startTime]").toString()+"'");
        }
        if(!mapUtil.isBlank("parameter[endTime]")){
        	namedSql.append(" and subscribe_time <'"+params.get("parameter[endTime]").toString()+"'");
        }
        if (!mapUtil.isBlank("parameter[fansGroupId]")) {
            Long fansGroupId = Long.valueOf((String) params.get("parameter[fansGroupId]"));
            namedParams.put("fansGroupId", fansGroupId);
            namedSql.append(" and fans_group =:fansGroupId");
        }
        FansGroup fansGroup;
        try {
            fansGroup = fansGroupService.getFansGroup(Long.valueOf((String) params.get("publicNo")), "1");
            if (!mapUtil.isBlank("parameter[fansGroupId]")) {
            /*	if(Long.valueOf((String) params.get("parameter[fansGroupId]"))!=fansGroup.getId()){
					namedParams.put("fansGroupId", fansGroup.getId());
					namedSql.append(" and fans_group !=:fansGroupId");
				}*/
            } else {
                namedParams.put("fansGroupId", fansGroup.getId());
                namedSql.append(" and fans_group !=:fansGroupId");
            }
        } catch (NumberFormatException e) {
            logger.error("异常信息：" + e.getMessage());
        } catch (LedpException e) {
            logger.error("异常信息：" + e.getMessage());
        }


        Map<String, Object> rs = new HashMap<String, Object>();
        rs.put("namedSql", namedSql);
        rs.put("namedParams", namedParams);
        return rs;
    }

    public Map<String, Object> statistics(Map<String, String[]> parameterMap) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (parameterMap != null) {
            String beginDate = null, endDate = null, organizationLevel = null, organizationSelectedId = null;
            //查本月关注数/取消关注数 （每天总数分组，具体总数在程序中累加)
            String sql00 = "select count(1) as count, date_format(date_update,'%c月%d') as day,(select name from :table where id=:id) as cn,subscribe from t_fans where :condition and date_format(`date_update`, '%Y%m') = date_format(curdate() , '%Y%m') group by date_update,subscribe order by date_update";

            //查上个月关注数( 每天总数分组，具体总数在程序中累加 )
            String sql10 = "select count(1) as count, date_format(date_update,'%c月%d') as day,(select name from :table where id=:id) as cn,subscribe from t_fans where :condition and period_diff(date_format(now() , '%Y%m') , date_format(`date_update`, '%Y%m')) =1 group by date_update,subscribe order by date_update";

            // 自定义一个时间查询
            String sql20 = "select count(1) as count, date_format(date_update,'%c月%d') as day,(select name from :table where id=:id) as cn,subscribe from t_fans where :condition and unix_timestamp(date_update) >= unix_timestamp(':start') and unix_timestamp(date_update) <= unix_timestamp(':end') group by date_update,subscribe order by date_update";

            String sql = sql00; // 默认查询本月的
            String condition = null; // 条件，是大区还是网点，变量命名请忽略
            String table = null; // 表，大区或者是网点

            boolean flag0 = true, flag1 = true; // 标识部分字段是否需要赋值

            // 对查询参数进行处理, 这段代码非常难看，后面考虑优化
            for (Iterator<Map.Entry<String, String[]>> iterator = parameterMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String[]> entry = iterator.next();
                String key = entry.getKey();
                // 分页参数暂时不处理

                if (key.equals("beginDate")) {
                    if (flag1) {
                        beginDate = entry.getValue()[0];
                        Date _d = DateUtil.parse(beginDate,"yyyy-MM-dd");
                        beginDate = DateUtil.format(_d);
                    }
                } else if (key.equals("endDate")) {
                    if (flag1) {
                        endDate = entry.getValue()[0];
                        Date _d = DateUtil.parse(endDate,"yyyy-MM-dd");
                        endDate = DateUtil.format(_d);
                    }
                } else if (key.equals("effect")) {
                    String[] v = entry.getValue();
                    // 如果不是自定义的，页面参数时间就认为是无效的
                    if ("0".equals(v[0])) { // 本月
                        sql = sql00;
                        flag1 = false;
                    } else if ("-1".equals(v[0])) { // 上月
                        sql = sql10;
                        flag1 = false;
                    } else if ("01".equals(v[0])) { // 本周
                        sql = sql20;
                        endDate = DateUtil.format(DateUtil.getSundayOfThisWeek());
                        beginDate = DateUtil.format(DateUtil.getMondayOfThisWeek());
                        flag1 = false;
                    } else if ("99".equals(v[0])) { // 自定义
                        sql = sql20;
                    }
                } else if (key.equals("organizationLevel")) {
                    if (flag0) {
                        organizationLevel = entry.getValue()[0];
                    }
                } else if (key.equals("organizationSelectedId")) {
                    // 这边即使被赋值，也可能会被网点替换，应该可以优化,这个判断的主要作用是保证网点是优先的，
                    // 如果网点有值，这段代码就不应该被执行
                    if (flag0) {
                        table = "t_organization";
                        condition = "area=:id";
                        organizationSelectedId = entry.getValue()[0];
                        if ("1".equals(organizationSelectedId)) { // 总部
                            organizationLevel = "1";
                            flag0 = false; // 总部的层级，页面层级忽略
                        }
                    }
                } else if (key.equals("ledpDealer")) {
                    String dealer = entry.getValue()[0];
                    if (!dealer.isEmpty()) { // 如果网点选择不是空的，认为层级和ID选择的网点，页面参数层级参数和大区ID无效
                        organizationLevel = "3"; // 网点
                        organizationSelectedId = dealer;
                        flag0 = false;

                        table = "t_dealer";
                        condition = "dealer=:id";
                    }
                } else {
                    // 这里没有其他参数
                }
            }
            if (!organizationSelectedId.matches("^\\d+$")) {
                organizationSelectedId = "1";
            }
            if(organizationLevel.equals("1")) {
                condition = "1 = 1"; //总部看所有的
            }

            if(beginDate != null && endDate != null) {
                if((DateUtil.parse(endDate).getTime() - DateUtil.parse(beginDate).getTime()) / (60 * 60 * 1000 * 24) >= 61) {
                    condition = " 1 = 2";
                }
            }

            sql = sql.replaceAll(":condition", condition).replaceAll(":table", table).replaceAll(":id", organizationSelectedId)
                    .replaceAll(":start", beginDate).replaceAll(":end", endDate);

            List<Map> maps = null;
            try {
                maps = mybaitsGenericDao.executeQuery(sql);
            } catch (LedpException e) {
                logger.error("异常信息：" + e.getMessage());
            }

            String c1 = "0",c2 = "0", c3 = "0";
            Map<String,Object> countMap = new HashMap<String, Object>(); // 存放关注总数，取消关注总数，留资总数
            List categories = new ArrayList(); // 横轴数据
            List data1 = new ArrayList(); // 每天关注数集合
            List data2 = new ArrayList(); // 每天留资数集合
            List<Map> list = new ArrayList<Map>(); // 列表数据
            if (maps != null) {
                int total0 = 0, total1 = 0; // 取消关注的总数,关注的总数

                Map _retMap = new HashMap(); // 列表实体
                for(Iterator<Map> iterator = maps.iterator();iterator.hasNext();) {
                    Map m = iterator.next();
                    Object day = m.get("day");
                    Object cn = m.get("cn");
                    Object o = m.get("subscribe");
                    Object count = m.get("count");

                    _retMap.put("cn",cn);
                    _retMap.put("date", day);

                    if("0".equals(o.toString())) { // 如果本条记录是取消关注的总数
                        _retMap.put("s0", count);
                        data2.add(count); // 因为留资这边没有取，需要部分假数据。这里用取消关注的数量充当留资数
                        total0 += Integer.parseInt(String.valueOf(count));
                    } else if("1".equals(o.toString())) { // 如果本条记录是关注的总数
                        _retMap.put("s1",count);
                        data1.add(count);
                        total1 += Integer.parseInt(String.valueOf(count));
                    }

                    if(_retMap.containsKey("s0") && _retMap.containsKey("s1")) {
                        list.add(_retMap);
                        categories.add(day);

                        _retMap = new HashMap();
                    }
                }

                c1 = String.valueOf(total1); // 关注数
                c2 = String.valueOf(total0); // 取消关注数
                c3 = String.valueOf(total0 + total1); // 留资的给个假数据

            }

            countMap.put("c1",c1);
            countMap.put("c2",c2);
            countMap.put("c3",c3);

            result.put("level", organizationLevel);
            result.put("categories",categories);
            result.put("data1",data1);
            result.put("data2",data2);
            result.put("ds0",list);
            result.put("ds1",countMap);
        }

        return result;
    }

    public SXSSFWorkbook createWorkbook(Map<String, String[]> parameterMap) {
        Map result = statistics(parameterMap);
        SXSSFWorkbook wb = new SXSSFWorkbook();
        if(result != null) {
            Sheet sheet = wb.createSheet("粉丝信息导出记录");
            String[] titles = new String[]{
                    "时间","大区","关注人数","取消关注人数"
            };
            Integer[] titleWidths = new Integer[]{12,12,12, 10};
            String[] columns = new String[] {
                    "date", "cn", "s1", "s0"
            };
            // 列头
            Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);

            List<Map> mapList = (List<Map>) result.get("ds0");

            if(mapList != null) {
                for(int i = 0; i < mapList.size(); ++i ){
                    row = sheet.createRow(i + 1);
                    Map map = mapList.get(i);

                    for(int j = 0; j < columns.length; ++j) {
                        row.createCell(j).setCellValue(map.get(columns[j]).toString());
                    }
                }
            }
        }

        return wb;
    }
    
	public Fans getFans(String appid,String openid) throws LedpException{
		return mybaitsGenericDao.find(Fans.class,"select f.* from t_fans f,t_publicno p where p.id=f.publicno and p.appid='"+appid+"' and f.openid='"+openid+"'");
	}
	
	public Fans getFansByOpenid(String openid) throws LedpException{
		return mybaitsGenericDao.find(Fans.class,"select f.* from t_fans f,t_publicno p where p.id=f.publicno and f.openid='"+openid+"'");
	}
	
	public void save(String appid,Map params) throws LedpException{
		PublicNo publicNo = mybaitsGenericDao.find(PublicNo.class,"select id from t_publicno where appid='"+appid+"'");
		Fans fans = new Fans();
		fans.setPublicNo(publicNo);
		fans.setRemark("第一次保存粉丝");
		if(params.containsKey("unionid")){
			fans.setUnionid((String)params.get("unionid"));
		}
		if(params.containsKey("nickname")){
			fans.setNickName((String)params.get("nickname"));
		}
		if(params.containsKey("openid")){
			fans.setOpenId((String)params.get("openid"));
		}
		if(params.containsKey("sex")){
			fans.setSex(params.get("sex").toString());
		}
		if(params.containsKey("province")){
			fans.setProvince((String)params.get("province"));
		}
		if(params.containsKey("city")){
			fans.setCity((String)params.get("city"));
		}
		if(params.containsKey("country")){
			fans.setCountry((String)params.get("country"));
		}
		if(params.containsKey("language")){
			fans.setLanguage((String)params.get("language"));
		}
		if(params.containsKey("headimgurl")){
			fans.setHeadImgUrl((String)params.get("headimgurl"));
		}
		if(params.containsKey("privilege")){
			fans.setPrivilege((String)params.get("privilege"));
		}
		if(params.containsKey("access_token")){
			fans.setAccess_token((String)params.get("access_token"));
		}
		if(params.containsKey("refresh_token")){
			fans.setRefresh_token((String)params.get("refresh_token"));
		}
		try{
			mybaitsGenericDao.save(fans);
		}catch(Exception e){
			fans.setNickName((String)params.get("openid"));
			mybaitsGenericDao.save(fans);
		}
	}
	
	public void update(Fans fans,Map params) throws LedpException{
		if(params.containsKey("unionid")){
			fans.setUnionid((String)params.get("unionid"));
		}
		if(params.containsKey("nickname")){
			fans.setNickName((String)params.get("nickname"));
		}
		if(params.containsKey("openid")){
			fans.setOpenId((String)params.get("openid"));
		}
		if(params.containsKey("sex")){
			fans.setSex((String)params.get("sex"));
		}
		if(params.containsKey("province")){
			fans.setProvince((String)params.get("province"));
		}
		if(params.containsKey("city")){
			fans.setCity((String)params.get("city"));
		}
		if(params.containsKey("country")){
			fans.setCountry((String)params.get("country"));
		}
		if(params.containsKey("language")){
			fans.setLanguage((String)params.get("language"));
		}
		if(params.containsKey("headimgurl")){
			fans.setHeadImgUrl((String)params.get("headimgurl"));
		}
		if(params.containsKey("privilege")){
			fans.setPrivilege((String)params.get("privilege"));
		}
		if(params.containsKey("access_token")){
			fans.setAccess_token((String)params.get("access_token"));
		}
		if(params.containsKey("refresh_token")){
			fans.setRefresh_token((String)params.get("refresh_token"));
		}
		try{
			mybaitsGenericDao.update(fans);
		}catch(Exception e){
			fans.setNickName((String)params.get("openid"));
			mybaitsGenericDao.update(fans);
		}
	}
}
