package dougou.rtmp.netty.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: DouGouServer2
 * @description RTMP消息载体（由chunk组成）
 * @author: zihan.wu
 * @create: 2020/12/24/024
 **/
@Data
@Slf4j
public  class RTMPMessage {

    protected Integer timestamp;

    protected Byte messageTypeId;

    protected Integer messageStreamId;

    protected Byte[] data;


}

