import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

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

    public ListMessage getList() throws IOException {
        ListMessage lm = new ListMessage();
        lm.setList(Files.list(Paths.get("my_server_storage"))
                .filter(p -> !Files.isDirectory(p))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList()));
        System.out.println("get list");

        return lm;

    }

    public void renameFile() {
        String s = cmd.getFilename();
        System.out.println(s);
        String[] tokens = s.split(" ");
        System.out.println(tokens[0]);
        System.out.println(tokens[1]);
        if (Files.exists(Paths.get("my_server_storage/" + tokens[0]))) {
            Path folder = Paths.get("my_server_storage/");
            File original = folder.resolve(tokens[0]).toFile();
            File newFile = folder.resolve(tokens[1]).toFile();
            if (original.exists() & original.isFile() & original.canWrite()) {
                original.renameTo(newFile);
            }
        }
    }

}
