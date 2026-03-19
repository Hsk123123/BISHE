package com.skillchain;

import com.skillchain.entity.User;
import com.skillchain.mapper.UserMapper;
import com.skillchain.service.AuthService;
import com.skillchain.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SkillChainApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private WalletService walletService;

    @Test
    void contextLoads() {
        assertNotNull(userMapper);
        assertNotNull(authService);
        assertNotNull(walletService);
    }

    @Test
    void testUserRegistration() {
        com.skillchain.dto.RegisterDTO registerDTO = new com.skillchain.dto.RegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setPassword("password123");
        registerDTO.setPhone("13800138000");
        registerDTO.setEmail("test@example.com");

        assertDoesNotThrow(() -> authService.register(registerDTO));

        User user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getUsername, "testuser")
        );

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void testWalletCheckIn() {
        Long userId = 1L;
        boolean firstCheckIn = walletService.checkIn(userId);
        assertTrue(firstCheckIn);

        boolean secondCheckIn = walletService.checkIn(userId);
        assertFalse(secondCheckIn);
    }
}