package com.citroen.ledp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
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
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.DealerMedia;
import com.citroen.ledp.domain.Media;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.OrganizationService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.JSONConverter;
import com.citroen.ledp.util.LedpLogger;
import com.citroen.ledp.util.LedpLogger.Operation;
import com.citroen.ledp.util.LedpLogger.Result;

/**
 * @Title: dealerController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(网点管理类)
 * @author 廖启洪
 * @date 2015年1月29日 下午3:24:24
 * @version V1.0
 */
@Controller
@RequestMapping("/dealer")
public class DealerController {

	/* 默认页面显示大小 */
	private static final int PAGE_SIZE = 10;
	private static final int PAGE_NUMBER = 1;

	private static final String CONSTANT_CATEGORY = "dealer_type";
    private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private DealerService dealerService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private ConstantService constantService;
	@Autowired
	private PermissionService permissionService;

	@RequestMapping(value = { "", "/index" })
	@Permission(code="dealer/index")
	public String index(Model model,HttpServletRequest request) throws Exception {
		String parentId=request.getParameter("parentId");
		if(parentId==null || parentId.equals("")){
			Organization organization = organizationService.getFirstOrganization();
			model.addAttribute("parentId", organization.getId());
		}else{
			model.addAttribute("parentId", Long.parseLong(parentId));
		}
		
		if(permissionService.hasAuth("dealer/create")){
			model.addAttribute("permission","permission");
		}
		return "dealer/index";
	}

	/**
	 * @Title: create
	 * @Description: TODO(添加网点入口)
	 * @param model
	 *            参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value = "/create")
	@Permission(code="dealer/create")
	public String create(Model model, long orgId, @ModelAttribute("dealer") Dealer dealer) throws Exception {

		Organization organization = dealerService.getOrganization(orgId);
		List<Constant> typeList = dealerService.getTypeList(CONSTANT_CATEGORY);
		List<Media> mediaList = dealerService.getMediaList();

		List<User> managerList = dealerService.getUsers(orgId);

		model.addAttribute("organization", organization);
		model.addAttribute("dealer", dealer);
		model.addAttribute("typeList", typeList);
		model.addAttribute("mediaList", mediaList);
		model.addAttribute("managerList", managerList);

		return "dealer/create";
	}

	/**
	 * @Title: save
	 * @Description: TODO(新增网点方法)
	 * @param model
	 *            参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@Permission(code="dealer/create")
	public String save(Model model,HttpServletRequest request, Dealer dealer, String mediaId) {
		HttpSession session = request.getSession();
		if(permissionService.hasAuth("dealer/create")){
			model.addAttribute("permission","permission");
		}
		if (dealer != null) {
			try {
				Long dealerId = dealerService.save(dealer);
				if (dealerId != null) {
					dealerService.saveDealerMedia(mediaId, dealerId);
					//写入日志
					LedpLogger.info((User) session.getAttribute("loginUser"), "增加网点", Operation.create,Result.success);
					return "redirect:/dealer/index";
				}
			} catch (LedpException e) {
				//写入日志
				LedpLogger.error((User) session.getAttribute("loginUser"), "增加网点", Operation.create,Result.failure);
				logger.error("异常信息：" + e.getMessage());
			}
		}
		LedpLogger.info((User) session.getAttribute("loginUser"), "增加网点", Operation.create,Result.failure);
		return "redirect:/dealer/create";
	}

	/**
	 * @Title: update
	 * @Description: TODO(修改网点入口)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            网点ID
	 * @return String
	 */
	@RequestMapping(value = "/update/{id}")
	@Permission(code="dealer/update")
	public String update(Model model, @PathVariable long id) {
		try {
			Dealer dealer = dealerService.getDealerInstance(id);

			List<DealerMedia> dealerMedias = dealerService.getDealerMedia(dealer.getId());
			List<Long> dealerMediaIds = new ArrayList<Long>();
			for (DealerMedia d : dealerMedias) {
				dealerMediaIds.add(d.getMedia().getId());
			}
			List<Media> mediaList = dealerService.getMediaList();

			List<User> managerList = dealerService.getUsers(dealer.getOrganization().getId());

			model.addAttribute("managerList", managerList);
			model.addAttribute("mediaList", mediaList);

			if (dealer != null) {
				model.addAttribute("dealer", dealer);
				model.addAttribute("organization", dealer.getOrganization());
				model.addAttribute("mediaList", mediaList);
				model.addAttribute("dealerMediaIds", dealerMediaIds);
				return "dealer/update";
			}
		} catch (LedpException e) {
            logger.error("异常信息：" + e.getMessage());
		}
		return "dealer/update";
	}

	/**
	 * @Title: edit
	 * @Description: TODO(修改网点方法)
	 * @param model
	 *            参数传递容器
	 * @return String
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@Permission(code="dealer/update")
	public String edit(Model model, Dealer dealer, Long dealerId, String mediaId,HttpServletRequest request) {
		HttpSession session = request.getSession();
		if(permissionService.hasAuth("dealer/create")){
			model.addAttribute("permission","permission");
		}
		try {
			Dealer srcDealer = dealerService.getDealerInstance(dealerId);
			srcDealer.setAddress(dealer.getAddress());
			srcDealer.setAlias(dealer.getAlias());
			srcDealer.setCode(dealer.getCode());
			srcDealer.setEmail(dealer.getEmail());
			srcDealer.setManager(dealer.getManager());
			srcDealer.setName(dealer.getName());
			srcDealer.setOrganization(dealer.getOrganization());
			srcDealer.setPhone(dealer.getPhone());
			srcDealer.setStatus(dealer.getStatus());
			srcDealer.setType(dealer.getType());
			dealerService.update(srcDealer);

			List<DealerMedia> dealerMedias = dealerService.getDealerMedia(srcDealer.getId());
			for (DealerMedia d : dealerMedias) {
				dealerService.delete(d.getId());
			}
			dealerService.saveDealerMedia(mediaId, dealerId);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改网点", Operation.update,Result.success);
			return "redirect:/dealer/index";
		} catch (LedpException e) {
            logger.error("异常信息：" + e.getMessage());
			LedpLogger.error((User) session.getAttribute("loginUser"), "修改网点", Operation.update,Result.failure);
		}
		LedpLogger.info((User) session.getAttribute("loginUser"), "修改网点", Operation.update,Result.failure);
		return "redirect:/dealer/update/" + dealerId;
	}

	@RequestMapping(value = "/active/{id}", method = RequestMethod.POST)
	@ResponseBody
	@Permission(code="dealer/update")
	public JSON active(@PathVariable long id) throws LedpException {

		Map<String, String> json = new HashMap<String, String>();
		Dealer dealer = dealerService.getDealerInstance(id);
		if (dealer == null) {
			throw new LedpException();
		}
		Constant status = constantService.find("record_status", "active");
		dealer.setStatus(status);
		dealerService.update(dealer);
		json.put("code", "success_message");
		json.put("message", "网点" + dealer.getStatus().getName() + "成功 ！");

		return (JSON) JSON.toJSON(json);

	}

	@RequestMapping(value = "/inactive/{id}", method = RequestMethod.POST)
	@ResponseBody
	@Permission(code="dealer/update")
	public JSON inactive(@PathVariable long id) throws LedpException {

		Map<String, String> json = new HashMap<String, String>();
		Dealer dealer = dealerService.getDealerInstance(id);
		if (dealer == null) {
			throw new LedpException();
		}
		Constant status = constantService.find("record_status", "inactive");
		dealer.setStatus(status);
		dealerService.update(dealer);
		json.put("code", "success_message");
		json.put("message", "网点" + dealer.getStatus().getName() + "成功 ！");

		return (JSON) JSON.toJSON(json);

	}

	@RequestMapping(value = "/manager/{id}", method = RequestMethod.POST)
	@ResponseBody
	public JSON manager(@PathVariable long id) {
		List<User> managers = new ArrayList<User>();
		try {
			managers = dealerService.getUsers(id);
		} catch (LedpException e) {
            logger.error("异常信息：" + e.getMessage());
		}
		return (JSON) JSON.toJSON(managers);
	}

	/**
	 * @Title: detail
	 * @Description: TODO(查看网点方法)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            网点ID
	 * @return String
	 */
	@RequestMapping(value = "/detail/{id}")
	@Permission(code="dealer/detail")
	public String detail(Model model, @PathVariable long id) {
		try {
			Dealer dealer = dealerService.getDealerInstance(id);
			List<DealerMedia> dmList = dealerService.getDealerMedia(id);
			if (dealer != null) {
				model.addAttribute("dealer", dealer);
				model.addAttribute("dmList", dmList);
				return "dealer/detail";
			}
		} catch (LedpException e) {
            logger.error("异常信息：" + e.getMessage());
		}
		return "dealer/index";
	}

	@RequestMapping(value = "/checkCode", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSON checkCode(HttpServletRequest request, String param) throws LedpException {

		String dealerId = request.getParameter("id");
		Long id = StringUtils.isNotBlank(dealerId) ? Long.parseLong(dealerId) : null;

		Map<String, String> checkResult = new HashMap<String, String>();
		checkResult.put("status", "n");
		checkResult.put("info", "网点编号已存在");

		List<Dealer> dealers = dealerService.checkCode(param, id);
		if (StringUtils.isNotBlank(param)) {
			if (dealers.isEmpty()) {
				checkResult.put("status", "y");
				checkResult.put("info", "");
			}
		}
		return (JSON) JSON.toJSON(checkResult);
	}

	/**
	 * 
	 * queryDealerList:(查询网店列表). <br/>
	 * 
	 * @author 刘学斌 Date: 2015-3-5 下午4:57:51
	 * @return
	 * @throws LedpException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/queryDealerList.json", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	@Permission(code="dealer/index")
	public JSON queryDealerList(HttpServletRequest request, Dealer dealer) throws LedpException {

		int pageSize = StringUtils.isBlank(request.getParameter("pageSize")) ? PAGE_SIZE : Integer.parseInt(request
				.getParameter("pageSize"));
		int pageNumber = StringUtils.isBlank(request.getParameter("pageNumber")) ? PAGE_NUMBER : Integer.parseInt(request
				.getParameter("pageNumber"));
		String sortName = StringUtils.isBlank(request.getParameter("sortName")) ? "" : request.getParameter("sortName");
		String sortOrder = StringUtils.isBlank(request.getParameter("sortOrder")) ? "" : request.getParameter("sortOrder");

		List<Dealer> result = dealerService.queryByConditionForPage(dealer, pageSize, pageNumber, sortName, sortOrder);

		int total = dealerService.getTotal(dealer);

		JSON data = JSONConverter.convert(pageSize, pageNumber, sortName, sortOrder, total, result, new String[] {
				"organization.name", "name", "code", "type.name", "status.name", "dateCreate" },
				new JSONConverter.Operation<Dealer>() {
					public String operate(Dealer t) {
						if (permissionService.hasAuth("dealer/detail")) {
							return "<a class='ielookDetails' href='../dealer/detail/" + t.getId()
									+ "' title='查看'><img alt='' src='../images/magnifier.png'></a>";
						}
						return "";
					}
				}, new JSONConverter.Operation<Dealer>() {
					public String operate(Dealer t) {
						/* 校验是否有修改权限 */
						if (permissionService.hasAuth("dealer/update")) {
							return "<a href='../dealer/update/" + t.getId()
									+ "' title='编辑' class='edit'><img alt='' src='../images/edit.png' /></a>";
						}
						return "";
					}
				}, new JSONConverter.Operation<Dealer>() {
					public String operate(Dealer t) {
						if (permissionService.hasAuth("dealer/update")) {
							if (t.getStatus().getCode().equals("active")) {
								return "<a class='ONOFF' href='#' title='禁用' onclick='delete_message(\"inactive/" + t.getId()
										+ "\",\"确定要禁用吗？\",false,onOffCallback)' ><img alt='' src='../images/jy.png' /></a>";
							} else {
								return "<a class='ONOFF' href='#' title='启用' onclick='delete_message(\"active/" + t.getId()
										+ "\",\"确定要启用吗？\",false,onOffCallback)' ><img alt='' src='../images/jynone.png' />";
							}
						}
						return "";
					}
				});
		return data;
	}
}
