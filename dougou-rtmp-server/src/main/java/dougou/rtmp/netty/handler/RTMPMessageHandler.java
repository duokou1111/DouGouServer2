package dougou.rtmp.netty.handler;

import dougou.rtmp.netty.messageHandler.IMessageHandler;
import dougou.rtmp.netty.model.Consts;
import dougou.rtmp.netty.model.RTMPChunk;
import dougou.rtmp.netty.tools.AMF0;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @program: DouGouServer2
 * @description: 处理数据
 * @author: zihan.wu
 * @create: 2020-12-24 23:24
 **/
@Slf4j
public class RTMPMessageHandler extends SimpleChannelInboundHandler<RTMPChunk> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RTMPChunk msg) throws Exception {
        System.out.println("msg.toString() = " + msg.toString());
        switch (msg.getMessageHeader().getMessageTypeId()){
            case Consts.AUDIO_MESSAGE:{
                log.info("SERVER RECEIVED A AUDIO MESSAGE");
                break;
            }
            case Consts.VIDEO_MESSAGE:{
                log.info("SERVER RECEIVED A VIDEO MESSAGE");
                break;
            }
            case Consts.DATA_MESSAGE:{
                log.info("SERVER RECEIVED A DATA MESSAGE");

                break;
            }
            case Consts.SET_CHUNK_SIZE:{

            }
            case Consts.ABORT_MESSAGE:{
                break;
            }
            case Consts.RTMP_COMMAND_MESSAGE:{
                List<Object> list = AMF0.decodeAll(Unpooled.copiedBuffer(msg.getChunkData()));
                String command = list.get(0).toString();
                log.info("SERVER RECEIVED A COMMAND MESSAGE:"+command);
                switch (command){
                    case Consts.COMMAND_CONNECT:{
                        log.info("CONNECT COMMAND:");
                        break;
                    }
                    case Consts.COMMAND_DELETE_STREAM:{
                        log.info("STREAM DELETE");
                        break;
                    }
                    case Consts.COMMAND_UNPUBLISH_STREAM:{
                        log.info("UNPUBLISH");

                        break;
                    }
                    case Consts.COMMAND_FCPUBLISH:{
                        log.info("INTO THE COMMAND FCPUBLISH");
                        break;
                    }
                    case Consts.COMMAND_RELEASE_STREAM:{
                        log.info("INTO THE COMMAND RELEASE STREAM;");
                        break;
                    }
                    case Consts.COMMAND_CREATE_STREAM:{
                        log.info("INTO THE COMMAND CREATE STREAM;");
                        break;
                    }
                    case Consts.COMMAND_PUBLISH:{
                        log.info("INTO THE COMMAND PUBLISH");
                        break;
                    }
                    default:{
                        log.info("RTMO COMMAND MESSAGE DO NOT ADAPT!");
                    }

                }
                break;
            }
            default:
                log.info("MessageTypeIdUnSupported!:"+msg.getMessageHeader().getMessageTypeId());
        }
        ServiceLoader<IMessageHandler> loader = ServiceLoader.load(IMessageHandler.class);
        while (loader.iterator())
    }
}
