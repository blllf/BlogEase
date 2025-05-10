package com.blllf.blogease.controller;


import com.blllf.blogease.mapper.CategoryMapper;
import com.blllf.blogease.pojo.Category;
import com.blllf.blogease.pojo.Result;
import com.blllf.blogease.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;


    @PostMapping
    public Result add(@RequestBody @Validated Category category){
        categoryService.add(category);
        return Result.success();
    }

    //查询所有分类
    @GetMapping
    public Result<List<Category>> selectAll(){
        List<Category> list = categoryService.selectAll();
        return Result.success(list);
    }

    //查询所有用户的所有分类
    @GetMapping("/all")
    public Result<List<Category>> selectAll2(){
        List<Category> list = categoryService.selectAll2();
        return Result.success(list);
    }

    @GetMapping("/detail")
    public Result<Category> selectById(@RequestParam Integer id){
        Category c = categoryService.selectOneById(id);
        return Result.success(c);
    }



    @PutMapping
    public Result update(@RequestBody @Validated Category category){
        categoryService.update(category);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(Integer id){
        categoryService.delete(id);
        return Result.success();
    }

    @GetMapping("/select5")
    public Result<List<Category>> select5(){
        List<Category> list = categoryService.select5Category();
        return Result.success(list);
    }

    @GetMapping("/selectCateByArtId")
    public Result<Category> selectCategoryByArticleId(Integer id){
        Category category = categoryMapper.selectCategoryByArticleId(id);
        return Result.success(category);
    }

    @GetMapping("/findCategoryName")
    public Result<Category> findCategoryNameById(Integer id){
        Category category = categoryMapper.selectById(id);
        return Result.success(category);
    }

}
