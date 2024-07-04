package com.yiquwuyou.wx.handler;

import java.util.Map;

/**
 * 微信消息处理器
 * 此handler包下的代码是个工厂+策略模式的代码
 * 不懂的话看com.yiquwuyou.subject.domain.handler.subject处的代码，那里注释写的全
 */
public interface WxChatMsgHandler {

    /**
     * 获取消息类型
     */
    WxChatMsgTypeEnum getMsgType();

    /**
     * 处理消息
     */
    String dealMsg(Map<String, String> messageMap);

}
