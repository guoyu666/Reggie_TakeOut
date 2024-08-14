package com.itheima.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.common.CustomException;
import com.itheima.reggie_takeout.entity.Category;
import com.itheima.reggie_takeout.entity.Dish;
import com.itheima.reggie_takeout.entity.Setmeal;
import com.itheima.reggie_takeout.mapper.CategoryMapper;
import com.itheima.reggie_takeout.service.CategoryService;
import com.itheima.reggie_takeout.service.DishService;
import com.itheima.reggie_takeout.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断是否有菜品或者套餐关联
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        // 条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = (int) dishService.count(dishLambdaQueryWrapper); //  强制类型转换
        // 查询当前分类是否关联了菜品，如果有，则不能删除
        if (count1 > 0) {
            // 已经关联了菜品，抛出一个异常
            throw new CustomException("该分类下有菜品，不能删除");
        }

        // 查询当前分类是否关联了套餐，如果有，则不能删除
        LambdaQueryWrapper<Setmeal> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        long count2 = setmealService.count(categoryLambdaQueryWrapper);
        if (count2 > 0) {
            // 已经关联了套餐，抛出一个异常
            throw new CustomException("该分类下有套餐，不能删除");
        }

        // 运行到这里，说明当前分类没有关联菜品和套餐，可以正常删除分类
        // 调用父类ServiceImpl的removeById方法
        super.removeById(id);
    }
}
