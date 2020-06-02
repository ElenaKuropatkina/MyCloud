package com.elenakuropatkina.my.cloud.server;

import com.elenakuropatkina.my.cloud.common.CommandMessage;
import com.elenakuropatkina.my.cloud.common.FileMessage;
import com.elenakuropatkina.my.cloud.common.SplitAndMerge;
import com.elenakuropatkina.my.cloud.common.ListMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
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
        if (AuthService.checkLoginAndPass(login, pass)) {
            return new CommandMessage(CommandMessage.Command.AUTH_OK);
        } else
            return new CommandMessage(CommandMessage.Command.AUTH_FALSE);
    }

    public ArrayList<FileMessage> processingFileRequest(CommandMessage cmd) throws Exception {
        if (Files.exists(Paths.get("my_server_storage/" + user + "/" + cmd.getData()))) {
            ArrayList<FileMessage> arr = new ArrayList<>();
            if (Files.size(Paths.get("my_server_storage/" + user + "/" + cmd.getData())) > 50 * 1024 * 1024) {
                SplitAndMerge fs = new SplitAndMerge();
                for (FileMessage fm : fs.split(Paths.get("my_server_storage/" + user + "/" + cmd.getData()))) {
                    arr.add(fm);
                    System.out.println("Файл " + fm.getFilename());
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
        if (fm.getFilename().startsWith("tmp")) {
            Files.write(Paths.get("my_server_storage/" + user + "/tmp/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
            System.out.println("Write " + fm.getFilename());
        } else
            Files.write(Paths.get("my_server_storage/" + user + "/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
        return getList();
    }

    public ListMessage filesMerge(String s) throws IOException {
        SplitAndMerge fs = new SplitAndMerge();
        System.out.println(fs.listOfFilesToMerge(Paths.get("my_server_storage/" + user + "/tmp")).toString());
        Files.write(Paths.get("my_server_storage/" + user + "/" + s), fs.mergeFiles(fs.listOfFilesToMerge(Paths.get("my_server_storage/" + user + "/tmp")), Paths.get("my_server_storage/" + user + "/" + s)).getData(), StandardOpenOption.CREATE);
        for (Path p : Files.walk(Paths.get("my_server_storage/" + user + "/tmp"), 1, FileVisitOption.FOLLOW_LINKS)
                .filter(p -> !Files.isDirectory(p))
                .collect(Collectors.toList())) {
            Files.delete(p);
        }
        return getList();
    }

}

