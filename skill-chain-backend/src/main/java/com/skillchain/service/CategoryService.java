package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.Category;
import com.skillchain.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> getAllCategories() {
        return categoryMapper.selectList(
            new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSortOrder)
        );
    }

    public Page<Category> getCategoryList(Integer page, Integer size) {
        Page<Category> pageInfo = new Page<>(page, size);
        return categoryMapper.selectPage(pageInfo,
            new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSortOrder)
        );
    }

    public Category getCategoryById(Long id) {
        return categoryMapper.selectById(id);
    }

    public boolean createCategory(Category category) {
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        return categoryMapper.insert(category) > 0;
    }

    public boolean updateCategory(Category category) {
        return categoryMapper.updateById(category) > 0;
    }

    public boolean deleteCategory(Long id) {
        return categoryMapper.deleteById(id) > 0;
    }
}
