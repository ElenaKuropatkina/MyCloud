//package com.elenakuropatkina.my.cloud.server;
//
//import com.elenakuropatkina.my.cloud.common.FileMessage;
//import com.elenakuropatkina.my.cloud.common.ListMessage;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//
//public class FileService {
//
//    public ListMessage processing(FileMessage fm) throws IOException {
//        Files.write(Paths.get("my_server_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
//        return new CmdService().getList();
//    }
//
//}