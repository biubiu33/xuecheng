package com.xuecheng.media;

import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;


public class BigFileTest  {
    @Test
    public void testChunk() throws Exception {
        String filePath = "D:\\WorkSpace\\DHU\\组会\\教资\\教资面试-历史\\普通高中教科书 历史 必修 中外历史纲要上.pdf";
        String chunkFileFolderPath = "D:\\WorkSpace\\DHU\\组会\\教资\\教资面试-历史\\chunk\\";

        int chunkSize = 5 * 1024 * 1024;
        byte[] cache = new byte[1024];


        File file = new File(filePath);
        int chunkNum = (int) Math.ceil((double) file.length() / chunkSize);

        RandomAccessFile raf = new RandomAccessFile( file, "r");

        for (int i = 0; i < chunkNum; i++) {
            File chunkFile = new File(chunkFileFolderPath + i);
            RandomAccessFile raf_w = new RandomAccessFile(chunkFile, "rw");

            int len = -1;
            while ((len = raf.read(cache)) != -1) {
                raf_w.write(cache, 0, len);
                if(chunkSize <= chunkFile.length()) break;
            }

            raf_w.close();
        }

        raf.close();

    }

    @Test
    public void mergeFile() throws Exception {
        String filePath = "D:\\WorkSpace\\DHU\\组会\\教资\\教资面试-历史\\普通高中教科书 历史 必修 中外历史纲要上.pdf";
        String mergeFilePath = "D:\\WorkSpace\\DHU\\组会\\教资\\教资面试-历史\\普通高中教科书 历史 必修 中外历史纲要上_2.pdf";
        String chunkFileFolderPath = "D:\\WorkSpace\\DHU\\组会\\教资\\教资面试-历史\\chunk\\";
        File chunkFileFolder = new File(chunkFileFolderPath);
        File[] chunkFiles = chunkFileFolder.listFiles();
        List<File> fileList = Arrays.asList(chunkFiles);
        fileList.sort(Comparator.comparingInt(f -> Integer.parseInt(f.getName())));
        byte[] bytes = new byte[1024];
        //合并文件写入流
        try(RandomAccessFile raf_w = new RandomAccessFile(mergeFilePath, "rw")) {
            for (int i = 0; i < fileList.size(); i++) {
                try(RandomAccessFile raf_r = new RandomAccessFile(fileList.get(i), "r")){
                    int len = -1;
                    while ((len = raf_r.read(bytes)) != -1) {
                        raf_w.write(bytes, 0, len);
                    }
                }
            }
        }
        md5Check(filePath, mergeFilePath);
    }

    public void md5Check(String filePath, String mergeFilePath) throws Exception {
        FileInputStream file = new FileInputStream(filePath);
        FileInputStream mergeFile = new FileInputStream(mergeFilePath);
        String s1 = DigestUtils.md5Hex(file);
        String s2 = DigestUtils.md5Hex(mergeFile);
        System.out.println(s1.equals(s2)?"一致":"不一致");

    }


}
