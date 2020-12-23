package dougou.rtmp.netty.model;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * @program: DouGouServer2
 * @description 每一个RTMP Chunk大多由四部分组成，
 * 分为:Basic Header(基本头)
 *     Message Header(消息头)
 *     ExtendedTimestamp(扩展时间戳，大多时候没有这个)
 *     ChunkData(负载的数据)
 * @author: zihan.wu
 * @create: 2020/12/23/023
 **/
@Data
public class RTMPChunk {
    private BasicHeader basicHeader;
    private MessageHeader messageHeader;
    private Byte[] extendTimestamp;
    private Byte[] chunkData;
    @Data
    public static class BasicHeader{
        //块类型，占两位，在BasicHeader里这个字段没啥用，他主要控制messageHeader的格式
        private Byte fmt;
        //chunk的ID AKA cs id
        private Integer chunkStreamId;

    }
    @Data
    public static class MessageHeader{
        //3字节的timestamp，如果timestamp等于0xFFFFFF，则会将数值填入extendTimestamp
        private Integer timestamp;
        //3字节，
        private Integer messageLength;
        //1字节
        private Byte messageTypeId;
        //4字节
        private Integer messageStreamId;
    }
    public void generateHeader(ByteBuf in){
        this.basicHeader = new BasicHeader();
        Byte firstByte = in.readByte();
        //获取第一个字节前两位的ChunkType(AKA fmt)
        basicHeader.setFmt((byte) ((firstByte & 0xff) >> 6));
        //获取第2到6位的bit
        int isreserved = firstByte & 0x3f;
        if(isreserved == 0){
            /*查看2到6位的bit，如果为0:
                +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
                |fmt| 0         | cs id - 64 |
                +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
            */
            //chunkStreamId = 第二字节+64
            basicHeader.setChunkStreamId((in.readByte() & 0xff) + 64);
        }
        if (isreserved == 1){
            /*  如果为1则ChunkStreamId占两个字节
                 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
                 |fmt|   1    |            cs id - 64            |
                 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
                 chunkStreamId = 第二字节+第三字节+64
            */
            byte secondByte = in.readByte();
            byte thirdByte = in.readByte();
            basicHeader.setChunkStreamId((thirdByte & 0xff) << 8 + (secondByte & 0xff) + 64);
        }
        if (isreserved >= 2){
            /*如果大于2则chunkStreamId就是2-8位的数字
            +-+-+-+-+-+-+-+-+
            |fmt|   cs id   |
            +-+-+-+-+-+-+-+-+
            */
            basicHeader.setChunkStreamId(isreserved);
        }
    }
}
