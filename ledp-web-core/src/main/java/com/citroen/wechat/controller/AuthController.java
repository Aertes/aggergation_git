package com.citroen.wechat.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.citroen.wechat.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.citroen.ledp.domain.Menu;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.service.AuthService;
import com.citroen.ledp.util.LedpLogger;
import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.ledp.util.LedpLogger.Operation;
import com.citroen.ledp.util.LedpLogger.Result;
import com.citroen.ledp.util.ValidCodeUtil;
import com.citroen.wechat.api.util.DateUtil;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.service.FansService;
import com.citroen.wechat.service.PublicNoService;
import com.citroen.wechat.util.ConstantUtil;

/**
 * 矩阵平台
 *
 * @author 何海粟
 * @date2015年6月2日
 */
@Controller("wechatAuthController")
@RequestMapping("/wechat/auth")
public class AuthController {
    private final String VALID_CODE = "login.valid.code";
    private Map<String, Object> params;
    @Autowired
    private AuthService authService;
    @Autowired
    private PublicNoService publicNoService;
    @Autowired
    private FansService fansService;


    @RequestMapping(value = "{id}")
    public String redirect(@PathVariable long id, HttpServletRequest request, HttpSession session) {
        //类似单点登录，校验context是否已经有存在user，有则同步到当前session
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            ServletContext context = session.getServletContext();
            HttpSession session2 = (HttpSession) context.getAttribute(String.valueOf(id));
            if (session2 != null) {
                //拷贝session
                Enumeration<String> enumeration = session2.getAttributeNames();
                while (enumeration.hasMoreElements()) {
                    String attrName = enumeration.nextElement();
                    session.setAttribute(attrName, session2.getAttribute(attrName));
                }
                return "redirect:/wechat/home/index";
            }
            return "redirect:/wechat/auth/login";
        }
        return "redirect:/wechat/home/index";
    }


    @RequestMapping(value = "login")
    public String login(Model model, HttpServletRequest request) throws Exception {
        Object validCode = request.getSession().getAttribute(VALID_CODE);
        if (validCode == null) {
            return "wechat/login";
        }

        String username = ServletRequestUtils.getStringParameter(request, "username");
        String password = ServletRequestUtils.getStringParameter(request, "password");
        String checkCode = ServletRequestUtils.getStringParameter(request, "checkCode");

        if (StringUtils.isEmpty(username)) {
            model.addAttribute("message", "请输入用户名！");
            return "wechat/login";
        }
        if (StringUtils.isEmpty(password)) {
            model.addAttribute("message", "请输入密码！");
            return "wechat/login";
        }
        if (StringUtils.isEmpty(checkCode)) {
            model.addAttribute("message", "请输入验证码！");
            return "wechat/login";
        }
        if (!checkCode.equals(validCode.toString())) {
            model.addAttribute("message", "验证码输入错误！");
            return "wechat/login";
        }

        //防止sql注入
        if (username.indexOf("'") >= 0 || password.indexOf("'") >= 0 || username.indexOf("\"") >= 0 || password.indexOf("\"") >= 0 || password.indexOf(" ") >= 0 || password.indexOf(" ") >= 0) {
            model.addAttribute("message", "用户名或密码输入错误！");
            return "wechat/login";
        }
        /* 验证账号密码*/
        User user = authService.getUser(username);
        int errCount = user.getErrCount();//已登录错误次数
        if (user == null) {
            model.addAttribute("message", "用户名或密码输入错误！");
            return "wechat/login";
        }

        /******************连续失败3次数冻结账号限制***********************/
        if (user.getStatus() != null && ConstantUtil.USER_STATUS_LOCK.equals(user.getStatus().getCode())) {
            Date currentDate = new Date();//当前时间
            Date unlockDate = DateUtil.addDay(user.getDateUpdate(), 1);//解冻时间
            if (currentDate.getTime() < unlockDate.getTime()) {
                SimpleDateFormat sdf = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
                sdf.applyPattern("MM月dd日HH:mm:ss");
                String timeStr = sdf.format(unlockDate);
                model.addAttribute("lockTime", timeStr);
                user.setUnlockDate(unlockDate);
                authService.updateUnlockDate(user);
                return "login";
            }
            //时间超过解冻时间自动解冻
            authService.unlock(username);
            //写入日志
            String ip = getIpAddr(request);
            LedpLogger.info(user, "矩阵登录", Operation.login, Result.success, "系统自动解冻账号，username：" + username + ",ip:" + ip);
        }

        if (!MD5Util.MD5(MD5Util.MD5(password + username)).equalsIgnoreCase(user.getPassword())) {
            //密码连续输入三次，锁定账号
            if (++errCount > 2) {
                user = authService.lock(username);
                //写入日志
                String ip = getIpAddr(request);
                LedpLogger.info(user, "矩阵登录", Operation.login, Result.failure, "系统自动冻结账号，username：" + username + ",ip:" + ip);
            } else {
                user.setErrCount(errCount);
                //user.setStatus(new Constant(1010L));
                authService.updateErrCount(user);
            }
            model.addAttribute("message", "用户名或密码输入错误！");
            return "wechat/login";
        }

        /******************连续失败3次数冻结账号限制***********************/

        if (!authService.isActive(user)) {
            model.addAttribute("message", "账号已被禁止登录！");
            return "wechat/login";
        }

        user.setErrCount(0);
        authService.updateErrCount(user);


        List<Menu> menus = authService.getMenuChildren(null);
        List<String> permissions = authService.getPermissions(user);

        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user);
        session.setAttribute("loginRole", user.getRole());
        session.setAttribute("loginOrg", user.getOrg());
        session.setAttribute("loginDealer", user.getDealer());

        session.setAttribute("menus", menus);
        session.setAttribute("permissions", permissions);

        //类似单点登录，拦截器做登录校验，session过期自动销毁
        ServletContext context = session.getServletContext();
        context.setAttribute(user.getId().toString(), session);

        /*********************设置当前公众号*********************/
        Object pn = request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
        if (pn != null) {
            request.getSession().removeAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
        }
        PublicNo publicNo = publicNoService.getCurrentActive(user);
        request.getSession().setAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO, publicNo);
        /*********************设置当前公众号*********************/
        /*********************设置当前公众号*********************/

        //写入日志
        String ip = getIpAddr(request);
        LedpLogger.info(user, "矩阵登录", Operation.login, Result.success, "登录成功，username：" + username + ",ip:" + ip);

        // 返回到界面
        return "redirect:/wechat/home/index";
    }

    @RequestMapping(value = "logout")
    public String logout(Model model, HttpSession session) throws Exception {
        session.invalidate();
        // 返回到界面
        return "redirect:" + PropertiyUtil.getWechatValue("logoutUrl");
    }

    @RequestMapping(value = "code")
    public void code(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ValidCodeUtil.generate(VALID_CODE, request, response);
        // 返回到界面
    }
    //获取客户端真实IP

    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
