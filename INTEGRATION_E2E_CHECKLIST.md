# 联调验收清单（E2E）

更新时间：2026-03-19
目标链路：注册 -> 登录 -> 发技能 -> 下单 -> 支付 -> 接单 -> 完成 -> 评价 -> 提现 -> 后台审核

## 1. 验收环境

- 后端：`http://localhost:8080/api`
- 数据库：`skill_chain`（MySQL 8.0）
- 鉴权：Bearer Token（JWT）

## 2. 角色与测试数据

- 服务者账号：`prov_20260319203624`（userId=13）
- 买家账号：`buyer_20260319203624`（userId=14）
- 管理员账号：`admin_20260319203624`（userId=15）
- 技能ID：`4`
- 订单ID：`1`
- 提现ID：`1`

## 3. 全链路检查项（可重复执行）

1. 注册账号（服务者/买家/管理员）
- 接口：`POST /auth/register`
- 预期：返回 code=200

2. 登录账号并拿 token
- 接口：`POST /auth/login`
- 预期：返回 token、user 信息

3. 管理员身份准备
- 方式：使用预置管理员账号登录（不再通过普通用户自切角色获得管理员权限）
- 预期：token 具备 admin 权限

4. 服务者发布技能
- 接口：`POST /skill/publish`
- 预期：发布成功（待审核状态）

5. 管理员审核技能
- 接口：`PUT /admin/skill/{skillId}/approve`
- 预期：审核通过

6. 选择可预约时段并下单
- 接口：`POST /order/create`
- 入参：`skillId/scheduleDate/timeSlot`
- 预期：创建订单成功，返回 orderId

7. 买家支付
- 接口：`POST /order/{id}/pay`
- 预期：支付成功，订单进入待接单

8. 服务者处理订单
- 接口：
  - `POST /order/{id}/accept`
  - `POST /order/{id}/start`
  - `POST /order/{id}/complete`
- 预期：状态流转正确

9. 买家评价
- 接口：`POST /review/create`
- 预期：评价成功，订单状态到已完成

10. 服务者提现
- 接口：`POST /withdrawal/apply`
- 预期：创建提现申请成功

11. 管理员审核提现
- 接口：`PUT /admin/withdrawal/{id}/approve`
- 预期：审核通过，提现状态更新为已通过

## 4. 本次实际执行结果（2026-03-19）

- register_accounts: PASS
- login_accounts: PASS
- promote_admin_and_relogin: PASS
- publish_skill: PASS
- query_provider_skills: PASS
- admin_approve_skill: PASS
- create_order: PASS
- pay_order: PASS
- accept_order: PASS
- start_service: PASS
- complete_service: PASS
- create_review: PASS
- apply_withdrawal: PASS
- admin_approve_withdrawal: PASS
- verify_order_completed: PASS（status=5）
- verify_withdrawal_status: PASS（status=1）

## 5. 本次联调中使用的临时补位

1. 排期曾无开放接口（已修复）
- 现状（历史）：曾经缺少排期创建接口，无法仅靠 API 完成链路
- 当前：已补充接口 `POST /skill/{skillId}/schedule`、`DELETE /skill/{skillId}/schedule/{scheduleId}`
- 处理方式：优先走 API；仅在排查历史数据问题时才使用 SQL

2. 数据库结构与实体不一致
- 现状：多张表缺少实体字段，导致部分接口 500
- 临时处理：补充缺失列后继续联调

3. 缺失 `withdrawal_request` 表
- 现状：提现申请时报 `Table 'skill_chain.withdrawal_request' doesn't exist`
- 临时处理：手动建表后继续联调
