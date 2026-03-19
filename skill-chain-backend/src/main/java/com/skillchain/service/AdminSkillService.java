package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.Skill;
import com.skillchain.mapper.SkillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminSkillService {

    @Autowired
    private SkillMapper skillMapper;

    public Page<Skill> getSkillList(Integer page, Integer size, Integer status) {
        Page<Skill> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Skill::getStatus, status);
        }

        wrapper.orderByDesc(Skill::getCreateTime);

        return skillMapper.selectPage(pageInfo, wrapper);
    }

    @Transactional
    public void approveSkill(Long skillId) {
        Skill skill = skillMapper.selectById(skillId);
        if (skill == null) {
            throw new RuntimeException("技能不存在");
        }

        skill.setStatus(1);
        skillMapper.updateById(skill);
    }

    @Transactional
    public void rejectSkill(Long skillId) {
        Skill skill = skillMapper.selectById(skillId);
        if (skill == null) {
            throw new RuntimeException("技能不存在");
        }

        skill.setStatus(2);
        skillMapper.updateById(skill);
    }

    @Transactional
    public void deleteSkill(Long skillId) {
        skillMapper.deleteById(skillId);
    }
}