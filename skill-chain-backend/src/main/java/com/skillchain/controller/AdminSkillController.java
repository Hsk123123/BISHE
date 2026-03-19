package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.Skill;
import com.skillchain.service.AdminSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/skill")
public class AdminSkillController {

    @Autowired
    private AdminSkillService adminSkillService;

    @GetMapping("/list")
    public Result<Page<Skill>> getSkillList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status
    ) {
        Page<Skill> pageInfo = adminSkillService.getSkillList(page, size, status);
        return Result.success(pageInfo);
    }

    @PutMapping("/{skillId}/approve")
    public Result<Void> approveSkill(@PathVariable Long skillId) {
        adminSkillService.approveSkill(skillId);
        return Result.success("审核通过", null);
    }

    @PutMapping("/{skillId}/reject")
    public Result<Void> rejectSkill(@PathVariable Long skillId) {
        adminSkillService.rejectSkill(skillId);
        return Result.success("审核拒绝", null);
    }

    @DeleteMapping("/{skillId}")
    public Result<Void> deleteSkill(@PathVariable Long skillId) {
        adminSkillService.deleteSkill(skillId);
        return Result.success("删除成功", null);
    }
}