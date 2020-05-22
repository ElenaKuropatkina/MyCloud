import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileService {
    FileMessage fm;

    public FileService(FileMessage fm) {
        this.fm = fm;
    }

    public void processing() throws IOException {
        Files.write(Paths.get("my_server_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
    }

}