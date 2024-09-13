package com.yiquwuyou.circle.server.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiquwuyou.circle.api.enums.IsDeletedFlagEnum;
import com.yiquwuyou.circle.api.req.RemoveShareCommentReq;
import com.yiquwuyou.circle.api.req.SaveShareCommentReplyReq;
import com.yiquwuyou.circle.server.dao.ShareCommentReplyMapper;
import com.yiquwuyou.circle.server.dao.ShareMomentMapper;
import com.yiquwuyou.circle.server.entity.po.ShareCommentReply;
import com.yiquwuyou.circle.server.entity.po.ShareMoment;
import com.yiquwuyou.circle.server.service.ShareCommentReplyService;
import com.yiquwuyou.circle.server.util.LoginUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 评论及回复信息 服务实现类
 * </p>
 *
 * @author ChickenWing
 * @since 2024/05/16
 */
@Service
public class ShareCommentReplyServiceImpl extends ServiceImpl<ShareCommentReplyMapper, ShareCommentReply> implements ShareCommentReplyService {

    @Resource
    private ShareMomentMapper shareMomentMapper;

    @Override
    public Boolean saveComment(SaveShareCommentReplyReq req) {
        ShareMoment moment = shareMomentMapper.selectById(req.getMomentId());
        ShareCommentReply comment = new ShareCommentReply();
        comment.setMomentId(req.getMomentId());
        comment.setReplyType(req.getReplyType());
        String loginId = LoginUtil.getLoginId();
        // 1评论 2回复
        if (req.getReplyType() == 1) {
            comment.setToId(req.getTargetId());
            comment.setToUser(loginId);
            comment.setToUserAuthor(Objects.nonNull(moment.getCreatedBy()) && loginId.equals(moment.getCreatedBy()) ? 1 : 0);
        } else {
            comment.setReplyId(req.getTargetId());
            comment.setReplyUser(loginId);
            comment.setReplayAuthor(Objects.nonNull(moment.getCreatedBy()) && loginId.equals(moment.getCreatedBy()) ? 1 : 0);
        }
        comment.setContent(req.getContent());
        if (!CollectionUtils.isEmpty(req.getPicUrlList())) {
            comment.setPicUrls(JSON.toJSONString(req.getPicUrlList()));
        }
        comment.setCreatedBy(LoginUtil.getLoginId());
        comment.setCreatedTime(new Date());
        comment.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        return super.save(comment);
    }

    @Override
    public Boolean removeComment(RemoveShareCommentReq req) {

        ShareCommentReply reply = new ShareCommentReply();
        reply.setId(reply.getId());
        reply.setIsDeleted(IsDeletedFlagEnum.DELETED.getCode());
        return super.updateById(reply);

    }

}
