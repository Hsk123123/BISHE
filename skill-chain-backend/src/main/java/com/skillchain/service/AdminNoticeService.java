package com.skillchain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.Notice;
import com.skillchain.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AdminNoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    public Page<Notice> getNoticeList(Integer page, Integer size) {
        Page<Notice> pageInfo = new Page<>(page, size);
        return noticeMapper.selectPage(pageInfo, new LambdaQueryWrapper<Notice>()
                .eq(Notice::getDeleted, 0)
                .orderByDesc(Notice::getIsTop)
                .orderByDesc(Notice::getCreateTime));
    }

    @Transactional
    public Notice createNotice(Notice notice) {
        notice.setDeleted(0);
        if (notice.getStatus() == null) {
            notice.setStatus(0);
        }
        if (notice.getIsTop() == null) {
            notice.setIsTop(0);
        }
        if (notice.getViewCount() == null) {
            notice.setViewCount(0);
        }
        if (notice.getStatus() == 1) {
            notice.setPublishTime(LocalDateTime.now());
        }
        noticeMapper.insert(notice);
        return notice;
    }

    @Transactional
    public void updateNotice(Long noticeId, Notice payload) {
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            throw new RuntimeException("公告不存在");
        }
        notice.setTitle(payload.getTitle());
        notice.setType(payload.getType());
        notice.setContent(payload.getContent());
        notice.setEndTime(payload.getEndTime());
        notice.setIsTop(payload.getIsTop() == null ? 0 : payload.getIsTop());
        notice.setStatus(payload.getStatus() == null ? notice.getStatus() : payload.getStatus());
        if (notice.getStatus() == 1 && notice.getPublishTime() == null) {
            notice.setPublishTime(LocalDateTime.now());
        }
        noticeMapper.updateById(notice);
    }

    @Transactional
    public void publish(Long noticeId) {
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            throw new RuntimeException("公告不存在");
        }
        notice.setStatus(1);
        notice.setPublishTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            throw new RuntimeException("公告不存在");
        }
        notice.setDeleted(1);
        noticeMapper.updateById(notice);
    }
}
