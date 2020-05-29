package com.elenakuropatkina.my.cloud.server;

import com.elenakuropatkina.my.cloud.common.CommandMessage;
import com.elenakuropatkina.my.cloud.common.FileMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;

public class MainHandler extends ChannelInboundHandlerAdapter {

//    FileService fs = new FileService();
//    CmdService cs = new CmdService();
    Service s = new Service();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof CommandMessage) {
            System.out.println(((CommandMessage) msg).getData());
            switch (((CommandMessage) msg).getCommand()) {
                case FILE_REQUEST:
                    try {
                        for (FileMessage fm: s.processingFileRequest((CommandMessage) msg)
                             ) {
                            ctx.writeAndFlush(fm);
                        }
                    } catch (IOException e) {
                        System.out.println("File not found");
                    }
                    break;
                case FILE_DELETE:
                    ctx.writeAndFlush(s.processingCM((CommandMessage) msg));
                    break;
                case FILE_GET_LIST:
                    System.out.println("FilesList");
                    ctx.writeAndFlush(s.getList());
                    break;
                case FILE_RENAME:
                    ctx.writeAndFlush(s.renameFile((CommandMessage) msg));
                    break;
                case AUTH:
                    ctx.writeAndFlush(s.auth((CommandMessage) msg));
                    break;

            }
        }

        if (msg instanceof FileMessage) {
            ctx.writeAndFlush(s.processingFM((FileMessage) msg));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
