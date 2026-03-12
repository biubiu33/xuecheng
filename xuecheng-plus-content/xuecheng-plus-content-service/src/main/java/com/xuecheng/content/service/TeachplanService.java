package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;

import java.util.List;

public interface TeachplanService {
    public List<TeachplanDto> findTeachplanTree(Long courseId);

    void deleteTeachplan(Long teachplanId);

    void orderByTeachplan(String moveType, Long teachplanId);

    void saveTeachplan(Teachplan teachplan);
}
