package com.citroen.ledp.controller;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.VehicleSeries;
import com.citroen.ledp.domain.VehicleSeriesIntention;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.IntentionService;
import com.citroen.ledp.service.VehicleSeriesService;
import com.citroen.ledp.util.JSONConverter;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maskx on 2017/5/22.
 */
@Controller
@RequestMapping("/intention")
public class IntentionController {

    private static Logger logger = Logger.getLogger(IntentionController.class);

    @Autowired
    private IntentionService intentionService;

    @Autowired
    private VehicleSeriesService vehicleSeriesService;

    @RequestMapping(value = { "", "/index" })
    public String index() {
        logger.info("intention index");
        return "intention/index";
    }

    @RequestMapping(value={"/search"})
    @ResponseBody
    @com.citroen.ledp.interceptor.Permission(code={"intention/index"})
    public JSON search(HttpServletRequest request, @ModelAttribute("intention")VehicleSeriesIntention intention) throws Exception {
        logger.info("intention search");

        final String contextPath = request.getContextPath();
        int pageSize   = 10;
        int pageNumber = 1;
        try{
            pageSize = Integer.parseInt(request.getParameter("pageSize"));
        }catch(Exception e){}
        try{
            pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
        }catch(Exception e){}

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", pageSize);
        params.put("pageNumber", pageNumber);
        params.put("vehicleSeriesCode", intention.getVehicleSeriesCode());
        params.put("vehicleSeriesName", intention.getVehicleSeriesName());

        List<VehicleSeriesIntention> intentions = intentionService.executeQuery(params);

        int total = intentionService.getTotalRow(params);

        return JSONConverter.convert(pageSize, pageNumber, "", "", total, intentions, new String[]{"vehicleSeriesCode", "vehicleSeriesName"},
                new JSONConverter.Operation<VehicleSeriesIntention>() {
                    @Override
                    public String operate(VehicleSeriesIntention i) {
                        return "<a href='#' title='删除' d_id=" + i.getId()
                                + " status_code='active' class='ONOFF' onclick='delete_message(\"" + contextPath + "/intention/delete/" + i.getId()
                                + "\",\"确定要删除吗？\",false,deleteCallback)'><img alt='' src='../images/del.png' />";
                    }
                }
        );

    }

    @RequestMapping(value="/create")
    @com.citroen.ledp.interceptor.Permission(code={"intention/index"})
    public String create(Model model, @ModelAttribute("intention")VehicleSeriesIntention intention) throws Exception {
        model.addAttribute("intention", intention);
        return "intention/create";
    }

    @RequestMapping(value="/check")
    @ResponseBody
    @com.citroen.ledp.interceptor.Permission(code={"intention/index"})
    public Object check(HttpServletRequest request) throws Exception {
        logger.info("intention check...");
        String param = request.getParameter("param");
        logger.info("param:" + param);
        Map<String, String> map = new HashMap<String, String>();

        VehicleSeriesIntention ie = intentionService.find(param);
        if (null != ie) {
            map.put("status", "n");
            map.put("info", "车型重复");
            return map;
        }
        VehicleSeries c = vehicleSeriesService.findC(param);
        if (null == c) {
            map.put("status", "n");
            map.put("info", "车型不存在");
            return map;
        }
        Constant status = c.getStatus();
        Long id = status.getId();
        if (1010 != id) {
            map.put("status", "n");
            map.put("info", "车型未启用");
            return map;
        }
        map.put("status", "y");
        map.put("info", "");
        return map;

    }

    @RequestMapping(value="/name/{code}")
    @ResponseBody
    @com.citroen.ledp.interceptor.Permission(code={"intention/index"})
    public Object name(@PathVariable String code) throws Exception {
        if (StringUtils.isBlank(code)) {
            return "";
        }
        Map<String, String> map = new HashMap<String, String>();
        VehicleSeries c = vehicleSeriesService.findC(code);
        if (null != c) {
            map.put("name", c.getName());
        }
        return map;

    }

    @RequestMapping(value="/save",method= RequestMethod.POST)
    @com.citroen.ledp.interceptor.Permission(code={"intention/index"})
    public String save(@Valid @ModelAttribute("intention") VehicleSeriesIntention intention, BindingResult br) throws LedpException {
        String code;

        if (null == intention) {
            br.rejectValue("vehicleSeriesCode", "", "车型代码不能为空");
            return "intention/create";
        }
        code = intention.getVehicleSeriesCode();
        if (null == code) {
            br.rejectValue("vehicleSeriesCode", "", "车型代码不能为空");
            return "intention/create";
        }
        VehicleSeriesIntention ie = intentionService.find(code);
        if (null != ie) {
            br.rejectValue("vehicleSeriesCode", "", "车型重复");
            return "intention/create";
        }

        VehicleSeries c = vehicleSeriesService.findC(code);
        if (null == c) {
            br.rejectValue("vehicleSeriesCode", "", "车型不存在");
            return "intention/create";
        }
        Constant status = c.getStatus();
        Long id = status.getId();
        if (1010 != id) {
            br.rejectValue("vehicleSeriesCode", "", "车型未启用");
            return "intention/create";
        }
        intention.setVehicleSeriesName(c.getName());
		Date now = new Date();
		intention.setCreateTime(now);
		intention.setUpdateTime(now);
		intentionService.save(intention);

        return "redirect:/intention/index";
    }

    @RequestMapping(value = "delete/{id}")
    @ResponseBody
    @com.citroen.ledp.interceptor.Permission(code={"intention/index"})
    public Object delete(@PathVariable long id) {
        Map json = new HashMap<String, String>();
        try {
            int count = intentionService.count();
            if (count <= 1) {
                json.put("code", "success_message");
                json.put("message", "至少要有一条记录！");
            } else {
                intentionService.delete(id);
                json.put("code", "success_message");
                json.put("message", "删除成功！");
            }
        } catch (LedpException e) {
            logger.error(e);
            json.put("code", "fail_message");
            json.put("message", "删除失败！");
        }
        return json;
    }
}
