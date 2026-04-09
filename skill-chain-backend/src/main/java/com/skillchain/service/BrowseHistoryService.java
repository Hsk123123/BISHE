package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.BrowseHistory;
import com.skillchain.entity.Skill;
import com.skillchain.mapper.BrowseHistoryMapper;
import com.skillchain.mapper.SkillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrowseHistoryService {

    @Autowired
    private BrowseHistoryMapper browseHistoryMapper;

    @Autowired
    private SkillMapper skillMapper;

    public void record(Long userId, Long skillId) {
        // 已存在则更新 viewed_at（UNIQUE KEY uk_user_skill + ON UPDATE）
        BrowseHistory existing = browseHistoryMapper.selectOne(
            new LambdaQueryWrapper<BrowseHistory>()
                .eq(BrowseHistory::getUserId, userId)
                .eq(BrowseHistory::getSkillId, skillId)
        );
        if (existing != null) {
            existing.setViewedAt(LocalDateTime.now());
            browseHistoryMapper.updateById(existing);
        } else {
            BrowseHistory history = new BrowseHistory();
            history.setUserId(userId);
            history.setSkillId(skillId);
            browseHistoryMapper.insert(history);
        }
    }

    public Page<Skill> getHistorySkills(Long userId, Integer page, Integer size) {
        List<BrowseHistory> histories = browseHistoryMapper.selectList(
            new LambdaQueryWrapper<BrowseHistory>()
                .eq(BrowseHistory::getUserId, userId)
                .orderByDesc(BrowseHistory::getViewedAt)
        );

        if (histories.isEmpty()) {
            return new Page<>(page, size);
        }

        List<Long> skillIds = histories.stream()
            .map(BrowseHistory::getSkillId)
            .collect(Collectors.toList());

        Page<Skill> pageInfo = new Page<>(page, size);
        return skillMapper.selectPage(pageInfo,
            new LambdaQueryWrapper<Skill>()
                .in(Skill::getSkillId, skillIds)
                .eq(Skill::getDeleted, 0)
        );
    }

    public void clearHistory(Long userId) {
        browseHistoryMapper.delete(
            new LambdaQueryWrapper<BrowseHistory>()
                .eq(BrowseHistory::getUserId, userId)
        );
    }
}
