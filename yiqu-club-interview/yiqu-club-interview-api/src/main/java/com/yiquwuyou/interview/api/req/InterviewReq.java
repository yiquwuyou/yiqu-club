package com.yiquwuyou.interview.api.req;

import com.yiquwuyou.interview.api.enums.EngineEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class InterviewReq implements Serializable {

    private String url;

    private String engine = EngineEnum.JI_CHI.name();

}