package com.skillchain.controller;

import com.skillchain.common.Result;
import com.skillchain.entity.Decoration;
import com.skillchain.entity.Wallet;
import com.skillchain.service.DecorationService;
import com.skillchain.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private DecorationService decorationService;

    @GetMapping
    public Result<Wallet> getWallet(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Wallet wallet = walletService.getWalletByUserId(userId);
        return Result.success(wallet);
    }

    @PostMapping("/checkin")
    public Result<Map<String, Object>> checkIn(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = walletService.checkIn(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("points", success ? 10 : 0);

        if (success) {
            return Result.success("签到成功，获得10积分", data);
        } else {
            return Result.error("今日已签到");
        }
    }

    @PostMapping("/recharge")
    public Result<Void> recharge(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Double amount = Double.parseDouble(params.get("amount").toString());
        walletService.recharge(userId, java.math.BigDecimal.valueOf(amount));
        return Result.success("充值成功", null);
    }

    @GetMapping("/decorations")
    public Result<List<Decoration>> getDecorations(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Decoration> decorations = decorationService.getUserDecorations(userId);
        return Result.success(decorations);
    }

    @PostMapping("/decorations/{decoId}/equip")
    public Result<Void> equipDecoration(@PathVariable Long decoId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        decorationService.equipDecoration(userId, decoId);
        return Result.success("装备成功", null);
    }

    @PostMapping("/decorations/{decoId}/unequip")
    public Result<Void> unequipDecoration(@PathVariable Long decoId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        decorationService.unequipDecoration(userId, decoId);
        return Result.success("卸下成功", null);
    }

    @PostMapping("/decorations/purchase")
    public Result<Void> purchaseDecoration(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String name = (String) params.get("name");
        String imageUrl = (String) params.get("imageUrl");
        Integer type = (Integer) params.get("type");
        Integer cost = (Integer) params.get("cost");

        walletService.deductPoints(userId, java.math.BigDecimal.valueOf(cost));
        decorationService.purchaseDecoration(userId, name, imageUrl, type, cost);

        return Result.success("购买成功", null);
    }
}