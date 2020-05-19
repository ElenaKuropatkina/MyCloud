public class CommandMessage extends AbstractMessage {

    public enum Command {
        FILE_REQUEST, FILE_DELETE, FILE_RENAME, FILE_GET_LIST
    }

    private Command cmd;
    private String filename;

    public CommandMessage(Command cmd){
        this.cmd = cmd;
    }

    public CommandMessage(Command cmd, String filename){
        this.cmd = cmd;
        this.filename = filename;
    }

    public String getCommand(){
        return cmd.toString();
    }

    public String getFilename() {
        return filename;
    }
}
