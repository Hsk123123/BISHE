package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.skillchain.entity.Decoration;
import com.skillchain.mapper.DecorationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DecorationService {

    @Autowired
    private DecorationMapper decorationMapper;

    public List<Decoration> getUserDecorations(Long userId) {
        return decorationMapper.selectList(
                new LambdaQueryWrapper<Decoration>()
                        .eq(Decoration::getUserId, userId)
        );
    }

    public Decoration getEquippedDecoration(Long userId, Integer type) {
        return decorationMapper.selectOne(
                new LambdaQueryWrapper<Decoration>()
                        .eq(Decoration::getUserId, userId)
                        .eq(Decoration::getType, type)
                        .eq(Decoration::getIsEquipped, 1)
        );
    }

    @Transactional
    public void equipDecoration(Long userId, Long decoId) {
        Decoration decoration = decorationMapper.selectById(decoId);
        if (!decoration.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }

        decorationMapper.update(null,
                new LambdaUpdateWrapper<Decoration>()
                        .eq(Decoration::getUserId, userId)
                        .eq(Decoration::getType, decoration.getType())
                        .set(Decoration::getIsEquipped, 0)
        );

        decoration.setIsEquipped(1);
        decorationMapper.updateById(decoration);
    }

    @Transactional
    public void unequipDecoration(Long userId, Long decoId) {
        Decoration decoration = decorationMapper.selectById(decoId);
        if (!decoration.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }

        decoration.setIsEquipped(0);
        decorationMapper.updateById(decoration);
    }

    @Transactional
    public void purchaseDecoration(Long userId, String name, String imageUrl, Integer type, Integer cost) {
        Decoration decoration = new Decoration();
        decoration.setUserId(userId);
        decoration.setName(name);
        decoration.setImageUrl(imageUrl);
        decoration.setType(type);
        decoration.setIsEquipped(0);

        decorationMapper.insert(decoration);
    }
}