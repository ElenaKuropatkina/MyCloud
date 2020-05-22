import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class MyClientController implements Initializable {

    private ListMessage lm;

    @FXML
    TextField tfFileName;

    @FXML
    ListView<String> filesList;
    @FXML
    ListView<String> remoteFilesList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readMsg();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("client_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalFilesList();
                    }
                    if (am instanceof ListMessage) {
                        lm = (ListMessage) am;
                        System.out.println(lm.getList().toString());
                        refreshRemoteFilesList();
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        refreshLocalFilesList();

    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        if (tfFileName.getLength() > 0) {
            Network.sendMsg(new CommandMessage(CommandMessage.Command.FILE_REQUEST, tfFileName.getText()));
            tfFileName.clear();
        }
    }

    public void pressOnUploadBtn(ActionEvent actionEvent) {
        if (tfFileName.getLength() > 0) {
            try {
                Network.sendMsg(new FileMessage((Paths.get("client_storage/" + tfFileName.getText()))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            tfFileName.clear();
        }
    }

    public void pressOnDeleteRemoteBtn(ActionEvent actionEvent) {
        if (tfFileName.getLength() > 0) {
            Network.sendMsg(new CommandMessage(CommandMessage.Command.FILE_DELETE, tfFileName.getText()));
            tfFileName.clear();
        }
    }

    public void pressOnDeleteLocalBtn(ActionEvent actionEvent) throws IOException {
        if (tfFileName.getLength() > 0) {
            Files.delete(Paths.get("client_storage/" + tfFileName.getText()));
            tfFileName.clear();
            refreshLocalFilesList();
        }
    }

    public void pressOnGetListBtn(ActionEvent actionEvent) {
        Network.sendMsg(new CommandMessage(CommandMessage.Command.FILE_GET_LIST));
    }

    public void pressOnRenameLocalBtn(ActionEvent actionEvent) throws IOException {
        if (tfFileName.getLength() > 0) {
            String s = tfFileName.getText();
            String[] tokens = s.split(" ");
            Path folder = Paths.get("client_storage/");

            File original = folder.resolve(tokens[0]).toFile();
            File newFile = folder.resolve(tokens[1]).toFile();

            if (original.exists() & original.isFile() & original.canWrite()) {
                original.renameTo(newFile);
            }
            tfFileName.clear();
            refreshLocalFilesList();
        }
    }

    public void pressOnRenameRemoteBtn(ActionEvent actionEvent) {
        if (tfFileName.getLength() > 0) {
            Network.sendMsg(new CommandMessage(CommandMessage.Command.FILE_RENAME, tfFileName.getText()));
        }
        tfFileName.clear();
    }

    public void refreshLocalFilesList() {
        Platform.runLater(() -> {
            try {
                filesList.getItems().clear();
                Files.list(Paths.get("client_storage"))
                        .filter(p -> !Files.isDirectory(p))
                        .map(p -> p.getFileName().toString())
                        .forEach(o -> filesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public void refreshRemoteFilesList() {
        Platform.runLater(() -> {
            remoteFilesList.getItems().clear();
            lm.getList().forEach(o -> remoteFilesList.getItems().add(o));
        });
    }
}

