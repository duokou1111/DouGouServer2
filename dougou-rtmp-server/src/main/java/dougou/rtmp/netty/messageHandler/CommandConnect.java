package dougou.rtmp.netty.messageHandler;

import dougou.rtmp.netty.model.Consts;
import dougou.rtmp.netty.model.RTMPChunk;
import dougou.rtmp.netty.operate.SetAconowledgementSizeChunk;
import dougou.rtmp.netty.operate.SetBandWidthChunk;
import dougou.rtmp.netty.operate.SetChunkSizeChunk;
import dougou.rtmp.netty.operate.SetMessageResponseChunk;
import dougou.rtmp.netty.tools.AMF0;
import dougou.rtmp.netty.tools.AMF0Project;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: DouGouServer2
 * @description
 * @author: zihan.wu
 * @create: 2020/12/24/024
 **/
public class CommandConnect implements IMessageHandler{
    @Override
    public Boolean isSupport(String b) {
        if (b == Consts.COMMAND_CONNECT){
            return true;
        }
        return false;
    }

    @Override
    public void handle(RTMPChunk receive, ChannelHandlerContext ctx) {
        List<Object> list = AMF0.decodeAll(Unpooled.copiedBuffer(receive.getChunkData()));
        List<Object> result = new ArrayList<Object>();
        result.add("_result");
        result.add(list.get(1));// transaction id
        result.add(new AMF0Project().addProperty("fmsVer", "FMS/3,0,1,123").addProperty("capabilities", 31));
        result.add(new  AMF0Project().addProperty("level", "status").addProperty("code", "NetConnection.Connect.Success")
                .addProperty("description", "Connection succeeded").addProperty("objectEncoding", 0));
        ctx.writeAndFlush(new SetAconowledgementSizeChunk(5000000));
        ctx.writeAndFlush(new SetBandWidthChunk(5000000,Consts.BAND_WIDTH_TYPE_SOFT));
        ctx.writeAndFlush(new SetChunkSizeChunk(5000));
        ctx.writeAndFlush(new SetMessageResponseChunk(result));
    }


}
