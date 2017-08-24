package com.citroen.ledp.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.service.UserService;
import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.wechat.util.MD5Util;
import org.jboss.logging.Logger;
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
import com.citroen.ledp.util.LedpLogger.Operation;
import com.citroen.ledp.util.LedpLogger.Result;
import com.citroen.ledp.util.ValidCodeUtil;
import com.citroen.wechat.api.util.DateUtil;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.service.PublicNoService;
import com.citroen.wechat.util.ConstantUtil;

/**
 * @author 廖启洪
 * @version V1.0
 * @Title: AuthController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(登录退出类)
 * @date 2015年1月29日 下午3:24:24
 */
@Controller
@RequestMapping("/auth")
public class AuthController {
    private String wechatDomain = PropertiyUtil.getWechatValue(ConstantUtil.WECHAT_DOMAIN);
    private String ledpDomain = PropertiyUtil.getWechatValue(ConstantUtil.LEDP_DOMAIN);
    public static final String VALID_CODE = ConstantUtil.VALID_CODE;
    private static Logger logger = Logger.getLogger(AuthController.class);
    @Autowired
    private AuthService authService;
    @Autowired
    private PublicNoService publicNoService;
    @Autowired
    private UserService userService;
    @Autowired
    private MybaitsGenericDao<Long> mybaitsGenericDao;

    @RequestMapping(value = "{id}")
    public String redirect(@PathVariable long id, HttpServletRequest request, HttpSession session) {
        //类似单点登录，校验context是否已经有存在user，有则同步到当前session
        User user = (User) session.getAttribute(ConstantUtil.LOGIN_USER);
        String url = request.getRequestURL().toString();
        String domain = "";
        if (url.contains(wechatDomain)) {
            domain = ledpDomain;
        }
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
                return "redirect:" + domain + "/home/index";
            }
            return "redirect:" + domain + "/auth/login";
        }
        return "redirect:" + domain + "/home/index";
    }

    @RequestMapping(value = "login")
    public String login(Model model, HttpServletRequest request) throws Exception {
        Object validCode = request.getSession().getAttribute(VALID_CODE);
        if (validCode == null) {
            return "login";
        }
       /* List<User> users = mybaitsGenericDao.executeQuery(User.class,"select * from t_user");
        for(User u : users) {
            String password = MD5Util.MD5(MD5Util.MD5(u.getPassword()+u.getUsername()));

            mybaitsGenericDao.executeQuery("update t_user set password='"+password+"' where id="+u.getId());
        }*/
        String username = ServletRequestUtils.getStringParameter(request, "username");
        String password = ServletRequestUtils.getStringParameter(request, "password");
        String checkCode = ServletRequestUtils.getStringParameter(request, "checkCode");

        if (StringUtils.isEmpty(username)) {
            model.addAttribute("message", "请输入用户名！");
            return "login";
        }
        if (StringUtils.isEmpty(password)) {
            model.addAttribute("message", "请输入密码！");
            return "login";
        }
        if (StringUtils.isEmpty(checkCode)) {
            model.addAttribute("message", "请输入验证码！");
            return "login";
        }
        if (!checkCode.equals(validCode.toString())) {
            model.addAttribute("message", "验证码输入错误！");
            return "login";
        }

        //防止sql注入
        if (username.indexOf("'") >= 0 || password.indexOf("'") >= 0 || username.indexOf("\"") >= 0 || password.indexOf("\"") >= 0 || password.indexOf(" ") >= 0 || password.indexOf(" ") >= 0) {
            model.addAttribute("message", "用户名或密码输入错误！");
            return "login";
        }
        /* 验证账号密码*/
        User user = authService.getUser(username);
        if (user == null) {
            model.addAttribute("message", "用户名或密码输入错误！");
            return "login";
        }
        int errCount = user.getErrCount();//已登录错误次数
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
            LedpLogger.info(user, "长效登录", Operation.login, Result.success, "系统自动解冻账号，username：" + username + ",ip:" + ip);
        }

        if (user.getStatus() != null && !"active".equals(user.getStatus().getCode())) {
            model.addAttribute("message", "账号已被禁止登录！");
            return "login";
        }

        /******************连续失败3次数冻结账号限制***********************/

        if (!MD5Util.MD5(MD5Util.MD5(password + username)).equalsIgnoreCase(user.getPassword())) {
            //密码连续输入三次，锁定账号
            if (++errCount > 2) {
                user = authService.lock(username);
                //写入日志
                String ip = getIpAddr(request);
                LedpLogger.info(user, "长效登录", Operation.login, Result.failure, "系统自动冻结账号，username：" + username + ",ip:" + ip);
            }else{
                user.setErrCount(errCount);
                //user.setStatus(new Constant(1010L));
                authService.updateErrCount(user);
            }
            model.addAttribute("message", "用户名或密码输入错误！");
            return "login";
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

        /*********************设置当前公众号*********************/
        Object pn = request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
        if (pn != null) {
            request.getSession().removeAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
        }
        PublicNo publicNo = publicNoService.getCurrentActive(user);
        request.getSession().setAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO, publicNo);
        /*********************设置当前公众号*********************/

        //类似单点登录，拦截器做登录校验，session过期自动销毁
        ServletContext context = session.getServletContext();
        context.setAttribute(user.getId().toString(), session);
        //写入日志
        String ip = getIpAddr(request);
        LedpLogger.info(user, "长效登录", Operation.login, Result.success, "登录成功，username：" + username + ",ip:" + ip);
        // 返回到界面
        return "redirect:/home/index";
    }

    @RequestMapping(value = "timeout")
    public String timeout(Model model, HttpSession session) throws Exception {
        model.addAttribute("message", "登录超时，请重新登录！");
        // 返回到界面
        return "login";
    }

    @RequestMapping(value = "logout")
    public String logout(Model model, HttpSession session) throws Exception {
        session.invalidate();
        // 返回到界面
        return "login";
    }

    @RequestMapping(value = "code")
    public void code(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ValidCodeUtil.generate(VALID_CODE, request, response);
        // 返回到界面
    }

    //获取客户端真实IP
    public String getIpAddr2(HttpServletRequest request) {
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

    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    logger.error("异常信息：" + e.getMessage());
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

}
