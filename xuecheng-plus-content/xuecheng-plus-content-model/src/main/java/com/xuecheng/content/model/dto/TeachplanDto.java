package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class TeachplanDto extends Teachplan implements Serializable {
    private TeachplanMedia teachplanMedia;

    private List<TeachplanDto> teachPlanTreeNodes;
}
