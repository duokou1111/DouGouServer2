package com.rtmp.core.model;

import lombok.Data;

/**
 * @program: DouGouServer2
 * @description 消息块
 * @author: zihan.wu
 * @create: 2020/12/23/023
 **/
@Data
public class RTMPChunk {
    private BasicHeader basicHeader;
    private byte[] chunkData;
    public static class BasicHeader{

    }
    public static class MessageHeader{

    }
    public static class ExtendedTimestamp{

    }
}
