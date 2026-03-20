# 联调问题记录（2026-03-19）

## P0

1. `withdrawal_request` 表缺失，提现链路中断
- 现象：`POST /withdrawal/apply` 返回 500
- 日志：`Table 'skill_chain.withdrawal_request' doesn't exist`
- 影响：提现与后台审核无法进行
- 临时处理：手动建表后链路恢复
- 建议修复：统一执行并校验初始化脚本，应用启动时增加 DB 健康检查与关键表校验

## P1

1. `skill` 表结构与实体不一致（缺 `unit_type/media_urls/deleted`）
- 现象：`GET /skill/provider/{providerId}` 返回 500
- 日志：`Unknown column 'unit_type' in 'field list'`
- 影响：技能查询、后续下单联调受阻
- 临时处理：补齐缺失列
- 建议修复：统一实体与 DDL，建立迁移脚本（Flyway/Liquibase）

2. 排期链路缺少创建接口
- 现象：无法仅靠 API 完成“发布技能 -> 下单”，必须手动写 `schedule`
- 影响：前后端联调无法闭环自动化
- 建议修复：新增 `schedule` 的增删改查接口，并补权限控制

## P2

1. 权限风险：普通用户可自切管理员角色
- 现象：`POST /user/role/switch?newRole=2` 未限制调用来源
- 影响：存在越权风险，生产不可接受
- 建议修复：该接口仅允许管理员调用；普通用户只能申请工作者，不能直接改角色

2. 文档与实现不一致
- 现象：文档描述与当前实际表结构、接口覆盖不完全一致
- 影响：联调成本升高，误判问题来源
- 建议修复：以当前可运行代码为准更新 README/运行文档/API 文档

## 回归建议（下一轮）

1. 固化 SQL 迁移脚本：一次执行可恢复到联调可用状态
2. 增加最小自动化 E2E（至少覆盖本次 11 步）
3. 每次提交触发后端 smoke test（注册/登录/下单/提现）
