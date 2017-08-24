package com.citroen.ledp.util;

/**
 * 系统常量类
 * @author      miaoshuai
 * @email       miaoshuai@tocersfot.com
 * @company		www.tocersoft.com
 * @create-time 2015年3月14日 下午12:36:05
 * @version     1.0
 */
public class SysConstant {
	
	/** 权限：留资查看详情 */
	public static final String PERMISSION_LEADS_DETAIL = "leads/detail";
	/** 权限：客户查看详情 */
	public static final String PERMISSION_CUSTOMER_DETAIL = "customer/detail";
	/** 权限：修改客户 */
	public static final String PERMISSION_CUSTOMER_UPDATE = "customer/update";
	/** 权限：查看合并留资 */
	public static final String PERMISSION_LEADSMERGED_DETAIL = "leadsMerged/detail";
	/** 权限：回流查看详情 */
	public static final String PERMISSION_CRM_DETAIL = "crm/leads/detail";

	/** 线索类型:电话类型 */
	public static final Long LEADS_TYPE_PHONE = 3030L;
	
	/** 常量类型:意向级别 */
	public static final Long CONSTANT_CATEGORY_LEADS_INTENT = 20L;
	/** 常量类型:跟进状态 */
	public static final Long CONSTANT_CATEGORY_LESDS_STATE = 40L;
}
