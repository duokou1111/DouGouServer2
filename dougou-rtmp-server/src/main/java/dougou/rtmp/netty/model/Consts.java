package dougou.rtmp.netty.model;

/**
 * @program: DouGouServer2
 * @description
 * @author: zihan.wu
 * @create: 2020/12/24/024
 **/
public class Consts {
    public static final byte SET_CHUNK_SIZE = 0x01;
    public static final byte ABORT_MESSAGE = 0x02;
    public static final byte ACKNOWLEDGEMENT = 0x03;
    public static final byte DATA_MESSAGE = 0x12;
    public static final byte AUDIO_MESSAGE = 0x08;
    public static final byte VIDEO_MESSAGE = 0x09;
    public static final byte SET_ACKNOWLEDGEMENT_SIZE = 0x05;
    public static final byte SET_BAND_WIDTH = 0x06;
    public static final byte RTMP_COMMAND_MESSAGE = 0x14;
    public static final int PROTOCOL_CHUNK_STREAM_ID = 2;
    public static final int PROTOCOL_MESSAGE_STREAM_ID = 5;
    public static final byte BAND_WIDTH_TYPE_SOFT = 0;
    public static final String COMMAND_CONNECT ="connect";
    public static final String COMMAND_PUBLISH = "publish";
    public static final String COMMAND_FCPUBLISH = "FCPublish";
    public static final String COMMAND_RELEASE_STREAM= "releaseStream";
    public static final String COMMAND_CREATE_STREAM = "createStream";
    public static final String COMMAND_DELETE_STREAM = "deleteStream";
    public static final String COMMAND_UNPUBLISH_STREAM = "FCUnpublish";
}