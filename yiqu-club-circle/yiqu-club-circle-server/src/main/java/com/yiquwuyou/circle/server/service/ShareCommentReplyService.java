package com.yiquwuyou.circle.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiquwuyou.circle.api.req.RemoveShareCommentReq;
import com.yiquwuyou.circle.api.req.SaveShareCommentReplyReq;
import com.yiquwuyou.circle.server.entity.po.ShareCommentReply;

/**
 * <p>
 * 评论及回复信息 服务类
 * </p>
 *
 * @author ChickenWing
 * @since 2024/05/16
 */
public interface ShareCommentReplyService extends IService<ShareCommentReply> {

    Boolean saveComment(SaveShareCommentReplyReq req);

    Boolean removeComment(RemoveShareCommentReq req);

}
