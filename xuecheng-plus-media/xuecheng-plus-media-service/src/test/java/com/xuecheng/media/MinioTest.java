package com.xuecheng.media;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MinioTest {
    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://101.35.45.108:9000")
                    .credentials("admin", "adminminio")
                    .build();

    @Test
    void testUpload() throws Exception {



          minioClient.uploadObject(
                  UploadObjectArgs.builder()
                  .bucket("mediafiles")
                  .object("1.pdf")
                  .filename("D:\\Downloads\\BaiduNetdiskDownload\\Java学习资料\\xuecheng\\day05 媒资管理 Nacos Gateway MinIO\\资料\\minio\\docker部署.txt")
                  .build());
    }

    @Test
    void testGet() throws Exception {
        GetObjectResponse inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("mediafiles")
                        .object("1.pdf")
                        .build());
        FileOutputStream outputStream = new FileOutputStream("D:\\WorkSpace\\DHU\\组会\\教资\\教资面试-历史\\1.pdf");
        IOUtils.copy(inputStream,outputStream);
        // 校验 文件 md5
        String local = DigestUtils.md5Hex(new FileInputStream("D:\\WorkSpace\\DHU\\组会\\教资\\教资面试-历史\\普通高中教科书 历史 必修 中外历史纲要上.pdf"));
        if(local.equals(DigestUtils.md5Hex(new FileInputStream("D:\\WorkSpace\\DHU\\组会\\教资\\教资面试-历史\\1.pdf")))){
            System.out.println("下载成功");
        }
    }
}
