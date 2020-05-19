import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MyClientController implements Initializable {

    public List<String> listRemote = new ArrayList<String>();


    @FXML
    TextField tfFileName;
    @FXML
    TextField loginField;
    @FXML
    TextField passwordField;

    @FXML
    ListView<String> filesListFromServer;

    @FXML
    ListView<String> filesList;

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

    public void pressOnDeleteBtn(ActionEvent actionEvent) {
        if (tfFileName.getLength() > 0) {
            Network.sendMsg(new CommandMessage(CommandMessage.Command.FILE_DELETE, tfFileName.getText()));
            tfFileName.clear();
        }
    }

    public void pressOnGetListBtn(ActionEvent actionEvent) {
        Network.sendMsg(new CommandMessage(CommandMessage.Command.FILE_GET_LIST));
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

}

