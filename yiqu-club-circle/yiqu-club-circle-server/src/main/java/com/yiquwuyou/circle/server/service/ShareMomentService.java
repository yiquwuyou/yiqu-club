package com.yiquwuyou.circle.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiquwuyou.circle.api.common.PageResult;
import com.yiquwuyou.circle.api.req.GetShareMomentReq;
import com.yiquwuyou.circle.api.req.RemoveShareMomentReq;
import com.yiquwuyou.circle.api.req.SaveMomentCircleReq;
import com.yiquwuyou.circle.api.vo.ShareMomentVO;
import com.yiquwuyou.circle.server.entity.po.ShareMoment;

/**
 * <p>
 * 动态信息 服务类
 * </p>
 *
 * @author ChickenWing
 * @since 2024/05/16
 */
public interface ShareMomentService extends IService<ShareMoment> {

    Boolean saveMoment(SaveMomentCircleReq req);

    PageResult<ShareMomentVO> getMoments(GetShareMomentReq req);

    Boolean removeMoment(RemoveShareMomentReq req);

}
