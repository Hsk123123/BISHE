package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.aspect.TaskTrigger;
import com.skillchain.entity.Skill;
import com.skillchain.mapper.SkillMapper;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SkillService {

    @Autowired
    private SkillMapper skillMapper;

    // @Cacheable(value = "skillList", key = "#page + '-' + #size + '-' + #categoryId + '-' + #keyword + '-' + #sort")
    public Page<Skill> getSkillList(Integer page, Integer size, Long categoryId, String keyword, Integer sort) {
        Page<Skill> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();

        if (categoryId != null && categoryId > 0) {
            wrapper.eq(Skill::getCategoryId, categoryId);
        }

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Skill::getTitle, keyword)
                    .or()
                    .like(Skill::getDescription, keyword));
        }

        wrapper.eq(Skill::getStatus, 1);

        switch (sort) {
            case 1:
                wrapper.orderByAsc(Skill::getPricePerUnit);
                break;
            case 2:
                wrapper.orderByDesc(Skill::getPricePerUnit);
                break;
            case 3:
                wrapper.orderByDesc(Skill::getCreateTime);
                break;
            default:
                wrapper.orderByDesc(Skill::getCreateTime);
        }

        return skillMapper.selectPage(pageInfo, wrapper);
    }

    @TaskTrigger(action = "view_skill")
    public Skill getSkillById(Long skillId) {
        return skillMapper.selectById(skillId);
    }

    @Transactional
    public void publishSkill(Long providerId, Skill skill) {
        skill.setProviderId(providerId);
        skill.setStatus(0);
        skillMapper.insert(skill);
    }

    @Transactional
    public void updateSkill(Long providerId, Long skillId, Skill skill) {
        Skill existSkill = skillMapper.selectById(skillId);
        if (existSkill == null) {
            throw new RuntimeException("技能不存在");
        }
        if (!existSkill.getProviderId().equals(providerId)) {
            throw new RuntimeException("无权操作");
        }

        skill.setSkillId(skillId);
        skill.setProviderId(providerId);
        skillMapper.updateById(skill);
    }

    @Transactional
    public void deleteSkill(Long providerId, Long skillId) {
        Skill existSkill = skillMapper.selectById(skillId);
        if (existSkill == null) {
            throw new RuntimeException("技能不存在");
        }
        if (!existSkill.getProviderId().equals(providerId)) {
            throw new RuntimeException("无权操作");
        }

        skillMapper.deleteById(skillId);
    }

    public List<Skill> getProviderSkills(Long providerId) {
        return skillMapper.selectList(
                new LambdaQueryWrapper<Skill>()
                        .eq(Skill::getProviderId, providerId)
        );
    }
}