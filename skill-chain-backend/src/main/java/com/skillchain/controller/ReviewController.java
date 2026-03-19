package com.skillchain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.common.Result;
import com.skillchain.entity.Review;
import com.skillchain.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/create")
    public Result<Review> createReview(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long orderId = Long.parseLong(params.get("orderId").toString());
        Integer rating = Integer.parseInt(params.get("rating").toString());
        String content = params.getOrDefault("content", "").toString();
        Review review = reviewService.createReview(userId, orderId, rating, content);
        return Result.success("评价成功", review);
    }

    @GetMapping("/skill/{skillId}")
    public Result<Page<Review>> getReviewsBySkill(
            @PathVariable Long skillId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<Review> pageInfo = reviewService.getReviewsBySkillId(skillId, page, size);
        return Result.success(pageInfo);
    }
}
