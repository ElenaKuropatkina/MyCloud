package com.elenakuropatkina.my.cloud.common;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;


public class FileSplit {

    Path path;
    String fileName;

    public FileSplit(Path path, String fileName) {
        this.path = path;
        this.fileName = fileName;

    }

    public ArrayList<FileMessage> split() throws Exception {
        ArrayList<FileMessage> arr = new ArrayList<>();
        long size = Files.size(Paths.get(path.toString() + "/" + fileName));
        int sizeOfFiles = 3 * 1024 * 1024;
        int partCount = (int) (size / sizeOfFiles);
        byte[] buffer = new byte[sizeOfFiles];
        try (BufferedInputStream input =
                     new BufferedInputStream(Files.newInputStream(Paths.get(path.toString() + "/tmp/" + fileName)))) {
            for (int i = 0; i < partCount; i++) {

                //String partFileName = "$" + i + fileName;
                //try (BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(Paths.get(path.toString() + "/" + partFileName)))) {
                try (BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(Files.createTempFile("$$$" + i, ".tmp", (FileAttribute<?>) Paths.get(path.toString() + "/tmp/"))))) {
                    int currentSize = 0;
                    while(currentSize<sizeOfFiles) {
                        int byteCount = input.read(buffer);
                        output.write(buffer, 0, byteCount);
                        currentSize += byteCount;
                    }
                }
                arr.add(new FileMessage(Paths.get(path.toString() + "/tmp/" + "$$$" + i + ".tmp" )));


            }
            return arr;

        }
    }

//    public static void mergeFiles(ArrayList<Path> listOfFilesToMerge, Path pathBigFile) throws IOException {
//        try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(pathBigFile))) {
//            for (Path p : listOfFilesToMerge) {
//                Files.copy(p, out);
//            }
//        }
//    }
//
//    public static ArrayList<File> listOfFilesToMerge(File f) {
//        String tmpName = f.getName();//{name}.{number}
//        String destFileName = tmpName.substring(0, tmpName.lastIndexOf('.'));//remove .{number}
//        File[] files = f.getParentFile().listFiles(
//                (File dir, String name) -> name.matches(destFileName + "[.]\\d+"));
//        Arrays.sort(files);//ensuring order 001, 002, ..., 010, ...
//        return Arrays.asList(files);
//    }
}



