package com.elenakuropatkina.my.cloud.server;

import com.elenakuropatkina.my.cloud.common.CommandMessage;
import com.elenakuropatkina.my.cloud.common.FileMessage;
import com.elenakuropatkina.my.cloud.common.FileSplit;
import com.elenakuropatkina.my.cloud.common.ListMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Service {

    String user;

    public CommandMessage auth(CommandMessage cmd) throws IOException {
        String s = cmd.getData();
        String[] tokens = s.split(" ");
        String login = tokens[0];
        user = login;
        String pass = tokens[1];
        if(AuthService.checkLoginAndPass(login, pass)) {
            return new CommandMessage(CommandMessage.Command.AUTH_OK);
        } else
            return new CommandMessage(CommandMessage.Command.AUTH_FALSE);
    }

    public ArrayList<FileMessage> processingFileRequest(CommandMessage cmd) throws Exception {
        if (Files.exists(Paths.get("my_server_storage/" + user + "/" + cmd.getData()))) {
            ArrayList<FileMessage> arr = new ArrayList<>();
            if(Files.size(Paths.get("my_server_storage/" + user + "/" + cmd.getData())) > 3*1024*1024){
               FileSplit fs = new FileSplit(Paths.get("my_server_storage/" + user + "/"), cmd.getData());
                for (FileMessage fm : fs.split()
                     ) {
                    arr.add(fm);
                }
            } else {
                arr.add(new FileMessage(Paths.get("my_server_storage/" + user + "/" + cmd.getData())));
            }
            return arr;
        } else throw new IOException();
    }




    public ListMessage processingCM(CommandMessage cmd) throws IOException {
        if (Files.exists(Paths.get("my_server_storage/" + user + "/" + cmd.getData()))) {
            Files.delete(Paths.get("my_server_storage/" + user + "/" + cmd.getData()));
        }
        return getList();
    }

    public ListMessage getList() throws IOException {
        ListMessage lm = new ListMessage();
        lm.setList(Files.list(Paths.get("my_server_storage/" + user + "/"))
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
        if (Files.exists(Paths.get("my_server_storage/" + user + "/" + tokens[0]))) {
            Path folder = Paths.get("my_server_storage/" + user);
            File original = folder.resolve(tokens[0]).toFile();
            File newFile = folder.resolve(tokens[1]).toFile();
            if (original.exists() & original.isFile() & original.canWrite()) {
                original.renameTo(newFile);
            }
        }
        return getList();
    }

    public ListMessage processingFM(FileMessage fm) throws IOException {
        Files.write(Paths.get("my_server_storage/" + user + "/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
        return getList();
    }




}

