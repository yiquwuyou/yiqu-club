package com.yiquwuyou.oss.util;

import com.yiquwuyou.oss.entity.FileInfo;
import io.minio.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * minio文件操作工具
 *
 * @author: ChickenWing
 * @date: 2023/10/11
 */
@Component
public class MinioUtil {

    @Resource
    private MinioClient minioClient;

    /**
     * 创建bucket桶
     */
    public void createBucket(String bucket) throws Exception {
        // 判断 bucket 是否存在，存在则不创建，不存在则创建
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    /**
     * 上传文件
     */
    public void uploadFile(InputStream inputStream, String bucket, String objectName) throws Exception {
        // 将文件以流的形式存入minioClient中
        minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(objectName)
                .stream(inputStream, -1, 5242889L).build());
    }

    /**
     * 列出所有桶
     */
    public List<String> getAllBucket() throws Exception {
        // 这里返回的是所有桶的名字，以列表的形式返回
        List<Bucket> buckets = minioClient.listBuckets();
        return buckets.stream().map(Bucket::name).collect(Collectors.toList());
    }

    /**
     * 列出当前桶及文件
     */
    public List<FileInfo> getAllFile(String bucket) throws Exception {
        // 获取当前桶中的所有文件信息
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucket).build());
        List<FileInfo> fileInfoList = new LinkedList<>();
        for (Result<Item> result : results) {
            FileInfo fileInfo = new FileInfo();
            Item item = result.get();
            fileInfo.setFileName(item.objectName());
            fileInfo.setDirectoryFlag(item.isDir());
            fileInfo.setEtag(item.etag());
            fileInfoList.add(fileInfo);
        }
        return fileInfoList;
    }

    /**
     * 下载文件
     */
    public InputStream downLoad(String bucket, String objectName) throws Exception {
        // 返回一个流，用于下载文件
        return minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).object(objectName).build()
        );
    }

    /**
     * 删除桶
     */
    public void deleteBucket(String bucket) throws Exception {
        minioClient.removeBucket(
                RemoveBucketArgs.builder().bucket(bucket).build()
        );
    }

    /**
     * 删除文件
     */
    public void deleteObject(String bucket, String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder().bucket(bucket).object(objectName).build()
        );
    }

}