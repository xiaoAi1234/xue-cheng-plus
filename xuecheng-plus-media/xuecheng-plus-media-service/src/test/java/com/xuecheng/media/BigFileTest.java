package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ClassName: BigFileTest
 * Package: com.xuecheng.media
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/2/28 21:04
 * @Version 1.0
 */
public class BigFileTest {
    @Test
    public void testChunk() throws IOException {
        //获取原文件流
        File file = new File("D:\\develop\\upload\\1.docx");
        RandomAccessFile raf_r = new RandomAccessFile(file,"r");
        //设置分块大小并计算分块数量
        int chunkSize = 1024 * 1024 * 1;
        int chunkNum = (int)Math.ceil((file.length() * 1.0) / chunkSize);
        //对文件块进行写入
        int len = -1;
        byte[] buffer = new byte[1024];
        for (int i = 0; i < chunkNum; i++) {
            File file1 = new File("D:\\develop\\upload\\chunk\\" + i);
            RandomAccessFile raf_rw = new RandomAccessFile(file1,"rw");
            while ((len = raf_r.read(buffer)) != -1) {
                raf_rw.write(buffer,0,len);
                if (raf_rw.length() >= chunkSize) {
                    break;
                }

            }
            raf_rw.close();

        }

        //关闭文件流
        raf_r.close();
    }

    @Test
    public void testMerge() throws IOException {
        //对分块文件列表进行排序
        File chunckFolder = new File("D:\\develop\\upload\\chunk");
        File[] files = chunckFolder.listFiles();
        //转换成list便于排序
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });

        //将文件块合并
        File file = new File("D:\\develop\\upload\\2.docx");
        RandomAccessFile raf_wr = new RandomAccessFile(file,"rw");
        byte[] buffer = new byte[1024];
        int len = -1;
        //循环取出集合中的文件
        for (File chunkFile : fileList) {
            RandomAccessFile raf_r = new RandomAccessFile(chunkFile,"r");
            while ((len = raf_r.read(buffer)) != -1) {
                raf_wr.write(buffer,0,len);
            }

            raf_r.close();
        }

        raf_wr.close();

        //比较md5值
        File originalFile = new File("D:\\develop\\upload\\1.docx");
        FileInputStream fileInputStream = new FileInputStream(originalFile);
        FileInputStream mergeFileStream = new FileInputStream(file);
        String originalMd5 = DigestUtils.md5Hex(fileInputStream);
        String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);
        if (originalMd5.equals(mergeFileMd5)) {
            System.out.println("成功！");
        }




        }
}
