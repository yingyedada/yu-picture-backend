package com.yupi.yupicturebackend.service;

import com.yupi.yupicturebackend.model.dto.space.analyze.SpaceCategoryAnalyzeRequest;
import com.yupi.yupicturebackend.model.dto.space.analyze.SpaceUsageAnalyzeRequest;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.vo.space.analyze.SpaceCategoryAnalyzeResponse;
import com.yupi.yupicturebackend.model.vo.space.analyze.SpaceUsageAnalyzeResponse;

import java.util.List;

public interface SpaceAnalyzeService {
    SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);


    List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser);
}
