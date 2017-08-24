package com.citroen.ledp.controller;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.domain.*;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.DealerMappingService;
import com.citroen.ledp.util.JSONConverter;
import com.citroen.ledp.vo.DealerMappingVO;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maskx on 2017/5/22.
 */
@Controller
@RequestMapping("/dealerMapping")
public class DealerMappingController {

    private static Logger logger = Logger.getLogger(DealerMappingController.class);

    @Autowired
    private DealerMappingService dealerMappingService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @RequestMapping(value = {"","/index"})
    public String index(Model model) {

        return "dealerMapping/index";
    }

    @RequestMapping(value={"/search"})
    @ResponseBody
    @com.citroen.ledp.interceptor.Permission(code={"dealerMapping/index"})
    public JSON search(HttpServletRequest request, @ModelAttribute("dealerMapping")DealerMappingVO dealerMapping, Integer pageSize, Integer pageNumber) throws Exception {

        final String contextPath = request.getContextPath();
        if (null == pageSize) {
            pageSize = 10;
        }
        if (null == pageNumber) {
            pageNumber = 1;
        }

        List<DealerMappingVO> dealerMappings = dealerMappingService.query(dealerMapping, pageSize, pageNumber);

        int total = dealerMappingService.totalRow(dealerMapping);

        return JSONConverter.convert(pageSize, pageNumber, "", "", total, dealerMappings,
                new String[]{"sourceDealer", "sourceDealerName", "targetDealer", "targetDealerName", "mappingReasonPhrase", "mappingBegDate", "mappingEndDate"},
                new JSONConverter.Operation<DealerMappingVO>() {
                    public String operate(DealerMappingVO t) {
                        return "<a href='"+contextPath+"/dealerMapping/update/" + t.getId()
                                    + "' title='编辑' class='edit'><img alt='' src='../images/edit.png' /></a>";
                    }
                },
                new JSONConverter.Operation<DealerMappingVO>() {
                    @Override
                    public String operate(DealerMappingVO i) {
                        return "<a href='#' title='删除' d_id=" + i.getId()
                                + " status_code='active' class='ONOFF' onclick='delete_message(\"" + contextPath + "/dealerMapping/delete/" + i.getId()
                                + "\",\"确定要删除吗？\",false,deleteCallback)'><img alt='' src='../images/del.png' />";
                    }
                }
        );
    }

    @RequestMapping(value="/create")
    @com.citroen.ledp.interceptor.Permission(code={"dealerMapping/index"})
    public String create(Model model, @ModelAttribute("dealerMapping")DealerMapping dealerMapping) throws Exception {
        model.addAttribute("dealerMapping", dealerMapping);
        return "dealerMapping/create";
    }

    @RequestMapping(value="/check")
    @ResponseBody
    @com.citroen.ledp.interceptor.Permission(code={"dealerMapping/index"})
    public Object check(HttpServletRequest request) throws Exception {
        String name = request.getParameter("name");
        String param = request.getParameter("param");
        Map<String, String> map = new HashMap<String, String>();

        Dealer dealer = dealerMappingService.findD(param);
        if (null == dealer) {
            map.put("status", "n");
            map.put("info", "网点不存在");
            return map;
        }

        if ("sourceDealer".equals(name)) {
            DealerMapping dealerMapping = dealerMappingService.find(param);
            if (null != dealerMapping) {
                map.put("status", "n");
                map.put("info", "源网点已配置映射关系");
                return map;
            }
        }
        map.put("status", "y");
        map.put("info", "");
        return map;

    }

    @RequestMapping(value="/name/{dealer}")
    @ResponseBody
    @com.citroen.ledp.interceptor.Permission(code={"dealerMapping/index"})
    public Object name(@PathVariable String dealer) throws Exception {
        if (StringUtils.isBlank(dealer)) {
            return "";
        }
        Map<String, String> map = new HashMap<String, String>();
        Dealer d = dealerMappingService.findD(dealer);
        if (null != d) {
            map.put("name", d.getName());
        }
        return map;
    }

    @RequestMapping(value="/save",method= RequestMethod.POST)
    @com.citroen.ledp.interceptor.Permission(code={"dealerMapping/index"})
    public String save(@ModelAttribute("dealerMapping")DealerMappingVO dealerMapping, BindingResult br) throws LedpException {

        String sourceDealer = dealerMapping.getSourceDealer();
        String targetDealer = dealerMapping.getTargetDealer();
		Date beg = toDate(dealerMapping.getMappingBegDate());
		Date end = toDate(dealerMapping.getMappingEndDate());
		if (null == sourceDealer) {
            br.rejectValue("sourceDealer", "", "源网点不能为空");
            return "dealerMapping/create";
        }
        DealerMapping d = dealerMappingService.find(sourceDealer);
        if (null != d) {
            br.rejectValue("sourceDealer", "", "源网点重复");
            return "dealerMapping/create";
        }

        Dealer d1 = dealerMappingService.findD(sourceDealer);
        if (null == d1) {
            br.rejectValue("sourceDealer", "", "源网点不存在");
            return "dealerMapping/create";
        }
        Dealer d2 = dealerMappingService.findD(targetDealer);
        if (null == d2) {
            br.rejectValue("targetDealer", "", "目标网点不存在");
            return "dealerMapping/create";
        }
        if (sourceDealer.equals(targetDealer)) {
            br.rejectValue("targetDealer", "", "目标网点和源网点相同");
            return "dealerMapping/create";
        }
        DealerMapping dm = new DealerMapping();
        dm.setSourceDealer(sourceDealer);
        dm.setTargetDealer(targetDealer);
        dm.setMappingReasonPhrase(dealerMapping.getMappingReasonPhrase());
        dm.setMappingBegDate(beg);
        dm.setMappingEndDate(end);
		Date now = new Date();
		dm.setCreateTime(now);
		dm.setUpdateTime(now);
		dealerMappingService.save(dm);

        return "redirect:/dealerMapping/index";
    }

    @RequestMapping(value="/update/{id}")
    @com.citroen.ledp.interceptor.Permission(code={"dealerMapping/index"})
    public String update(Model model,@PathVariable long id) throws LedpException {
        DealerMappingVO dealerMapping = dealerMappingService.get(id);
        model.addAttribute("dealerMapping",dealerMapping);
        return "dealerMapping/update";
    }

    @RequestMapping(value="/edit",method= RequestMethod.POST)
    @com.citroen.ledp.interceptor.Permission(code={"dealerMapping/index"})
    public String edit(@ModelAttribute("dealerMapping")DealerMappingVO dealerMapping, BindingResult br) throws LedpException {

        String targetDealer = dealerMapping.getTargetDealer();
		Date beg = toDate(dealerMapping.getMappingBegDate());
		Date end = toDate(dealerMapping.getMappingEndDate());

        Dealer d2 = dealerMappingService.findD(targetDealer);
        if (null == d2) {
            br.rejectValue("targetDealer", "", "目标网点不存在");
            return "dealerMapping/update";
        }

        DealerMapping updateDealerMapping = dealerMappingService.find(dealerMapping.getId());

        if (updateDealerMapping.getSourceDealer().equals(targetDealer)) {
            br.rejectValue("targetDealer", "", "目标网点和源网点相同");
            return "dealerMapping/update";
        }

        updateDealerMapping.setTargetDealer(dealerMapping.getTargetDealer());
        updateDealerMapping.setMappingReasonPhrase(dealerMapping.getMappingReasonPhrase());
        updateDealerMapping.setMappingBegDate(beg);
        updateDealerMapping.setMappingEndDate(end);
        updateDealerMapping.setUpdateTime(new Date());

        dealerMappingService.update(updateDealerMapping);

        return "redirect:/dealerMapping/index";
    }

    @RequestMapping(value = "delete/{id}")
    @ResponseBody
    @com.citroen.ledp.interceptor.Permission(code={"dealerMapping/index"})
    public Object delete(@PathVariable long id) {
        Map json = new HashMap<String, String>();
        try {
            dealerMappingService.delete(id);
            json.put("code", "success_message");
            json.put("message", "删除成功！");
        } catch (LedpException e) {
            logger.error(e);
            json.put("code", "fail_message");
            json.put("message", "删除失败！");
        }
        return json;
    }

    private Date toDate(String mappingDate) {
        if (StringUtils.isBlank(mappingDate)) {
            return null;
        } else {
            try {
                return sdf.parse(mappingDate);
            } catch (ParseException e) {
                return null;
            }
        }
    }
}
