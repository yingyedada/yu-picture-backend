package com.yupi.yupicturebackend.manager;

import cn.hutool.core.io.FileUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.yupi.yupicturebackend.config.CosClientConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    // 将本地文件上传到 COS
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        PicOperations picOperations = new PicOperations();
        // 1表示返回图片信息
        picOperations.setIsPicInfo(1);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     *
     * @param key 唯一键
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }
    /**
     * 上传对象（附带图片信息）
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        // 对图片进行处理（获取基本信息也被视作为一种处理）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        String webKey = FileUtil.mainName(key) +".webp";
        List<PicOperations.Rule> ruleList = new LinkedList<>();
        PicOperations.Rule rule1 = new PicOperations.Rule();
        rule1.setBucket(cosClientConfig.getBucket());
        rule1.setFileId(webKey);
        rule1.setRule("imageMogr2/format/webp");
        if(file.length() > 2 * 1024){
            PicOperations.Rule rule2 = new PicOperations.Rule();
            rule2.setBucket(cosClientConfig.getBucket());
            String thumbnail = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(file);
            rule2.setFileId(thumbnail);
            rule2.setRule(String.format("imageMogr2/thumbnail/%sx%s>",128,128));
            ruleList.add(rule2);
        }

        ruleList.add(rule1);

        // 构造处理参数
        putObjectRequest.setPicOperations(picOperations);
        picOperations.setRules(ruleList);
        return cosClient.putObject(putObjectRequest);
    }

    public void deleteObject(String key){
        cosClient.deleteObject(cosClientConfig.getBucket(),key);
    }

}
