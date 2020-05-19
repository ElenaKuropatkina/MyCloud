import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.IOException;

public class MainHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof CommandMessage) {
            CmdService cs = new CmdService((CommandMessage) msg);
            switch (((CommandMessage) msg).getCommand()) {
                case "FILE_REQUEST":
                    try {
                        FileMessage fm = cs.processingFileRequest();
                        ctx.writeAndFlush(fm);
                    } catch (IOException e) {
                        System.out.println("File not found");
                    }
                    break;
                case "FILE_DELETE":
                    cs.processing();
                    break;
//                case "FILE_GET_LIST":
//                    ListMessage lm = (ListMessage) cs.getList();
//                    System.out.println("FilesList");
//                    ctx.writeAndFlush(lm);
//                    break;
            }
        }

        if (msg instanceof FileMessage) {
            FileService fs = new FileService((FileMessage) msg);
            fs.processing();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
