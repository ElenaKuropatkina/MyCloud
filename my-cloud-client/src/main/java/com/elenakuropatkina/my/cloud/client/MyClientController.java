package com.elenakuropatkina.my.cloud.client;

import com.elenakuropatkina.my.cloud.common.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.ResourceBundle;

public class MyClientController implements Initializable {

    @FXML
    TextField tfFileName;

    @FXML
    ListView<String> filesList;
    @FXML
    ListView<String> remoteFilesList;

    @FXML
    HBox upperPanel;
    @FXML
    HBox bottomPanel;

    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readMsg();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        if (fm.getFilename().startsWith("tmp")) {
                            String s = fm.getFilename();
                            String[] tokens = s.split("~");
                            System.out.println("Имя большого файла " + tokens[2]);
                            SplitAndMerge fs = new SplitAndMerge();
                            Files.write(Paths.get("client_storage/" + tokens[2]), fs.mergeFiles(fs.listOfFilesToMerge(Paths.get("client_storage/tmp")), Paths.get("client_storage/" + tokens[2])).getData(), StandardOpenOption.CREATE);
                        } else
                            Files.write(Paths.get("client_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalFilesList();
                    }
                    if (am instanceof ListMessage) {
                        ListMessage lm = (ListMessage) am;
                        System.out.println(lm.getList().toString());
                        refreshRemoteFilesList(lm);
                    }
                    if (am instanceof CommandMessage) {
                        CommandMessage cm = (CommandMessage) am;
                        setAuth(cm);
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

    public void setAuth(CommandMessage cm) {

        if (cm.getCommand().equals(CommandMessage.Command.AUTH_FALSE)) {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
        }
        if (cm.getCommand().equals(CommandMessage.Command.AUTH_OK)) {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
        }
    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        if (tfFileName.getLength() > 0) {
            Network.sendMsg(new CommandMessage(CommandMessage.Command.FILE_REQUEST, tfFileName.getText()));
            tfFileName.clear();

        }
    }

    public void pressOnUploadBtn(ActionEvent actionEvent) throws Exception {
        if (tfFileName.getLength() > 0) {
            if (Files.size(Paths.get("client_storage/" + tfFileName.getText())) > 50 * 1024 * 1024) {
                SplitAndMerge fs = new SplitAndMerge();
                for (FileMessage fm : fs.split(Paths.get("client_storage/" + tfFileName.getText()))) {
                    Network.sendMsg(fm);
                }
                Network.sendMsg(new CommandMessage(CommandMessage.Command.BIG_FILE_END, tfFileName.getText()));
            } else
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
            Path folder = Paths.get("client_storage");

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

    public void refreshRemoteFilesList(ListMessage lm) {
        Platform.runLater(() -> {
            remoteFilesList.getItems().clear();
            lm.getList().forEach(o -> remoteFilesList.getItems().add(o));
        });
    }

    public void tryToAuth(ActionEvent actionEvent) {
        if (loginField.getLength() > 0 && passwordField.getLength() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(loginField.getText());
            sb.append(" ");
            sb.append(passwordField.getText());
            Network.sendMsg(new CommandMessage(CommandMessage.Command.AUTH, sb.toString()));
        }
        loginField.clear();
        passwordField.clear();
    }

    public void selectFile(javafx.scene.input.MouseEvent mouseEvent) {
        String fileName;
        tfFileName.clear();
        if (filesList.isPickOnBounds() && filesList.isFocused()) {
            MultipleSelectionModel<String> msm = filesList.getSelectionModel();
            fileName = msm.getSelectedItem();
            tfFileName.setText(fileName);
        }
        if (remoteFilesList.isPickOnBounds() && remoteFilesList.isFocused()) {
            MultipleSelectionModel<String> msm = remoteFilesList.getSelectionModel();
            fileName = msm.getSelectedItem();
            tfFileName.setText(fileName);
        }
    }
}


