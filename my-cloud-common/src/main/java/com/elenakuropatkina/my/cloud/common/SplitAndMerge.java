package com.elenakuropatkina.my.cloud.common;

import java.io.*;
import java.nio.file.*;

import java.util.*;
import java.util.stream.Collectors;

public class SplitAndMerge {

    public ArrayList<FileMessage> split(Path path) throws Exception {
        ArrayList<FileMessage> arr = new ArrayList<>();
        long size = Files.size(path);
        int sizeOfFiles = 50 * 1024 * 1024;
        int partCount = (int) (size / sizeOfFiles + 1);
        int[] count = new int[partCount];
        for (int i = 0; i < partCount - 1; i++) {
            count[i] = sizeOfFiles;
        }
        count[partCount - 1] = (int) (size - sizeOfFiles * (partCount - 1));
        byte[] buffer = new byte[sizeOfFiles];
        try (BufferedInputStream input =
                     new BufferedInputStream(Files.newInputStream(path))) {
            for (int i = 0; i < partCount; i++) {
                String partFileName = "tmp~" + i + "~" + path.getFileName();
                try (BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(Paths.get(path.getParent() + "/tmp/" + partFileName)))) {
                    int currentSize = 0;
                    while (currentSize < count[i]) {
                        int byteCount = input.read(buffer);
                        output.write(buffer, 0, byteCount);
                        currentSize += byteCount;
                    }
                }
                arr.add(new FileMessage(Paths.get(path.getParent() + "/tmp/" + partFileName)));
                System.out.println("Split " + arr.get(i).getFilename());
                Files.delete(Paths.get(path.getParent() + "/tmp/" + partFileName));
            }
            return arr;
        }
    }

    public ArrayList<Path> listOfFilesToMerge(Path path) throws IOException {
        ArrayList<Path> pathArrayList = new ArrayList<>(Files.walk(path, 1, FileVisitOption.FOLLOW_LINKS)
                .filter(p -> !Files.isDirectory(p))
                .collect(Collectors.toList()));
        return pathArrayList;
    }

    public FileMessage mergeFiles(ArrayList<Path> listOfFilesToMerge, Path pathBigFile) throws IOException {
        ArrayList<InputStream> al = new ArrayList<>();
        TreeMap<Integer, Path> t = new TreeMap<>();
        for (Path p : listOfFilesToMerge) {
            String s = p.getFileName().toString();
            String[] tokens = s.split("~");
            //System.out.println(tokens[1]);
            t.put(Integer.valueOf(tokens[1]), p);
        }
        for (int i : t.keySet()) {
            al.add(new FileInputStream(t.get(i).toString()));
            //System.out.println(i);
        }
        BufferedInputStream in = new BufferedInputStream(new SequenceInputStream(Collections.enumeration(al)));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(String.valueOf(pathBigFile)));
        int x;
        while ((x = in.read()) != -1) {
            out.write(x);
        }
        in.close();
        out.close();
        return new FileMessage(pathBigFile);
    }
}
