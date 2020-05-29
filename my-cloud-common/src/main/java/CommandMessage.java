public class CommandMessage extends AbstractMessage {

    public enum Command {
        AUTH, AUTH_OK, AUTH_FALSE, FILE_REQUEST, FILE_DELETE, FILE_RENAME, FILE_GET_LIST
    }

    private Command cmd;
    private String data;

    public CommandMessage(Command cmd) {
        this.cmd = cmd;
    }

    public CommandMessage(Command cmd, String data) {
        this.cmd = cmd;
        this.data = data;
    }

    public Command getCommand() {
        return cmd;
    }

    public String getData() {
        return data;
    }
}
