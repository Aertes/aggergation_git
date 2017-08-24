package com.citroen.wechat.service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.util.LedpLogger;
import com.citroen.wechat.api.model.Group;
import com.citroen.wechat.api.service.ApiException;
import com.citroen.wechat.api.service.ApiFans;
import com.citroen.wechat.api.service.ApiGroup;
import com.citroen.wechat.api.token.TokenHolder;
import com.citroen.wechat.domain.*;
import com.citroen.wechat.util.EmojiUtil;
import com.citroen.wechat.util.ReplyXMLUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 杨少波
 * @ClassName: MessageService
 * @Description: TODO(的大卫杜夫)
 * @date 2015年6月16日 下午3:31:50
 */
@Service
public class ApiMessageService {
    private static Logger logger = Logger.getLogger(ApiMessageService.class);
    @Autowired
    private MybaitsGenericDao<Long> mybaitsGenericDao;
    @Autowired
    private MaterialService materialService;
    private Map<String, PublicNo> publicNos = new HashMap<String, PublicNo>();
    private Map<String, Constant> types = new HashMap<String, Constant>();

    //获取粉丝
    private Fans getFans(PublicNo publicNo, String openid) {
        //保存粉丝信息
        Fans fans = null;
        try {
            fans = mybaitsGenericDao.find(Fans.class, "select * from t_fans where publicno =" + publicNo.getId() + " and  openid = '" + openid + "'");
        } catch (LedpException e) {
            LedpLogger.error("保存粉丝发送信息", LedpLogger.Operation.create, LedpLogger.Result.failure, "数据库获粉丝失败，openid=" + openid);
        }

        String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
        com.citroen.wechat.api.model.Fans f = null;
        //调用接口获取粉丝
        try {
            f = ApiFans.getFansByOpenId(access_token, openid);
            if (f == null) {
                return fans;
            }
        } catch (ApiException e) {
            logger.error(e.getMessage());
            return fans;
        }

        //获取分组信息
        FansGroup fansGroup = null;
        if (StringUtils.isNotBlank(f.getGroupid())) {
            try {
                fansGroup = mybaitsGenericDao.find(FansGroup.class, " select * from t_fans_group where publicno=" + publicNo.getId() + " and wechatgroupid='" + f.getGroupid() + "'");
                if (fansGroup == null) {
                    List<Group> groups = ApiGroup.list(access_token);
                    for (Group group : groups) {
                        if (f.getGroupid().equals(group.getId())) {
                            fansGroup = new FansGroup();
                            fansGroup.setName(group.getName());
                            fansGroup.setPublicno(publicNo);
                            fansGroup.setStatus(1);
                            fansGroup.setWechatgroupid(group.getId());
                            fansGroup.setSort(0);
                            fansGroup.setDateUpdate(new Date());
                            break;
                        }
                    }
                    if (fansGroup != null) {
                        mybaitsGenericDao.save(fansGroup);
                    }
                }
            } catch (LedpException e) {
                logger.error(e.getMessage());
            } catch (ApiException e) {
                logger.error(e.getMessage());
                return fans;
            }
        }
        fans = (fans == null) ? new Fans() : fans;
        fans.setPublicNo(publicNo);
        fans.setOpenId(openid);

        /* 昵称需要过滤emoji表情 */
        fans.setNickName(EmojiUtil.filterEmoji(f.getNickname()));
        //fans.setNickName(f.getNickname());

        fans.setUnionid(f.getUnionid());
        fans.setPublicNo(publicNo);
        fans.setOpenId(f.getOpenid());
        fans.setSex(f.getSex());
        fans.setCity(f.getCity());
        fans.setCountry(f.getCountry());
        fans.setProvince(f.getProvince());
        fans.setLanguage(f.getLanguage());
        fans.setHeadImgUrl(f.getHeadimgurl());
        fans.setSubscribe(true);
        try {
            fans.setSubscribeTime(new Date(Long.parseLong(f.getSubscribe_time()) * 1000));
        } catch (Exception e) {
            fans.setSubscribeTime(new Date());
        }
        fans.setFansGroup(fansGroup == null ? fans.getFansGroup() : fansGroup);
        fans.setWechatgroupid(f.getGroupid());
        fans.setRemark("第一次保存粉丝");
        try {
            if (fans.getId() != null && fans.getId() > 0) {
                mybaitsGenericDao.update(fans);
            } else {
                Fans _fans = mybaitsGenericDao.find(Fans.class, "select * from t_fans where openid = '" + openid + "'");
                if (_fans == null) {
                    long id = mybaitsGenericDao.save(fans);
                    fans.setId(id);
                }
            }
        } catch (Exception e) {
            fans.setNickName(openid);
            try {
                if (fans.getId() != null && fans.getId() > 0) {
                    mybaitsGenericDao.update(fans);
                } else {
                    Fans _fans = mybaitsGenericDao.find(Fans.class, "select * from t_fans where openid = '" + openid + "'");
                    if (_fans == null) {
                        long id = mybaitsGenericDao.save(fans);
                        fans.setId(id);
                    }
                }
            } catch (LedpException e1) {
                logger.error(e1.getMessage());
            }
        }
        return fans;
    }

    //获取公众号
    private PublicNo getPublicNo(String appid) {
        if (!publicNos.containsKey(appid)) {
            try {
                PublicNo publicNo = mybaitsGenericDao.find(PublicNo.class, "select * from t_publicno where appid = '" + appid + "'");
                publicNos.put(appid, publicNo);
            } catch (LedpException e) {
                LedpLogger.error("保存粉丝发送信息", LedpLogger.Operation.create, LedpLogger.Result.failure, "数据库获取公众号实体失败，appid=" + appid);
            }
        }
        return publicNos.get(appid);
    }

    //获取消息类别
    private Constant getType(String type) {
        if (!types.containsKey(type)) {
            try {
                Constant t = mybaitsGenericDao.find(Constant.class, "select * from t_constant where category =110 and code = '" + type + "'");
                types.put(type, t);
            } catch (LedpException e) {
                LedpLogger.error("保存粉丝发送信息", LedpLogger.Operation.create, LedpLogger.Result.failure, "数据库获取消息类别失败，appid=" + type);
            }
        }
        return types.get(type);
    }

    //保存服务器推送来的消息
    public void save(com.citroen.wechat.api.model.Message message, int reply) {
        PublicNo publicNo = getPublicNo(message.getAppid());
        if (publicNo == null) {
            return;
        }
        if ("MASSSENDJOBFINISH".equals(message.getEvent())) {
            try {
                MessageSendBatch entity = mybaitsGenericDao.find(MessageSendBatch.class, "select * from t_messagesendbatch where publicno =" + publicNo.getId() + " and msg_id = '" + message.getMsgId() + "'");
                try {
                    entity.setTotalCount(Integer.parseInt(message.getTotalCount()));
                } catch (Exception e) {
                }
                try {
                    entity.setFilterCount(Integer.parseInt(message.getFilterCount()));
                } catch (Exception e) {
                }
                try {
                    entity.setSentCount(Integer.parseInt(message.getSentCount()));
                } catch (Exception e) {
                }
                try {
                    entity.setErrorCount(Integer.parseInt(message.getErrorCount()));
                } catch (Exception e) {
                }
                mybaitsGenericDao.update(entity);
            } catch (Exception e1) {
                logger.error(e1.getMessage());
            }
        } else if (!"event".equals(message.getMsgType())) {
            com.citroen.wechat.domain.Message m = new com.citroen.wechat.domain.Message();
            Fans fans = getFans(publicNo, message.getFromUserName());
            Constant type = getType(message.getMsgType());
            m.setPublicNo(publicNo);
            m.setFan(fans);
            m.setType(type);
            //m.setCanstant(reply);
            //标识为自动回复
            m.setIsAuto(reply);
            m.setComment(message.toString());
            m.setContent(message.getContent());
            m.setCreateTime(new Date());
            m.setCreateUser(new User(1l));
            m.setIsMass(false);
            m.setParent(null);
            m.setMid(message.getMediaId());
            m.setTransType(0);
            try {
                mybaitsGenericDao.save(m);
            } catch (LedpException e) {
                logger.error(e.getMessage());
                LedpLogger.error("保存粉丝发送信息", LedpLogger.Operation.create, LedpLogger.Result.failure, e.getMessage());
            }
        }
        log(message);
    }

    //获取反馈给粉丝的消息
    public String getReplyXML(com.citroen.wechat.api.model.Message message) throws Exception {
        PublicNo publicNo = mybaitsGenericDao.find(PublicNo.class, "select * from t_publicno where appid='" + message.getAppid() + "'");
        if (publicNo == null) {
            return "";
        }
        //订阅自动回复
        if ("subscribe".equals(message.getEvent())) {
            //关注时自动回复消息
            AutoReply reply = mybaitsGenericDao.find(AutoReply.class, "select * from t_autoreply where type='subscribe' and status=1 and publicno=" + publicNo.getId());
            if (reply != null) {
                String msgType = reply.getMsgType();
                if ("news".equals(msgType)) {
                    //回复图文消息
                    if (reply.getMaterialId() != null) {
                        Material material = materialService.get(reply.getMaterialId());
                        if(material == null || material.getType() == null){
                        	return "";
                        }
                        if ("news".equals(material.getType().getCode())) {
                            //回复图文消息
                            int i = 0;
                            int len = 1;
                            if (CollectionUtils.isNotEmpty(material.getChildren())) {
                                len = material.getChildren().size() + 1;
                            }
                            HashMap[] maps = new HashMap[len];
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("Title", material.getTitle());
                            params.put("Description", material.getDigest());
                            params.put("PicUrl", material.getUrl());
                            params.put("Url", material.getContentUrl());
                            maps[i++] = params;
                            if (CollectionUtils.isNotEmpty(material.getChildren())) {
                                for (Material child : material.getChildren()) {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("Title", child.getTitle());
                                    map.put("Description", child.getDigest());
                                    map.put("PicUrl", child.getUrl());
                                    map.put("Url", child.getContentUrl());
                                    maps[i++] = map;
                                }
                            }
                            return ReplyXMLUtil.wrapXml("news", message.getFromUserName(), message.getToUserName(), maps);
                        }
                        if ("picture".equals(material.getType().getCode())) {
                            //回复图片消息
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("MediaId", material.getMediaId());
                            return ReplyXMLUtil.wrapXml("image", message.getFromUserName(), message.getToUserName(), params);
                        }
                    }
                } else if ("text".equals(msgType)) {
                    //回复文本消息
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content", reply.getContent());
                    return ReplyXMLUtil.wrapXml("text", message.getFromUserName(), message.getToUserName(), params);
                }
            }
            return "";
        }

        //菜单事件消息自动回复
        if ("event".equals(message.getMsgType()) && StringUtils.isNotBlank(message.getEventKey())) {
            WechatMenu menu = mybaitsGenericDao.find(WechatMenu.class, "select * from t_wechat_menu where sn='" + message.getEventKey() + "' and publicno=" + publicNo.getId());
            if (menu != null && menu.getMaterial() != null) {
                Material material = materialService.get(menu.getMaterial().getId());
                if(material == null || material.getType() == null){
                	return "";
                }
                if ("news".equals(material.getType().getCode())) {
                    //回复图文消息
                    int i = 0;
                    int len = 1;
                    if (CollectionUtils.isNotEmpty(material.getChildren())) {
                        len = material.getChildren().size() + 1;
                    }
                    HashMap[] maps = new HashMap[len];
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("Title", material.getTitle());
                    params.put("Description", material.getDigest());
                    params.put("PicUrl", material.getUrl());
                    params.put("Url", material.getContentUrl());
                    maps[i++] = params;
                    if (CollectionUtils.isNotEmpty(material.getChildren())) {
                        for (Material child : material.getChildren()) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("Title", child.getTitle());
                            map.put("Description", child.getDigest());
                            map.put("PicUrl", child.getUrl());
                            map.put("Url", child.getContentUrl());
                            maps[i++] = map;
                        }
                    }
                    return ReplyXMLUtil.wrapXml("news", message.getFromUserName(), message.getToUserName(), maps);
                }
                if ("picture".equals(material.getType().getCode())) {
                    //回复图片消息
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("MediaId", material.getMediaId());
                    return ReplyXMLUtil.wrapXml("image", message.getFromUserName(), message.getToUserName(), params);
                }
                if ("text".equals(material.getType().getCode())) {
                    //回复文本消息
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content", material.getContent());
                    return ReplyXMLUtil.wrapXml("text", message.getFromUserName(), message.getToUserName(), params);
                }

            }
            return "";
        }

        //普通消息自动回复
        AutoReply reply = mybaitsGenericDao.find(AutoReply.class, "select * from t_autoreply where type='message' and status=1 and publicno=" + publicNo.getId());
        if (reply != null) {
            String msgType = reply.getMsgType();
            if ("news".equals(msgType)) {
                //回复图文消息
                if (reply.getMaterialId() != null) {
                    Material material = materialService.get(reply.getMaterialId());
                    if(material == null || material.getType() == null){
                    	return "";
                    }
                    if ("news".equals(material.getType().getCode()) && material != null) {
                        int i = 0;
                        int len = 1;
                        if (CollectionUtils.isNotEmpty(material.getChildren())) {
                            len = material.getChildren().size() + 1;
                        }
                        HashMap[] maps = new HashMap[len];
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("Title", material.getTitle());
                        params.put("Description", material.getDigest());
                        params.put("PicUrl", material.getUrl());
                        params.put("Url", material.getContentUrl());
                        maps[i++] = params;
                        if (CollectionUtils.isNotEmpty(material.getChildren())) {
                            for (Material child : material.getChildren()) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("Title", child.getTitle());
                                map.put("Description", child.getDigest());
                                map.put("PicUrl", child.getUrl());
                                map.put("Url", child.getContentUrl());
                                maps[i++] = map;
                            }
                        }
                        return ReplyXMLUtil.wrapXml("news", message.getFromUserName(), message.getToUserName(), maps);
                    }
                    if ("picture".equals(material.getType().getCode()) && material != null) {
                        //回复图片消息
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("MediaId", material.getMediaId());
                        return ReplyXMLUtil.wrapXml("image", message.getFromUserName(), message.getToUserName(), params);
                    }
                }
            } else if ("text".equals(msgType)) {
                //回复文本消息
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content", reply.getContent());
                return ReplyXMLUtil.wrapXml("text", message.getFromUserName(), message.getToUserName(), params);
            }
        }
        return "";
    }

    //取消或订阅粉丝
    public void subscribe(com.citroen.wechat.api.model.Message message) throws Exception {
        PublicNo publicNo = getPublicNo(message.getAppid());
        if ("subscribe".equals(message.getEvent())) {
            //订阅关注
            Fans fans = getFans(publicNo, message.getFromUserName());
            if (fans != null) {
                fans.setSubscribe(true);
                fans.setSubscribeTime(new Date());
                mybaitsGenericDao.update(fans);
            }
            int checkSubscribe = checkSubscribe(message.getFromUserName(), publicNo);
            if (checkSubscribe == 0 || checkSubscribe == -1) {
                mybaitsGenericDao.execute("INSERT INTO t_fans_log (openid, subscribe, subscribe_time, publicno) VALUES ('"
                        + message.getFromUserName() + "', 1, NOW()," + publicNo.getId() + ")");
            }
        } else if ("unsubscribe".equals(message.getEvent())) {
            //取消关注
            mybaitsGenericDao.execute("update t_fans set subscribe=0,desubscribe_time=now() where openid='" + message.getFromUserName() + "'");
            int checkSubscribe = checkSubscribe(message.getFromUserName(), publicNo);
            if (checkSubscribe == 1) {
                mybaitsGenericDao.execute("INSERT INTO t_fans_log (openid, subscribe, subscribe_time, publicno) VALUES ('"
                        + message.getFromUserName() + "', -1, NOW()," + publicNo.getId() + ")");
            }
        }
    }

    //记录接口日志
    private void log(com.citroen.wechat.api.model.Message message) {
        WechatRequestLog log = new WechatRequestLog();
        try {
            BeanUtils.copyProperties(log, message);
            log.setDateCreate(new Date());
            log.setDecryptJson(message.toString());
            try {
                long id = mybaitsGenericDao.save(log);
                log.setId(id);
            } catch (LedpException e) {
                logger.error(e.getMessage());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void unauthorized(String appid) {
        try {
            mybaitsGenericDao.execute("update t_publicno set authorized=0,unauthorized_time = now(),status=1020 where appid='" + appid + "'");
        } catch (LedpException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 检查t_fans_log中状态是否正常，因为微信推送可能存在数据延迟，如果不做校验，可能产生冗余数据
     * 例如：连续关注两次或者多次而没有取消几率，做报表的时候无法计算出
     *
     * @param openid
     * @return
     */
    public int checkSubscribe(String openid, PublicNo publicNo) {
        try {
            int isSub = 0;
            List<Map> fans = mybaitsGenericDao.executeQuery("SELECT SUM(subscribe) isSubscribe FROM t_fans_log WHERE openid = '" + openid + "' AND publicno = " + publicNo.getId());
            if (fans.get(0) != null) {
                isSub = Integer.parseInt(fans.get(0).get("isSubscribe").toString());
            }
            return isSub;
        } catch (LedpException e) {
            logger.error(e.getMessage());
        }
        return 0;
    }
}
