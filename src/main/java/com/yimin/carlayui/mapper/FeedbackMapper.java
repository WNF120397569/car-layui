package com.yimin.carlayui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimin.carlayui.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 反馈表(Feedback)表数据库访问层
 */
@Repository
@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {

}

