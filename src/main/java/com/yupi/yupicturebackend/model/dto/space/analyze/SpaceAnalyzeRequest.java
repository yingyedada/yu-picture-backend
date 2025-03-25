package com.yupi.yupicturebackend.model.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间查询范围
 */
@Data
public class SpaceAnalyzeRequest implements Serializable {
    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 是否查询公共空间
     */
    private boolean queryPublic;

    /**
     * 是否查询所有空间
     */
    private boolean queryAll;

    private static final long serialVersionUID = 1L;
}
