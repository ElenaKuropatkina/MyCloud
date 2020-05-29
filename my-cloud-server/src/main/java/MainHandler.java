import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;

public class MainHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof CommandMessage) {

            System.out.println(((CommandMessage) msg).getData());
            switch (((CommandMessage) msg).getCommand()) {
                case FILE_REQUEST:
                    try {
                        ctx.writeAndFlush(new CmdService().processingFileRequest((CommandMessage) msg));
                    } catch (IOException e) {
                        System.out.println("File not found");
                    }
                    break;
                case FILE_DELETE:
                    ctx.writeAndFlush(new CmdService().processing((CommandMessage) msg));
                    break;
                case FILE_GET_LIST:
                    System.out.println("FilesList");
                    ctx.writeAndFlush(new CmdService().getList());
                    break;
                case FILE_RENAME:
                    ctx.writeAndFlush(new CmdService().renameFile((CommandMessage) msg));
                    break;
                case AUTH:
                    ctx.writeAndFlush(new CmdService().auth((CommandMessage) msg));
                    break;
            }
        }

        if (msg instanceof FileMessage) {
            ctx.writeAndFlush(new FileService().processing((FileMessage) msg));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
