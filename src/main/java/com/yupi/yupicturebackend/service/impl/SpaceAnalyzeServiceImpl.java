package com.yupi.yupicturebackend.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.model.dto.space.analyze.SpaceAnalyzeRequest;
import com.yupi.yupicturebackend.model.dto.space.analyze.SpaceCategoryAnalyzeRequest;
import com.yupi.yupicturebackend.model.dto.space.analyze.SpaceUsageAnalyzeRequest;
import com.yupi.yupicturebackend.model.entity.Picture;
import com.yupi.yupicturebackend.model.entity.Space;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.vo.space.analyze.SpaceCategoryAnalyzeResponse;
import com.yupi.yupicturebackend.model.vo.space.analyze.SpaceUsageAnalyzeResponse;
import com.yupi.yupicturebackend.service.PictureService;
import com.yupi.yupicturebackend.service.SpaceAnalyzeService;
import com.yupi.yupicturebackend.service.SpaceService;
import com.yupi.yupicturebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpaceAnalyzeServiceImpl implements SpaceAnalyzeService {

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;
    @Autowired
    private PictureService pictureService;

    @Override
    public SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser) {
        Long spaceId = spaceUsageAnalyzeRequest.getSpaceId();
        boolean queryPublic = spaceUsageAnalyzeRequest.isQueryPublic();
        boolean queryAll = spaceUsageAnalyzeRequest.isQueryAll();
        // 访问的是公共空间或者是全部空间，需要管理员权限
        if(queryAll || queryPublic){
            boolean admin = userService.isAdmin(loginUser);
            if(!admin){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"无权限");
            }
            // 拼接请求参数
            // 只需要查询总大小和总数量
            QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("picSize");
            if(queryPublic){
                // 公共图库
                queryWrapper.isNull("spaceId");
            }
            List<Object> objects = pictureService.getBaseMapper().selectObjs(queryWrapper);
            long usedSize = objects.stream().mapToLong(result -> result == null ? 0L : (Long) result).sum();
            long usedCount = objects.size();
            SpaceUsageAnalyzeResponse spaceUsageAnalyzeResponse = new SpaceUsageAnalyzeResponse();
            spaceUsageAnalyzeResponse.setUsedSize(usedSize);
            spaceUsageAnalyzeResponse.setUsedCount(usedCount);
            //公共图库无上限、无比例
            spaceUsageAnalyzeResponse.setMaxSize(null);
            spaceUsageAnalyzeResponse.setSizeUsageRatio(null);
            spaceUsageAnalyzeResponse.setMaxCount(null);
            spaceUsageAnalyzeResponse.setCountUsageRatio(null);
            return spaceUsageAnalyzeResponse;
        }else{
            // 私有空间
            Space space = spaceService.getById(spaceId);
            if(space == null){
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"空间不存在");
            }
            spaceService.checkSpaceAuth(loginUser,space);
            SpaceUsageAnalyzeResponse spaceUsageAnalyzeResponse = new SpaceUsageAnalyzeResponse();
            spaceUsageAnalyzeResponse.setUsedSize(space.getTotalSize());
            spaceUsageAnalyzeResponse.setMaxSize(space.getMaxSize());
            double sizeUsageRatio = NumberUtil.round((space.getTotalSize() * 100.0 / space.getMaxSize()),2).doubleValue();
            spaceUsageAnalyzeResponse.setSizeUsageRatio(sizeUsageRatio);
            spaceUsageAnalyzeResponse.setUsedCount(space.getTotalCount());
            spaceUsageAnalyzeResponse.setMaxCount(space.getMaxCount());
            double countUsageRatio = NumberUtil.round((space.getTotalCount() * 100.0 / space.getMaxCount()),2).doubleValue();
            spaceUsageAnalyzeResponse.setCountUsageRatio(countUsageRatio);
            return spaceUsageAnalyzeResponse;
        }

    }

    /**
     * 查询空间下面的不同分类的图片大小、占比等
     * @param spaceCategoryAnalyzeRequest
     * @return
     */
    @Override
    public List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser){
        if(spaceCategoryAnalyzeRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查询空间
        checkSpaceAnalyzeAuth(spaceCategoryAnalyzeRequest,loginUser);
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceCategoryAnalyzeRequest,queryWrapper);
        // select category,count(*),sum(pictureSize) from picture where spaceId = #{spaceId} group by category
        queryWrapper.select("category AS category","count(*) AS count","sum(pictureSize) AS totalSize");
        queryWrapper.groupBy("category");
        List<SpaceCategoryAnalyzeResponse> collect = pictureService.getBaseMapper().selectMaps(queryWrapper)
                .stream()
                .map(result -> {
                    String category = result.get("category") != null ? result.get("category").toString() : "未分类";
                    Long count = ((Number) result.get("count")).longValue();
                    Long totalSize = ((Number) result.get("totalSize")).longValue();
                    return new SpaceCategoryAnalyzeResponse(category, count, totalSize);
                }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 检查空间权限
     */
    private void checkSpaceAnalyzeAuth(SpaceAnalyzeRequest spaceAnalyzeRequest, User loginUser){
        Long spaceId = spaceAnalyzeRequest.getSpaceId();
        boolean queryPublic = spaceAnalyzeRequest.isQueryPublic();
        boolean queryAll = spaceAnalyzeRequest.isQueryAll();
        // 如果是查询全部空间或者是公共空间需要管理员权限
        if(queryAll || queryPublic){
            boolean admin = userService.isAdmin(loginUser);
            if(!admin){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"无权限访问公共图库");
            }
        }else{
            // 访问的是私有图库
            Space space = spaceService.getById(spaceId);
            if(space == null){
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"空间不存在");
            }
            // 校验是否有权限访问当前的空间
            spaceService.checkSpaceAuth(loginUser,space);
        }
    }
    /**
     * 根据空间权限来填充请求参数
     */
    public void fillAnalyzeQueryWrapper(SpaceAnalyzeRequest spaceAnalyzeRequest, QueryWrapper<Picture> queryWrapper){
        if (spaceAnalyzeRequest.isQueryAll()) {
            return ;
        }
        if(spaceAnalyzeRequest.isQueryPublic()){
            queryWrapper.isNull("spaceId");
            return ;
        }
        Long spaceId = spaceAnalyzeRequest.getSpaceId();
        if(spaceId != null){
            queryWrapper.eq("spaceId",spaceId);
            return ;
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR,"未指定查询范围");

    }


}
