package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.Category;
import com.skillchain.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    @GetMapping("/page")
    public Result<Page<Category>> getCategoryPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<Category> pageInfo = categoryService.getCategoryList(page, size);
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return Result.error(404, "分类不存在");
        }
        return Result.success(category);
    }

    @PostMapping
    public Result<String> createCategory(@RequestBody Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return Result.error(400, "分类名称不能为空");
        }
        boolean success = categoryService.createCategory(category);
        if (success) {
            return Result.success("创建成功");
        }
        return Result.error(500, "创建失败");
    }

    @PutMapping("/{id}")
    public Result<String> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setCategoryId(id);
        boolean success = categoryService.updateCategory(category);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error(500, "更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        boolean success = categoryService.deleteCategory(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error(500, "删除失败");
    }
}
