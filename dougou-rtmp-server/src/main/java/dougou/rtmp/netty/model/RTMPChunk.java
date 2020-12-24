package dougou.rtmp.netty.model;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RTMPChunk {
    protected BasicHeader basicHeader;
    protected MessageHeader messageHeader;
    protected Integer extendTimestamp;
    protected byte[] chunkData;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public  class BasicHeader{
        //块类型，占两位，在BasicHeader里这个字段没啥用，他主要控制messageHeader的格式
        private Byte fmt;
        //chunk的ID AKA cs id
        private Integer chunkStreamId;

    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public  class MessageHeader{
        //3字节的timestamp，如果timestamp等于0xFFFFFF，则会将数值填入extendTimestamp
        private Integer timestamp;
        //3字节，表示整个message的长度
        private Integer messageLength;
        //1字节，表示message的Type
        private Byte messageTypeId;
        //4字节
        private Integer messageStreamId;
    }

    /**
     *
     * @param in
     * @param previousChunk(有些MessageHeader需要上一个chunk的信息)
     */
    public void generateHeader(ByteBuf in,RTMPChunk previousChunk){

        //1.生成BasicHeader

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

        //2、生成MessageHeader
        this.messageHeader = new MessageHeader();
        switch(basicHeader.fmt){
            case 0:{
                /* +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
                   |                timestamp                      |message length |
                   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
                   |     message length (cont)     |message type id| msg stream id |
                   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
                   |         message stream id (cont)              |
                   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
              */
                //读取3字节的时间戳
                this.messageHeader.setTimestamp(in.readMedium());
                //读取3字节的消息长度
                this.messageHeader.setMessageLength(in.readMedium());
                //读取1字节的messageType
                this.messageHeader.setMessageTypeId(in.readByte());
                //读取4字节的messageStreamId （注意大小端）
                this.messageHeader.setMessageStreamId(in.readIntLE());
                break;
            }
            case 1:{
                /* +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
                   |                timestamp                      |message length |
                   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
                   |     message length (cont)     |message type id|
                   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
               */
                //读取3字节的时间戳
                this.messageHeader.setTimestamp(in.readMedium());
                //读取3字节的消息长度
                this.messageHeader.setMessageLength(in.readMedium());
                //读取1字节的messageType
                this.messageHeader.setMessageTypeId(in.readByte());
                //获取上一个Chunk的MessageStreamId
                this.messageHeader.setMessageStreamId(previousChunk.getMessageHeader().getMessageStreamId());
                break;
            }
            case 2:{
                /*
                +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
                |              timestamp delta                  |
                +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
                //读取3字节的时间戳
                this.messageHeader.setTimestamp(in.readMedium());
                //messageLength和上一个Chunk的一样
                this.messageHeader.setMessageLength(previousChunk.getMessageHeader().getMessageLength());
                //messageType和上一个Chunk一样
                this.messageHeader.setMessageTypeId(previousChunk.getMessageHeader().getMessageTypeId());
                //messageStreamId和上一个Chunk一样
                this.messageHeader.setMessageStreamId(previousChunk.getMessageHeader().getMessageStreamId());
                break;
            }
            case 3:{
                //与前一Chunk一模一样
                this.messageHeader = previousChunk.messageHeader;
                break;
            }
            default:{
                log.error("不存在的chunkType，解析失败");
            }

            //3.如果MessageHeader的timestamp(3字节)全为1则有ExtendedTimestamp

            if(this.messageHeader.getTimestamp() == 0x0fff){
                this.setExtendTimestamp(in.readInt());
            }

        }
    }
}
