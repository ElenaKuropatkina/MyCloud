import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CmdService {

    CommandMessage cmd;

    public CmdService(CommandMessage cmd) {
        this.cmd = cmd;
    }

    public FileMessage processingFileRequest() throws IOException {
        if (Files.exists(Paths.get("my_server_storage/" + cmd.getFilename()))) {
            FileMessage fm = new FileMessage(Paths.get("my_server_storage/" + cmd.getFilename()));
            return fm;
        } else throw new IOException();
    }

    public void processing() throws IOException {
        if (Files.exists(Paths.get("my_server_storage/" + cmd.getFilename()))) {
            Files.delete(Paths.get("my_server_storage/" + cmd.getFilename()));
        }
    }



}
