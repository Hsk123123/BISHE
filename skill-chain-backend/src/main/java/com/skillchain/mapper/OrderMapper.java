package com.skillchain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skillchain.entity.Order;
import com.skillchain.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("<script>" +
            "SELECT o.*, " +
            "s.title AS skill_title, " +
            "s.description AS description, " +
            "pu.nickname AS worker_name, " +
            "pu.avatar AS worker_avatar, " +
            "bu.nickname AS buyer_name, " +
            "bu.avatar AS buyer_avatar " +
            "FROM `order` o " +
            "LEFT JOIN skill s ON o.skill_id = s.skill_id " +
            "LEFT JOIN user pu ON o.provider_id = pu.user_id " +
            "LEFT JOIN user bu ON o.buyer_id = bu.user_id " +
            "WHERE o.buyer_id = #{userId} " +
            "<if test='status != null'> AND o.status = #{status} </if>" +
            "ORDER BY o.create_time DESC " +
            "</script>")
    Page<OrderVO> selectOrderVOList(
            Page<OrderVO> page,
            @Param("userId") Long userId,
            @Param("status") Integer status
    );

    @Select("SELECT o.*, " +
            "s.title AS skill_title, " +
            "s.description AS description, " +
            "pu.nickname AS worker_name, " +
            "pu.avatar AS worker_avatar, " +
            "bu.nickname AS buyer_name, " +
            "bu.avatar AS buyer_avatar " +
            "FROM `order` o " +
            "LEFT JOIN skill s ON o.skill_id = s.skill_id " +
            "LEFT JOIN user pu ON o.provider_id = pu.user_id " +
            "LEFT JOIN user bu ON o.buyer_id = bu.user_id " +
            "WHERE o.order_id = #{orderId}")
    OrderVO selectOrderVOById(@Param("orderId") Long orderId);

    @Select("SELECT COALESCE(SUM(amount), 0) FROM `order` " +
            "WHERE provider_id = #{providerId} AND status = 5")
    java.math.BigDecimal selectTotalEarnings(@Param("providerId") Long providerId);

    @Select("SELECT COALESCE(SUM(amount), 0) FROM `order` " +
            "WHERE provider_id = #{providerId} AND status = 5 " +
            "AND YEAR(create_time) = YEAR(NOW()) AND MONTH(create_time) = MONTH(NOW())")
    java.math.BigDecimal selectMonthlyEarnings(@Param("providerId") Long providerId);

    @Select("SELECT COUNT(*) FROM `order` " +
            "WHERE provider_id = #{providerId} " +
            "AND YEAR(create_time) = YEAR(NOW()) AND MONTH(create_time) = MONTH(NOW())")
    Integer selectMonthlyOrders(@Param("providerId") Long providerId);

    @Select("SELECT COUNT(*) FROM `order` " +
            "WHERE provider_id = #{providerId} AND status = 5")
    Integer selectCompletedOrders(@Param("providerId") Long providerId);

    @Select("<script>" +
            "SELECT o.*, " +
            "s.title AS skill_title, " +
            "s.description AS description, " +
            "pu.nickname AS worker_name, " +
            "pu.avatar AS worker_avatar, " +
            "bu.nickname AS buyer_name, " +
            "bu.avatar AS buyer_avatar " +
            "FROM `order` o " +
            "LEFT JOIN skill s ON o.skill_id = s.skill_id " +
            "LEFT JOIN user pu ON o.provider_id = pu.user_id " +
            "LEFT JOIN user bu ON o.buyer_id = bu.user_id " +
            "WHERE o.provider_id = #{providerId} " +
            "<if test='status != null'> AND o.status = #{status} </if>" +
            "ORDER BY o.create_time DESC " +
            "</script>")
    Page<OrderVO> selectProviderOrderVOList(
            Page<OrderVO> page,
            @Param("providerId") Long providerId,
            @Param("status") Integer status
    );
}
