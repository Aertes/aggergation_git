package com.citroen.ledp.controller;

import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.citroen.ledp.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Leads;
import com.citroen.ledp.domain.Media;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.LeadsService;
import com.citroen.ledp.service.MediaService;
import com.citroen.ledp.service.OrganizationService;
import com.citroen.ledp.service.PermissionService;

/**
 * @author 廖启洪
 * @version V1.0
 * @Title: leadsController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(线索管理类)
 * @date 2015年1月29日 下午3:24:24
 */
@Controller
@RequestMapping("/leads")
public class LeadsController {
    private Logger logger = Logger.getLogger(LeadsController.class);
    private Map<String, Object> params;

    @Autowired
    private LeadsService leadsService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value = {"", "/index"})
    @Permission(code = "leads/index")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session, String parent) throws Exception {
        // 媒体渠道
        List<Media> mediaList = mediaService.listAll();
        model.addAttribute("mediaList", mediaList);

        model.addAttribute("parent", parent);
        model.addAttribute(params);

        //获取用户
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        //获取组织机构
        Organization org = loginUser.getOrg();
        //网点
        Dealer dealer = loginUser.getDealer();
        if (null == org && null != dealer) { //网点用户
            model.addAttribute("dealer", "true");
        } else {
            model.addAttribute("dealer", "false");
        }
        return "leads/index";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = {"search"})
    @ResponseBody
    @Permission(code = "leads/index")
    public JSON search(Model model, HttpServletRequest request) throws Exception {

        int pageSize = 10;
        int pageNumber = 1;
        String sortName = request.getParameter("sortName");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isBlank(sortName)) {
            sortName = "ledpDealer.name";
        }
        if (StringUtils.isBlank(sortOrder)) {
            sortOrder = "asc";
        }
        try {
            pageSize = Integer.parseInt(request.getParameter("pageSize"));
        } catch (Exception e) {
        }
        try {
            pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
        } catch (Exception e) {
        }
        params.put("sortOrder", sortOrder);
        params.put("sortName", sortName);
        List<Leads> rows = leadsService.executeQuery(params);

        int total = leadsService.getTotalRow(params);
        final String contextPath = request.getContextPath();
        JSON data = JSONConverter.convert(pageSize, pageNumber, sortName, sortOrder, total, rows,
                new String[]{
                        "name", "phone", "ledpType.name", "ledpMedia.name", "ledpDealer.name",
                        "ledpIntent.name", "createTime", "ledpFollow.name"
                },
                new JSONConverter.Operation<Leads>() {
                    public String operate(Leads t) {
                        if (permissionService.hasAuth(SysConstant.PERMISSION_LEADS_DETAIL)) {
                            return "<a href='" + contextPath + "/leads/detail/" + t.getId()
                                    + "' title='查看'><img alt='' src='" + contextPath + "/images/magnifier.png'></a>";
                        }
                        return "";
                    }
                });
        return data;
    }

    /**
     * @param model 参数传递容器
     * @return String
     * @throws Exception
     * @Title: create
     * @Description: TODO(添加线索入口)
     */
    @RequestMapping(value = "/create")
    @Permission(code = "leads/create")
    public String create(Model model) throws Exception {
        return "leads/create";
    }

    /**
     * @param model 参数传递容器
     * @return String
     * @throws Exception
     * @Title: save
     * @Description: TODO(新增线索方法)
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @Permission(code = "leads/create")
    public String save(Model model, @ModelAttribute("leads") Leads leads) throws Exception {
        return "leads/index";
    }

    /**
     * @param model 参数传递容器
     * @param id    线索ID
     * @return String
     * @Title: update
     * @Description: TODO(修改线索入口)
     */
    @RequestMapping(value = "/update/{id}")
    @Permission(code = "leads/update")
    public String update(Model model, @PathVariable long id) {
        return "leads/update";
    }

    /**
     * @param model 参数传递容器
     * @return String
     * @Title: edit
     * @Description: TODO(修改线索方法)
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @Permission(code = "leads/update")
    public String edit(Model model, @ModelAttribute("leads") Leads leads) {
        return "leads/index";
    }

    /**
     * @param model 参数传递容器
     * @param id    线索ID
     * @return String
     * @throws LedpException
     * @Title: detail
     * @Description: TODO(查看线索方法)
     */
    @RequestMapping(value = "/detail/{id}")
    @Permission(code = "leads/detail")
    public String detail(Model model, @PathVariable long id) throws LedpException {
        if (!permissionService.hasAuth(SysConstant.PERMISSION_LEADS_DETAIL)) {
            return "redirect:/leads/index";
        }
        Leads leads = leadsService.get(id);
        model.addAttribute("leads", leads);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateCreate = leads.getDateCreate() != null ? leads.getDateCreate() : null;
        if (null != dateCreate) {
            String dateCreateStr = sdf.format(dateCreate);
            model.addAttribute("leadCreateDate", dateCreateStr);
        }
        // 判断是否为400电话留资
        if (leads.getLedpType().getId().longValue() == SysConstant.LEADS_TYPE_PHONE.longValue()) {
            return "leads/detail400";
        }
        return "leads/detail";
    }

    /**
     * 导出
     *
     * @return
     * @throws LedpException
     */
    @RequestMapping(value = "/doExport")
    @Permission(code = "leads/index")
    public void doExport(HttpServletResponse response, HttpServletRequest request) {
        PropertiyUtil prop = new PropertiyUtil("/config.properties");
        String tempPath = prop.getString("export.leads.temp");
        SXSSFWorkbook wb = null;
        String fileName = new Date().getTime() + "";
        //每个文件30w条记录
        int batchCount = 300000;
        FileInputStream inStream = null;
        OutputStream out = null;
        File srcfile[] = null;
        File zip = null;
        List<String> fileNames = new ArrayList();// 用于存放生成的文件名称s
        try {
            //总条数
            int totalCount = leadsService.getTotalRow(params);
            int count = totalCount / batchCount;
            //总页数
            int totalPageSize = totalCount % batchCount == 0 ? count : (count + 1);
            zip = new File(tempPath + fileName + ".zip");// 压缩文件
            for (int p = 1; p <= totalPageSize; p++) {
                params.put("pageSize", batchCount);
                //当前页数
                params.put("offset", (p - 1) * batchCount);
                wb = leadsService.createWorkbook(params);
                String xlsName = tempPath + fileName + "-" + p + ".xlsx";
                fileNames.add(xlsName);
                //生成excel8
                ExcelUtil.generateExcel(xlsName, request, response, wb);
            }
            //设置导出文件header
            ExcelUtil.setHeader(request, response, fileName);
            out = response.getOutputStream();
            srcfile = new File[fileNames.size()];
            for (int i = 0, n = fileNames.size(); i < n; i++) {
                srcfile[i] = new File(fileNames.get(i));
            }
            //对文件进行压缩
            FileZip.ZipFiles(srcfile, zip);
            inStream = new FileInputStream(zip);
            byte[] buf = new byte[4096];
            int readLength;
            while (((readLength = inStream.read(buf)) != -1)) {
                out.write(buf, 0, readLength);
            }
        } catch (Exception e) {
            logger.error("导出文件出现异常，信息：" + e.getMessage());
        } finally {
            try {
                inStream.close();
                out.close();
                for (int i = 0; i < srcfile.length; i++) {
                    if (srcfile[i].exists()) {
                        srcfile[i].delete();
                    }
                }
                zip.delete();

            } catch (Exception e) {
                logger.error("异常信息："+e.getMessage());
                inStream = null;
                out = null;
            }
        }
    }

}
