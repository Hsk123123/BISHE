package com.skillchain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.skillchain.entity.TransactionLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionLogMapper extends BaseMapper<TransactionLog> {
}