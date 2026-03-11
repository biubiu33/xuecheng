package com.xuecheng.content;

import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TeachplanMapperTests {
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Test
    void testGetTreeNodes() {
        List<TeachplanDto> teachplanTree = teachplanMapper.selectTreeNodes(117L);
        System.out.println(teachplanTree);
    }
}
