package dougou.rtmp.netty.model;

/**
 * @program: DouGouServer2
 * @description
 * @author: zihan.wu
 * @create: 2020/12/23/023
 **/
public enum RTMPDecodeState {
    //消息头未读完
    DECODE_HEADER,
    //负载未读完
    DECODE_PAYLOAD;
}
