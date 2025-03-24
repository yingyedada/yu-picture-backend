package com.yupi.yupicturebackend.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupicturebackend.model.dto.picture.PictureQueryRequest;
import com.yupi.yupicturebackend.model.dto.space.SpaceAddRequest;
import com.yupi.yupicturebackend.model.dto.space.SpaceQueryRequest;
import com.yupi.yupicturebackend.model.entity.Picture;
import com.yupi.yupicturebackend.model.entity.Space;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.vo.PictureVO;
import com.yupi.yupicturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 张浩艺
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-03-09 10:20:15
*/
public interface SpaceService extends IService<Space> {
    /**
     * 校验空间参数
     * @param space
     */
    void validSpace(Space space,boolean add);

    /**
     * 根据空间请求自动填充参数
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 获取空间封装类
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 封装pagevo对象
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 新增空间
     * @param spaceAddRequest
     * @return
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    long addSpaceByRedission(SpaceAddRequest spaceAddRequest, User loginUser);
}
