package com.yupi.yupicturebackend.service;

import com.yupi.yupicturebackend.model.dto.space.analyze.SpaceUsageAnalyzeRequest;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.vo.space.analyze.SpaceUsageAnalyzeResponse;

public interface SpaceAnalyzeService {
    SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);
}
