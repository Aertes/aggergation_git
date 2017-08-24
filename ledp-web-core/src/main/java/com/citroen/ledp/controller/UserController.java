package com.citroen.ledp.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.service.*;
import com.citroen.wechat.service.*;
import com.citroen.wechat.util.ConstantUtil;
import com.citroen.wechat.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.Role;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.util.JSONConverter;
import com.citroen.ledp.util.LedpLogger;
import com.citroen.ledp.util.LedpLogger.Operation;
import com.citroen.ledp.util.LedpLogger.Result;

/**
 * @author 廖启洪
 * @version V1.0
 * @Title: UserController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(用户管理类)
 * @date 2015年1月25日 下午2:41:09
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private Map<String, Object> params;

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private DealerService dealerService;
    @Autowired
    private UserService userService;
    @Autowired
    private ConstantService constantService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MybaitsGenericDao<Long> mybaitsGenericDao;


    @RequestMapping(value = {"", "/index"})
    @com.citroen.ledp.interceptor.Permission(code = {"user/index"})
    public String index(Model model, String orgId) throws Exception {
        if (orgId == null) {
            Organization organization = organizationService.getRoot();
            orgId = organization.getId() + "";
        }
        if (permissionService.hasAuth("user/create")) {
            model.addAttribute("permission", "permission");
        }
        model.addAttribute("roles", getRoles());
        model.addAttribute("orgId", orgId);
        model.addAttribute(params);
        return "user/index";
    }

    @RequestMapping(value = {"search"})
    @ResponseBody
    @com.citroen.ledp.interceptor.Permission(code = {"user/index"})
    public JSON search(Model model, HttpServletRequest request) throws Exception {
        // 返回到界面
        String orgId = request.getParameter("orgId");
        if (StringUtils.isBlank(orgId)) {
            Organization organization = organizationService.getRoot();
            //params.put("orgId",organization.getId()+"");
        }
        final String contextPath = request.getContextPath();

        int pageSize = 10;
        int pageNumber = 1;
        String sortName = request.getParameter("sortName");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isBlank(sortName)) {
            sortName = "name";
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

        params.put("sortName", sortName);
        params.put("sortOrder", sortOrder);

        String nodeId = request.getParameter("usernodeId") == null ? 1 + "" : request.getParameter("usernodeId");
        String nodeType = request.getParameter("node_type") == null ? "headquarters" : request.getParameter("node_type");

        params.put("usernodeId", nodeId);
        params.put("nodeType", nodeType);

        List<User> rows = userService.executeQuery(params);
        int total = userService.getTotalRow(params);

        JSON data = JSONConverter.convert(pageSize, pageNumber, sortName, sortOrder, total, rows, new String[]{"org.name", "name", "code", "username", "phone", "email", "status.name"},
                new JSONConverter.Operation<User>() {
                    public String operate(User t) {
                        if (permissionService.hasAuth("user/detail")) {
                            return "<a class='ielookDetails' href='" + contextPath + "/user/detail/" + t.getId()
                                    + "' title='查看'><img alt='' src='../images/magnifier.png'></a>";
                        }
                        return "";
                    }
                }, new JSONConverter.Operation<User>() {
                    public String operate(User t) {
                        if (permissionService.hasAuth("user/update")) {
                            return "<a href='" + contextPath + "/user/update/" + t.getId()
                                    + "' title='编辑' class='edit'><img alt='' src='../images/edit.png' /></a>";
                        }
                        return "";
                    }
                },
                new JSONConverter.Operation<User>() {
                    public String operate(User t) {
                        if (permissionService.hasAuth("user/delete")) {
                            return "<a href='#' title='删除' d_id=" + t.getId()
                                    + "  onclick='delete_message(\"" + contextPath
                                    + "/user/delete/" + t.getId()
                                    + "\",\"确定要删除吗？\",false,deleteUserCallback)'><img alt='' src='../images/del.png' />";
                        }
                        return "";
                    }
                }, new JSONConverter.Operation<User>() {
                    public String operate(User t) {
                        if (permissionService.hasAuth("user/update")) {
                            Constant status = t.getStatus();
                            if ("active".equals(status.getCode())) {
                                return "<a href='#' title='禁用' d_id=" + t.getId()
                                        + " status_code='active' class='ONOFF' onclick='delete_message(\"" + contextPath
                                        + "/user/inactive/" + t.getId()
                                        + "\",\"确定要禁用吗？\",false,inactiveCallback)'><img alt='' src='../images/jy.png' />";
                            }
                            return "<a href='#' title='启用' d_id=" + t.getId()
                                    + " status_code='active' class='ONOFF' onclick='delete_message(\"" + contextPath + "/user/active/" + t.getId()
                                    + "\",\"确定要启用吗？\",false,activeCallback)'><img alt='' src='../images/jynone.png' />";
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
     * @Description: TODO(添加用户入口)
     */
    @RequestMapping(value = "/create")
    @com.citroen.ledp.interceptor.Permission(code = {"user/create"})
    public String create(Model model, @ModelAttribute("user") User user, HttpServletRequest request) throws Exception {

        if (user.getOrg() != null && user.getOrg().getId() > 0) {
            if (request.getParameter("nodeType") != null) {
                if (request.getParameter("nodeType").equals("headquarters") || request.getParameter("nodeType").equals("largeArea")) {
                    Organization org = organizationService.get(user.getOrg().getId());
                    user.setOrg(org);
                    model.addAttribute("createType", "org");
                }
                if (request.getParameter("nodeType").equals("dealer")) {
                    Dealer dealer = dealerService.get(user.getOrg().getId()).get(0);
                    user.setDealer(dealer);
                    model.addAttribute("createType", "dealer");
                }
            }
        }
        Constant status = constantService.find("record_status", "active");
        user.setStatus(status);
        model.addAttribute("user", user);
        model.addAttribute("roles", getRoles());
        return "user/create";
    }

    /**
     * @param model 参数传递容器
     * @return String
     * @throws Exception
     * @Title: save
     * @Description: TODO(新增用户方法)
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @com.citroen.ledp.interceptor.Permission(code = {"user/create"})
    public String save(Model model, @Valid @ModelAttribute("user") User user, BindingResult br, HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        if (user.getOrg() != null) {
            model.addAttribute("createType", "org");
        }
        if (user.getDealer() != null) {
            model.addAttribute("createType", "dealer");
        }

        if (br.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("roles", getRoles());
            LedpLogger.info((User) session.getAttribute("loginUser"), "增加用户", Operation.create, Result.failure);
            return "user/create";
        }

        if (user.getOrg() == null && user.getDealer() == null) {
            br.rejectValue("name", "user.org.not.null", "请选择机构或者网点！");
            model.addAttribute("user", user);
            model.addAttribute("roles", getRoles());
            LedpLogger.info((User) session.getAttribute("loginUser"), "增加用户", Operation.create, Result.failure, "没有机构或者网点");
            return "user/create";
        }
        if (!validatePassword(user.getPassword())) {
            br.rejectValue("password", "user.password", "密码必须是8至16位大小写字母和数字组成！");
            model.addAttribute("user", user);
            model.addAttribute("roles", getRoles());
            return "user/create";
        }

        boolean isExits = userService.findCode(user.getCode());
        if (isExits) {
            br.rejectValue("code", "user.code.already.exists", "用户编码已经存在，请重新输入！");
            model.addAttribute("user", user);
            model.addAttribute("roles", getRoles());
            LedpLogger.info((User) session.getAttribute("loginUser"), "增加用户", Operation.create, Result.failure, "用户编码已经存在");
            return "user/create";
        }

        user.setName(user.getName().trim());
        user.setUsername(user.getUsername().trim());
        user.setPassword(user.getPassword().trim());
        User user1 = null;
        if (user.getOrg() != null) {
            user1 = userService.finds(user.getOrg().getId(), null, user.getName());
        } else if (user.getDealer() != null) {
            user1 = userService.finds(null, user.getDealer().getId(), user.getName());
        }
        user1 = userService.findByUsername(user.getUsername());
        if (user1 != null) {
            br.rejectValue("username", "user.username.already.exists", "登录账号已经存在，请重新输入！");
            model.addAttribute("user", user);
            model.addAttribute("roles", getRoles());
            LedpLogger.info((User) session.getAttribute("loginUser"), "增加用户", Operation.create, Result.failure, "登录账号已经存在");
            return "user/create";
        }
        if (permissionService.hasAuth("user/create")) {
            model.addAttribute("permission", "permission");
        }

        userService.save(user);
        model.addAttribute("user", "用户添加成功");
        LedpLogger.info((User) session.getAttribute("loginUser"), "增加用户", Operation.create, Result.success);
        return "user/index";
    }


    /**
     * 删除用户
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    @ResponseBody
    public JSON deleteUser(Model model, String userId) throws LedpException {
        Map json = new HashMap<String, String>();

        try {
            User user = userService.get(Long.parseLong(userId));
            Constant constant = constantService.find("record_status", "delete");
            user.setStatus(constant);
            userService.update(user);
            json.put("code", "success_message");
            json.put("message", "用户删除成功！");
        } catch (LedpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            json.put("code", "fail_message");
            json.put("message", "用户删除失败！");
        }

        return (JSON) JSON.toJSON(json);
    }

    /**
     * @param model 参数传递容器
     * @param id    用户ID
     * @return String
     * @throws LedpException
     * @Title: update
     * @Description: TODO(修改用户入口)
     */
    @RequestMapping(value = "/update/{id}")
    @com.citroen.ledp.interceptor.Permission(code = {"user/update"})
    public String update(Model model, @PathVariable long id) throws LedpException {
        User user = userService.get(id);
        //传给前端一个假密码，如何提交时没有修改则不更新原来的密码，修改了才更新
        user.setPassword(ConstantUtil.FALSE_PASSWORD);
        model.addAttribute("user", user);
        if (user.getOrg() != null) {
            model.addAttribute("createType", "org");
        }
        if (user.getDealer() != null) {
            model.addAttribute("createType", "dealer");
        }
        model.addAttribute("roles", getRoles());
        return "user/update";
    }

    /**
     * @param model 参数传递容器
     * @return String
     * @throws LedpException
     * @Title: edit
     * @Description: TODO(修改用户方法)
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @com.citroen.ledp.interceptor.Permission(code = {"user/update"})
    public String edit(Model model, BindingResult br, HttpServletRequest request) throws LedpException {
        HttpSession session = request.getSession();
        Organization organization = organizationService.getRoot();
        model.addAttribute("orgId", organization.getId());
        List<User> users = mybaitsGenericDao.executeQuery(User.class,"select * from t_user");
        for(User u : users) {
            String password = MD5Util.MD5(MD5Util.MD5(u.getPassword()+u.getUsername()));
            mybaitsGenericDao.executeQuery("update t_user set password='"+password+"' where id="+u.getId());
        }

        model.addAttribute("user", "用户更新成功");
        LedpLogger.info((User) session.getAttribute("loginUser"), "修改用户", Operation.update, Result.success);
        return "user/index";
    }

    /**
     * @param model 参数传递容器
     * @param id    用户ID
     * @return String
     * @throws LedpException
     * @Title: detail
     * @Description: TODO(查看用户方法)
     */
    @RequestMapping(value = "/detail/{id}")
    @com.citroen.ledp.interceptor.Permission(code = {"user/detail"})
    public String detail(Model model, @PathVariable long id) throws LedpException {
        User user = userService.get(id);
        model.addAttribute("user", user);
        return "user/detail";
    }

    /**
     * @param model 参数传递容器
     * @param id    用户ID
     * @return String
     * @throws LedpException
     * @Title: detail
     * @Description: TODO(查看用户方法)
     */
    @RequestMapping(value = "/active/{id}")
    @ResponseBody
    @com.citroen.ledp.interceptor.Permission(code = {"user/update"})
    public JSON active(Model model, @PathVariable long id) throws LedpException {
        User user = userService.get(id);
        if (user == null) {
            throw new LedpException();
        }
        Constant status = constantService.find("record_status", "active");
        user.setStatus(status);
        user.setErrCount(0);
        user.setUnlockDate(null);
        userService.update(user);
        Map json = new HashMap<String, String>();
        json.put("code", "success_message");
        json.put("message", "用户" + status.getName() + "成功 ！");

        return (JSON) JSON.toJSON(json);
    }

    /**
     * @param model 参数传递容器
     * @param id    用户ID
     * @return String
     * @throws LedpException
     * @Title: detail
     * @Description: TODO(查看用户方法)
     */
    @RequestMapping(value = "/inactive/{id}")
    @ResponseBody
    @com.citroen.ledp.interceptor.Permission(code = {"user/update"})
    public JSON inactive(Model model, @PathVariable long id) throws LedpException {
        User user = userService.get(id);
        if (user == null) {
            throw new LedpException();
        }
        Constant status = constantService.find("record_status", "inactive");
        user.setStatus(status);
        userService.update(user);
        Map json = new HashMap<String, String>();
        json.put("code", "success_message");
        json.put("message", "用户" + status.getName() + "成功 ！");

        return (JSON) JSON.toJSON(json);
    }

    @RequestMapping(value = "/delete/{id}")
    @ResponseBody
    @com.citroen.ledp.interceptor.Permission(code = {"user/delete"})
    public JSON delete(Model model, @PathVariable long id) throws LedpException {
        User user = userService.get(id);
        if (user == null) {
            throw new LedpException();
        }
        Constant status = constantService.find("record_status", "delete");
        user.setStatus(status);
        userService.update(user);
        Map json = new HashMap<String, String>();
        json.put("code", "success_message");
        json.put("message", "用户" + status.getName() + "成功 ！");

        return (JSON) JSON.toJSON(json);
    }

    public List<Role> getRoles() throws LedpException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sortName", "name");
        params.put("sortOrder", "asc");
        Constant active = constantService.find("record_status", "active");
        params.put("statusId", active.getId() + "");
        List<Role> roles = roleService.executeQuery(params);
        return roles;
    }

    /**
     * 用户资料设置
     *
     * @return
     * @throws LedpException
     */
    @RequestMapping(value = "/setting")
    public String setting(Model model, HttpServletRequest request) throws LedpException {
        User sessionUser = (User) request.getSession().getAttribute("loginUser");
        User user = userService.get(sessionUser.getId());
        user.setPassword(null);
        model.addAttribute("user", user);
        return "user/setting";
    }

    /**
     * 设置用户资料
     *
     * @return
     * @throws LedpException
     * @throws IOException
     * @throws ServletException
     */
    @RequestMapping(value = "/doSetting")
    @ResponseBody
    public JSON doSetting(String name, String oldPassword, String newPassowrd, String rePassword, HttpServletRequest request) throws LedpException, ServletException, IOException {
        if (StringUtils.isBlank(name)) {
            Map<String, String> result = new HashMap<String, String>();
            result.put("code", "error_message");
            result.put("message", "用户名不能为空！");
            return (JSON) JSON.toJSON(result);
        }

        User oldUser = (User) request.getSession().getAttribute("loginUser");
        oldUser = userService.get(oldUser.getId());
        if (!oldUser.getPassword().equalsIgnoreCase(MD5Util.MD5(MD5Util.MD5(oldPassword + oldUser.getUsername())))) {
            Map<String, String> result = new HashMap<String, String>();
            result.put("code", "error_message");
            result.put("message", "旧密码输入错误！");
            return (JSON) JSON.toJSON(result);
        }
        if (StringUtils.isBlank(newPassowrd)) {
            Map<String, String> result = new HashMap<String, String>();
            result.put("code", "error_message");
            result.put("message", "新密码不能为空！");
            return (JSON) JSON.toJSON(result);
        }

        if (!validatePassword(newPassowrd)) {
            Map<String, String> result = new HashMap<String, String>();
            result.put("code", "error_message");
            result.put("message", "<label style='font-size:5px;'>新密码必须是8至16位大小写字母和数字组成！</label>");
            return (JSON) JSON.toJSON(result);
        }

        if (!newPassowrd.equals(rePassword)) {
            Map<String, String> result = new HashMap<String, String>();
            result.put("code", "error_message");
            result.put("message", "确认新密码必须和新密码输入一致！");
            return (JSON) JSON.toJSON(result);
        }

        oldUser.setName(name);
        oldUser.setPassword(rePassword);
        userService.update(oldUser);
        Map<String, String> result = new HashMap<String, String>();
        result.put("code", "success_message");
        result.put("message", "信息修改成功!");
        return (JSON) JSON.toJSON(result);
    }

    //验证密码是否为8至16位大小写字母和数字组成，正确返回true，错误返回false
    private boolean validatePassword(String passowrd) {
        if (!(passowrd.matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,20}$"))) {
            return false;
        }
        Pattern p1 = Pattern.compile("[a-z]+");
        Pattern p2 = Pattern.compile("[A-Z]+");
        Pattern p3 = Pattern.compile("[0-9]+");
        if (!(p1.matcher(passowrd).find() && p2.matcher(passowrd).find() && p3.matcher(passowrd).find())) {
            return false;
        }
        return true;
    }
}
