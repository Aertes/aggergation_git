package com.citroen.wechat.dto;

import com.citroen.wechat.domain.AutoReply;

import java.util.List;

/**
 * Created by vpc on 2015/6/23.
 */
public class AutoReplyDto {
    private List<AutoReply> autoReplies;

    public List<AutoReply> getAutoReplies() {
        return autoReplies;
    }

    public void setAutoReplies(List<AutoReply> autoReplies) {
        this.autoReplies = autoReplies;
    }
}
