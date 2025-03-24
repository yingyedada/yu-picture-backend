package com.yupi.yupicturebackend;

import com.yupi.yupicturebackend.model.entity.Picture;
import com.yupi.yupicturebackend.service.PictureService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class YuPictureBackendApplicationTests {

    @Resource
    private PictureService pictureService;
    @Test
    void contextLoads() {
        Picture byId = pictureService.getById(1898305061832691714L);
        pictureService.clearPictureFile(byId);
    }

}
