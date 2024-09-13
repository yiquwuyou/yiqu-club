package com.yiquwuyou.circle.server.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yiquwuyou.circle.api.common.PageResult;
import com.yiquwuyou.circle.api.req.GetShareMessageReq;
import com.yiquwuyou.circle.api.vo.ShareMessageVO;
import com.yiquwuyou.circle.server.entity.po.ShareMessage;

/**
 * <p>
 * 消息表 服务类
 * </p>
 *
 * @author ChickenWing
 * @since 2024/05/18
 */
public interface ShareMessageService extends IService<ShareMessage> {

    PageResult<ShareMessageVO> getMessages(GetShareMessageReq req);

    void comment(String fromId, String toId, Long targetId);

    void reply(String fromId, String toId, Long targetId);

    Boolean unRead();

}
