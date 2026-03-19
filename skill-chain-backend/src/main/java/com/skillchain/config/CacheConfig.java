package com.skillchain.config;

// 暂时禁用Redis缓存配置
// 如需启用，请取消注释并确保Redis已安装

// @Configuration
// @EnableCaching
// public class CacheConfig {
//
//     @Bean
//     public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
//         RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                 .entryTtl(Duration.ofHours(1))
//                 .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                 .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
//                 .disableCachingNullValues();
//
//         return RedisCacheManager.builder(factory)
//                 .cacheDefaults(config)
//                 .transactionAware()
//                 .build();
//     }
// }
