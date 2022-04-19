package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 分类管理
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("商品保存成功 {}",category.toString());
        categoryService.save(category);

        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize) {
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort);

        //进行分页查询
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id 删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids) {

        log.info("删除id {}",ids);
//        categoryService.removeById(ids);
        categoryService.remove(ids);
        return R.success("删除分类成功");
    }

    /**
     * 修改菜品信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody  Category category) {
        log.info("修改菜品{}成功",category.getName());
        categoryService.updateById(category);
        return R.success("修改菜品信息成功");
    }

    /**
     * 根据条件查询菜品分类
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        //构造条件
        queryWrapper.eq(category.getType()!= null,Category::getType,category.getType());
        //排序
        queryWrapper.orderByAsc(Category::getSort);

        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }


}
