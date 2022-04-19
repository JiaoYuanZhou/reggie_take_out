package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PrivilegedActionException;
import java.util.Iterator;
import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 新增套餐信息，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void svaeWithDish(SetmealDto setmealDto) {
        //新增套餐信息
        this.save(setmealDto);

        //保存套餐和菜品的关联关系
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();

        Iterator<SetmealDish> iterator = setmealDishList.iterator();

        while (iterator.hasNext()) {

            SetmealDish next =  iterator.next();

            next.setSetmealId(setmealDto.getId() );

        }


        setmealDishService.saveBatch(setmealDishList);

    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {

        //删除套餐中的数据setemeal
        this.removeByIds(ids);

        //删除关系表中的数据setemeal_dish
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(queryWrapper);

    }


}
