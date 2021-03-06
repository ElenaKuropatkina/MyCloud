import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class CmdService {

    public FileMessage processingFileRequest(CommandMessage cmd) throws IOException {
        if (Files.exists(Paths.get("my_server_storage/" + cmd.getData()))) {
            return new FileMessage(Paths.get("my_server_storage/" + cmd.getData()));
        } else throw new IOException();
    }

    public ListMessage processing(CommandMessage cmd) throws IOException {
        if (Files.exists(Paths.get("my_server_storage/" + cmd.getData()))) {
            Files.delete(Paths.get("my_server_storage/" + cmd.getData()));
        }
        return getList();
    }

    public ListMessage getList() throws IOException {
        ListMessage lm = new ListMessage();
        lm.setList(Files.list(Paths.get("my_server_storage/"))
                .filter(p -> !Files.isDirectory(p))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList()));
        System.out.println("get list");
        return lm;

    }

    public ListMessage renameFile(CommandMessage cmd) throws IOException {
        String s = cmd.getData();
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
        return getList();
    }

    public CommandMessage auth(CommandMessage cmd) throws IOException {
        String s = cmd.getData();
        String[] tokens = s.split(" ");
        String login = tokens[0];
        String pass = tokens[1];
        if(AuthService.checkLoginAndPass(login, pass)) {
            return new CommandMessage(CommandMessage.Command.AUTH_OK);
        } else
            return new CommandMessage(CommandMessage.Command.AUTH_FALSE);
    }





}
