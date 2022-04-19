package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){

        setmealService.svaeWithDish(setmealDto);

        return R.success("保存套餐成功");

    }

    /**
     * 分页查询套餐信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageinfo = new Page<>();
        Page<SetmealDto> pageDtoinfo = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name != null,Setmeal::getName,name);

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageinfo,queryWrapper);

        BeanUtils.copyProperties(pageinfo,pageDtoinfo,"records");

        List<Setmeal> setmealrecords = pageinfo.getRecords();

        Iterator<Setmeal> iterator = setmealrecords.iterator();

        List<SetmealDto> setmealDtoList = new LinkedList<>();

        while (iterator.hasNext()) {
            //将分类名称赋给SetmealDto
            Setmeal next =  iterator.next();
            Long categoryId = next.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            SetmealDto setmealDto = new SetmealDto();
            setmealDto.setCategoryName(categoryName);

            BeanUtils.copyProperties(next,setmealDto);
            //放入集合
            setmealDtoList.add(setmealDto);
        }

        pageDtoinfo.setRecords(setmealDtoList);
        return R.success(pageDtoinfo);
    }



    /**
     * 根据ID删除套餐信息
     * @param ids
     */
    @DeleteMapping
    @Transactional
    public R<String> delete(@RequestParam List<Long> ids) {


        setmealService.removeWithDish(ids);

        return R.success("删除成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }
}
