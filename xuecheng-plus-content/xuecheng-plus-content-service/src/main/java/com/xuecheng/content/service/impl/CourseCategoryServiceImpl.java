package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {

        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        List<CourseCategoryTreeDto> result = new ArrayList<>();

//        Map<String, CourseCategoryTreeDto> map = courseCategoryTreeDtos.stream()
//                .filter(item -> !id.equals(item.getId()))
//                .collect(Collectors.toMap(key -> key.getId(), value -> value, (k, v) -> k));
//
//        courseCategoryTreeDtos.stream()
//                .filter(item -> !id.equals(item.getId()))
//                .forEach(item -> {
//                    if(item.getParentid().equals(id)){
//                        result.add(item);
//                    }
//                    //找到item的父结点
//                    CourseCategoryTreeDto parent = map.get(item.getParentid());
//                    if(parent != null){
//                        if(parent.getChildrenTreeNodes() == null){
//                            parent.setChildrenTreeNodes(new ArrayList<>());
//                        }
//                        parent.getChildrenTreeNodes().add(item);
//                    }
//
//                });
        Map<String, List<CourseCategoryTreeDto>> parentMap = courseCategoryTreeDtos.stream()
                .filter(item -> !id.equals(item.getId()))
                .collect(Collectors.groupingBy(CourseCategoryTreeDto::getParentid));
        courseCategoryTreeDtos.stream()
                .filter(item -> !id.equals(item.getId()))
                .forEach(item -> {
                    if(id.equals(item.getParentid())){
                        result.add(item);
                    }
                    List<CourseCategoryTreeDto> childrenTreeNodes = parentMap.get(item.getId());
                    item.setChildrenTreeNodes(childrenTreeNodes);
                });

        return result;
    }
}
