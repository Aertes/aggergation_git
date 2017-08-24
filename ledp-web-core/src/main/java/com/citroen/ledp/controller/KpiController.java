package com.citroen.ledp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.citroen.ledp.domain.Kpi;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.KpiService;

/**
 * @Title: kpiController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(客户管理类)
 * @author 廖启洪
 * @date 2015年1月29日 下午3:24:24
 * @version V1.0
 */
@Controller
@RequestMapping("/kpi")
public class KpiController {

	@Autowired
	private KpiService kpiService;

	@RequestMapping(value = { "", "/index" })
	@Permission(code="kpi/index")
	public String index(Model model) throws Exception {
		List<Kpi> kpis = kpiService.queryKpiList();
		model.addAttribute("kpis", kpis);
		return "kpi/index";
	}

	/**
	 * @Title: create
	 * @Description: TODO(添加客户入口)
	 * @param model
	 *            参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value = "/create")
	@Permission(code="kpi/create")
	public String create(Model model) throws Exception {
		return "kpi/create";
	}

	/**
	 * @Title: save
	 * @Description: TODO(新增客户方法)
	 * @param model
	 *            参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@Permission(code="kpi/create")
	public String save(Model model, @ModelAttribute("kpi") Kpi kpi) throws Exception {
		return "kpi/index";
	}

	/**
	 * @Title: update
	 * @Description: TODO(修改客户入口)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            客户ID
	 * @return String
	 */
	@RequestMapping(value = "/update")
	@Permission(code="kpi/update")
	public String update(Model model) {
		List<Kpi> kpis;
		try {
			kpis = kpiService.queryKpiList();
			for (Kpi k : kpis) {
				if (k.getType().equals("leads")) {
					model.addAttribute("leads", k);
				} else if (k.getType().equals("media")) {
					model.addAttribute("media", k);
				} else if (k.getType().equals("400phone")) {
					model.addAttribute("phone", k);
				}
			}
		} catch (LedpException e) {
			e.printStackTrace();
		}
		return "kpi/update";
	}

	/**
	 * @Title: edit
	 * @Description: TODO(修改客户方法)
	 * @param model
	 *            参数传递容器
	 * @return String
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@Permission(code="kpi/update")
	public String edit(Model model, String leads_v, String leads_s, String leads_w, String phone_v, String phone_s,
			String phone_w, String media_v, String media_s, String media_w, long leadsId, long mediaId, long phoneId) {

		Kpi media = kpiService.get(mediaId);
		Kpi phone = kpiService.get(phoneId);
		Kpi leads = kpiService.get(leadsId);
		if (media != null) {
			media.setThreshold(Integer.parseInt(media_v));
			media.setScore(Integer.parseInt(media_s));
			media.setWeight(Integer.parseInt(media_w));
			try {
				kpiService.update(media);
			} catch (LedpException e) {
				e.printStackTrace();
				return "redirect:/kpi/update";
			}
		}
		if (phone != null) {
			phone.setThreshold(Integer.parseInt(phone_v));
			phone.setScore(Integer.parseInt(phone_s));
			phone.setWeight(Integer.parseInt(phone_w));
			try {
				kpiService.update(phone);
			} catch (LedpException e) {
				e.printStackTrace();
				return "redirect:/kpi/update";
			}
		}
		if (leads != null) {
			leads.setThreshold(Integer.parseInt(leads_v));
			leads.setScore(Integer.parseInt(leads_s));
			leads.setWeight(Integer.parseInt(leads_w));
			try {
				kpiService.update(leads);
			} catch (LedpException e) {
				e.printStackTrace();
				return "redirect:/kpi/update";
			}
		}
		return "redirect:/kpi/index";
	}

	/**
	 * @Title: detail
	 * @Description: TODO(查看客户方法)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            客户ID
	 * @return String
	 */
	@RequestMapping(value = "/detail/{id}")
	public String detail(Model model, @PathVariable long id) {
		return "kpi/detail";
	}

}
