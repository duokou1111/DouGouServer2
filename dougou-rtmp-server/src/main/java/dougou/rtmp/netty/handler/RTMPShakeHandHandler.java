package dougou.rtmp.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @program: DouGouServer2
 * @description RTMP握手协议
 * @author: zihan.wu
 * @create: 2020/12/23/023
 **/
public class RTMPShakeHandHandler extends ChannelInboundHandlerAdapter {
    private final Logger log= LoggerFactory.getLogger(RTMPShakeHandHandler.class);
    private static final byte S0 = 0x03;
    private byte[] S1;
    private byte[] S2;
    private boolean success = false;
    private final Integer C1_LENGTH = 1536;
    private Boolean isReceivedC0;
    private Boolean isReceivedC1;
    private ByteBuf heapBuf = ByteBufAllocator.DEFAULT.heapBuffer(1,6074);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //初始化C0,C1，开始时设置为未收到C0、C1
        isReceivedC0 = false;
        isReceivedC1 = false;
        super.channelActive(ctx);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        heapBuf.writeBytes((ByteBuf)msg);
        if(heapBuf.readableBytes()>=1 && isReceivedC0 == false){
            log.info("RTMPServer Received C0");
            byte C0 = heapBuf.readByte();
            //如果RTMP版本不为3则断开连接
            if(C0 != S0){
                log.info("RTMP协议版本错误");
                ctx.close();
            }
            //收到C0
            isReceivedC0 = true;
            //返回S0
            ctx.writeAndFlush(Unpooled.copiedBuffer(new byte[]{S0}));
            log.info("RTMP版本验证正确");
        }
        //接受C1
        //C1是长度为1536字节的数据包，前4个字节是时间戳，4-8字节全为0，剩下的1528字节为随机数
        if(heapBuf.readableBytes() >= C1_LENGTH && isReceivedC1 == false){
            byte[] C1 = new byte[C1_LENGTH];
            heapBuf.readBytes(C1);
            //S1格式和C1相同，复制C1内容
            S1 = Arrays.copyOf(C1,C1_LENGTH);
            //修改S1的前4个字节（即时间戳），其实也可以不设全为0
            S1 = setNowTime(S1);
            //设置随机数的最后一个字节为全1,这样校验C2比较轻松，不过我没写校验，这行可以去掉
            S1[C1_LENGTH - 1] = (byte) 0xff;
            //发送S1
            ctx.writeAndFlush(ctx.writeAndFlush(Unpooled.copiedBuffer(S1)));
            //复制C1内容
            S2 = C1;
            //S2的4-8字节需为C1的时间戳，因为S1是从C1复制过来的，所以S1的前四个字节就是C1的时间戳
            S2[4] = S1[0];
            S2[5] = S1[1];
            S2[6] = S1[2];
            S2[7] = S1[3];
            //发送S2
            ctx.writeAndFlush(ctx.writeAndFlush(Unpooled.copiedBuffer(S2)));
            isReceivedC1 = true;
            log.info("RTMPSever Received C1");
        }
        //如果收到了C1则开始接收C2
        if(isReceivedC1 == true && heapBuf.readableBytes() >= C1_LENGTH && success == false){
            //这里偷懒了就不验证C2内容了
            byte[] C2 = new byte[C1_LENGTH];
            heapBuf.readBytes(C2);
            success = true;
            log.info("RTMPServer Received C2");
        }
        if (success == true){
            log.info("RTMPServer HandShake Completed!");
            //将剩余未读完的字节交给下一个Handler
            ctx.fireChannelRead(heapBuf);
            //销毁当前Handler，因为之后都不需要握手了
            ctx.channel().pipeline().remove(RTMPShakeHandHandler.class);
        }

    }


    private byte[] setNowTime(byte[] arr){
        int time = (int) (System.currentTimeMillis() / 1000);
        arr[0] = (byte) (time >>24 & 0xff);
        arr[1] = (byte) (time >>16 & 0xff);
        arr[2] = (byte) (time >>8 & 0xff);
        arr[3] = (byte) (time & 0xff);
        return arr;
    }
}
