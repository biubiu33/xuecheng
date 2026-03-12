package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.config.MinioConfig;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
* @description TODO
* @author Mr.M
* @date 2022/9/10 8:58
* @version 1.0
*/
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {
    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MinioClient minioClient;

    @Autowired
    MediaFileService currentProxy;

    @Value("${minio.bucket.files}")
    private String bucket_mediafiles;

    @Value("${minio.bucket.videofiles}")
    private String bucket_video;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String filepath) {
        String filename = uploadFileParamsDto.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        String mimeType = getMimeType(extension);
        String fileMd5 = getFileMd5(new File(filepath));
        String objectName = getDefaultFolderPath() + fileMd5 + extension;
        boolean result = addMediaFilesToMinIO(filepath, mimeType, bucket_mediafiles, objectName);
        if(!result){
        XueChengPlusException.cast("上传文件失败");
        }

        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_mediafiles, objectName);
        if(mediaFiles == null){
            XueChengPlusException.cast("保存文件信息到数据库失败");
        }
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles,uploadFileResultDto);
        return uploadFileResultDto;

    }
    //获取文件默认存储目录路径 年/月/日
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/")+"/";
        return folder;
    }
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
 // 根据扩展名获取文件类型
    private String getMimeType(String extension) {
        if(extension == null){
            extension="";
        }
        ContentInfo contentInfo = ContentInfoUtil.findExtensionMatch(extension);
        // 如果获取不到，使用默认值
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        if(contentInfo!=null){
            mimeType = contentInfo.getMimeType();
        }
        return mimeType;
    }
     // 上传文件到minio
    public boolean addMediaFilesToMinIO(String localFilePath,String mimeType,String bucket, String objectName){
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .filename(localFilePath)
                            .build());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件失败, bucket:{}, objectName:{}, 错误信息{}",bucket, objectName, e.getMessage());
        }
        return false;
    }
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName){
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
        mediaFiles = new MediaFiles();
        //拷贝基本信息
        BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
        mediaFiles.setId(fileMd5);
        mediaFiles.setFileId(fileMd5);
        mediaFiles.setCompanyId(companyId);
        mediaFiles.setUrl("/" + bucket + "/" + objectName);
        mediaFiles.setBucket(bucket);
        mediaFiles.setFilePath(objectName);
        mediaFiles.setCreateDate(LocalDateTime.now());
        mediaFiles.setAuditStatus("002003");
        mediaFiles.setStatus("1");
        //保存文件信息到文件表
        int insert = mediaFilesMapper.insert(mediaFiles);
        if (insert < 0) {
            log.error("保存文件信息到数据库失败,{}",mediaFiles.toString());
            XueChengPlusException.cast("保存文件信息失败");
        }
        log.debug("保存文件信息到数据库成功,{}",mediaFiles.toString());

        }
        return mediaFiles;

    }
}
