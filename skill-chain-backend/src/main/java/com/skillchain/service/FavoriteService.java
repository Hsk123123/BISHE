package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.Skill;
import com.skillchain.entity.UserFavorite;
import com.skillchain.exception.BusinessException;
import com.skillchain.mapper.SkillMapper;
import com.skillchain.mapper.UserFavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Autowired
    private SkillMapper skillMapper;

    public void addFavorite(Long userId, Long skillId) {
        boolean exists = isFavorited(userId, skillId);
        if (exists) {
            throw new BusinessException("已在收藏列表中");
        }
        UserFavorite fav = new UserFavorite();
        fav.setUserId(userId);
        fav.setSkillId(skillId);
        userFavoriteMapper.insert(fav);
    }

    public void removeFavorite(Long userId, Long skillId) {
        userFavoriteMapper.delete(
            new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getSkillId, skillId)
        );
    }

    public boolean isFavorited(Long userId, Long skillId) {
        return userFavoriteMapper.selectCount(
            new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getSkillId, skillId)
        ) > 0;
    }

    public Page<Skill> getFavoriteSkills(Long userId, Integer page, Integer size) {
        // 查出收藏的 skillId 列表（按收藏时间倒序）
        List<UserFavorite> favs = userFavoriteMapper.selectList(
            new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .orderByDesc(UserFavorite::getCreatedAt)
        );

        if (favs.isEmpty()) {
            return new Page<>(page, size);
        }

        List<Long> skillIds = favs.stream()
            .map(UserFavorite::getSkillId)
            .collect(Collectors.toList());

        // 分页从 skill 表查
        Page<Skill> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<Skill>()
            .in(Skill::getSkillId, skillIds)
            .eq(Skill::getDeleted, 0);

        Page<Skill> result = skillMapper.selectPage(pageInfo, wrapper);

        // 标记 isFavorited = true
        result.getRecords().forEach(s -> s.setIsFavorited(true));

        return result;
    }

    public long countFavorites(Long userId) {
        return userFavoriteMapper.selectCount(
            new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
        );
    }
}
