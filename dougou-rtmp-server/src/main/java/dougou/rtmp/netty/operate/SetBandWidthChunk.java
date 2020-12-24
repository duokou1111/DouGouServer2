package dougou.rtmp.netty.operate;

import dougou.rtmp.netty.model.Consts;
import dougou.rtmp.netty.model.RTMPChunk;
import dougou.rtmp.netty.tools.Tools;

/**
 * @program: DouGouServer2
 * @description: 设置宽带
 * @author: zihan.wu
 * @create: 2020-12-24 22:53
 **/
public class SetBandWidthChunk extends RTMPChunk {
    public SetBandWidthChunk(int bandWidth,byte type){
        this.basicHeader = new BasicHeader();
        basicHeader.setFmt((byte) 0);
        basicHeader.setChunkStreamId(Consts.PROTOCOL_CHUNK_STREAM_ID);
        this.messageHeader = new MessageHeader();
        messageHeader.setTimestamp(0);
        messageHeader.setMessageStreamId(Consts.PROTOCOL_MESSAGE_STREAM_ID);
        messageHeader.setMessageTypeId(Consts.SET_BAND_WIDTH);
        messageHeader.setMessageLength(5);
        byte[] payload = Tools.IntToBytes(bandWidth,type);
        payload[4] = type;
        this.setChunkData(payload);
    }
}
