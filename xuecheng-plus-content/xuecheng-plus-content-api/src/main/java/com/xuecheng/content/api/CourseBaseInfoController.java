package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(value = "课程信息管理", tags = "课程管理接口")
@RestController
public class CourseBaseInfoController {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParams){
        PageResult<CourseBase> pageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParams);
        return pageResult;
    }

    /**
     * 新增课程接口
     * @Validated JSR303校验
     * @param addCourseDto
     * @return
     */
    @ApiOperation("新增课程接口")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated AddCourseDto addCourseDto){
        long companyId = 1232141425L;
        CourseBaseInfoDto courseBase = courseBaseInfoService.createCourseBase(companyId, addCourseDto);
        return courseBase;
    }

    @ApiOperation("根据课程id查询接口")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.getCourseBaseInfo(courseId);
        return courseBaseInfoDto;
    }

    @ApiOperation("修改课程接口")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated EditCourseDto editCourseDto){
        long companyId = 1232141425L;
        return courseBaseInfoService.modifyCourseBase(companyId, editCourseDto);
    }

    @ApiOperation("删除课程")
    @DeleteMapping("/course/{courseId}")
    public void deleteCourse(@PathVariable Long courseId) {
        Long companyId = 1232141425L;
        courseBaseInfoService.deleteCourse(companyId,courseId);
    }
}
