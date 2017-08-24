package com.citroen.ledp.interceptor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.citroen.ledp.binder.RequestUtil;
import com.citroen.ledp.domain.Menu;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.SecurityException;
import com.citroen.ledp.service.CacheService;
import com.citroen.ledp.util.CacheManager;
import com.citroen.ledp.util.PropertiyUtil;

public class PermissionInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private CacheService cacheService;
	private String wechatDomain = PropertiyUtil.getWechatValue("wechat.domain");
	private String logoutUrl = PropertiyUtil.getWechatValue("logoutUrl");
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		//排除非法微信矩阵路径
		String url = request.getRequestURL().toString();
/*        if(url.contains(wechatDomain)){
        	if(!(url.contains("/wechat")||url.contains("msgretrvauth")||url.contains("msgcallback")||url.contains("trial"))){
				request.getRequestDispatcher("/trial").forward(request, response);
				return false;
        	}
        }*/
		
		Class<?> clazz = handler.getClass();
		HttpSession session = request.getSession();
		
		/* 记住被选中的菜单 */
		String action = getAction(request);
		Menu menu = cacheService.getMenuByAction(action);
		if ("home/index".equals(action)) {
			session.setAttribute("choose", null);
			session.setAttribute("menu", null);
		} else if ("wechat/home/index".equals(action)) {
			session.setAttribute("choose", null);
			session.setAttribute("menu", null);
		} else if ("wechat/publicno/index".equals(action)) {
			session.setAttribute("choose", 121);
			session.setAttribute("menu", null);
		}else if (menu != null) {
			session.setAttribute("choose", menu.getChoose());
			session.setAttribute("menu", menu);
		}
		
		if (clazz.isAssignableFrom(HandlerMethod.class)) {
			HandlerMethod HandlerMethod = (HandlerMethod) handler;
			Object controller = HandlerMethod.getBean();
			// 方法名
			String methodName = HandlerMethod.getMethod().getName();
			// 类名
			String className = controller.getClass().getSimpleName();
			// 缓存权限CODE
			Map<String, String[]> permissions = CacheManager.hasController(
					controller, className, methodName);

			/* 绑定参数 */
			try {
				Field field = controller.getClass().getDeclaredField("params");
				if (field != null) {
					field.setAccessible(true);
					Map<String, Object> params = RequestUtil.getParameterMap(
							request, field.getType());
					field.set(controller, params);
				}
			} catch (Exception e) {
			}
			/* 权限校验 */
			// 权限CODE集合
			String[] codes = permissions.get(className + "_" + methodName);
			// 用户权限集合
			List<String> actions = (List<String>) session.getAttribute("permissions");
			// 验证用户状态
			
			if (action.contains("jsapi")) {
				//JS接口不拦截
				return true;
			}
			
			if(url.contains("/campaign/getUrl")){
	    	    return true;  
			}
			
			User user = (User) session.getAttribute("loginUser");
			if (null == user && action.indexOf("auth") < 0) {
				if(action.contains("wechat/services")){
					//外部接口不拦截
					return true;
				}else{
					response.sendRedirect(logoutUrl);
				}
				return false;
			}else{
				if (!CacheManager.hasController(className, methodName) || action.indexOf("auth") > -1) {
					return true;
				} else {
						for (String code : actions) {
							for (int i = 0; i < codes.length; i++) {
								String cod = codes[i];
								if (cod.equals(code)) {
									return true;
								}
							}
							
						}
						throw new SecurityException();
					}
			}
			

		}
		return true;
	}

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}

	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		super.afterCompletion(request, response, handler, ex);
	}

	private String getAction(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		String action=request.getRequestURI();
		if(!action.equals("")){
			action=action.charAt(0)=='/'?action.substring(1):action;
		}
		return action;
	}

	private String getMethodName(String action) {
		if (action != null) {
			String[] names = action.split("/");
			String name = names[names.length - 1];
			if ("".equals(name)) {
				return "index";
			}
		}
		return "";
	}

}
