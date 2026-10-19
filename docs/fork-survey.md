# kaaass/ZerotierFix 有价值的 fork 调研

> 调研时间：2026-10-19。基于 GitHub compare API 遍历 fork 网络（共 267 个 fork），
> 筛选出有独立提交且具参考价值的 fork，按价值排序。
>
> 本仓库（zivyuan/ZerotierFix）fork 自 kaaass/ZerotierFix，自最后合并点（PR #55）后
> 领先 23 个提交，主要工作为：ZeroTier 核心升级至 **1.12.2**（子模块指向
> kaaass/ZeroTierOne @ d028a24，仅含 Android JNI 绑定层）、ICMPv6 NS 崩溃修复、
> 组播实现修复（b481035）、自定义 Planet 文件（支持非官方 Planet ID / 本地文件 / HTTP URL）、
> 通知权限提醒、desugaring 旧设备适配、CI 构建签名改造（secrets 方式，无密钥入库）。
>
> 注意：官方最新 release 为 1.16.2（2026-05-28），本仓库核心落后 4 个次版本，
> 升级路径为 1.12.2 → 1.16.2。基于本仓库现状整理的功能升级清单见
> docs/upgrade-checklist.md 。

## 一、大量功能性开发

### 1. gmij/ZerotierFix（398 提交，17 star，最活跃）

<https://github.com/gmij/ZerotierFix> —— 体量接近独立分支项目

- 智能分流：CHINA_DIRECT（chnroutes 国内直连）、GFW list 模式、组合模式、每应用路由；
  CIDR 超聚合（压缩到 Android VPN 路由表上限内）；自学习直播 CDN 直连 IP
- Fake-IP 模式：完整的 VPN Fake-IP 实现
- 性能修复：约 100KB/s 下载瓶颈（TUN fd 阻塞、10ms poll sleep）；消除热路径分配
  （InetAddress/ArrayList）；按目的 MAC 快速路径；耗电优化（轮询线程、通知渠道）
- 稳定性：onStartCommand 死锁、VPN 重建死循环（网络切换后图标消失的多重根因）、
  TransactionTooSmallException、UdpCom 关闭 bug、零长度 UDP 包处理
- 实测驱动修复：微信视频卡顿、YouTube 不可用、谷歌商店路由等
- 核心升级：先后升至 wongsyrone/ZeroTierOne 1.14.2 和官方 1.16.0
  （为 1.16.0 补写 Android 版 CMakeLists，TunTapAdapter/UdpCom 适配官方 byte[] API）
- OTA 更新提示、面向使用者的业务日志视图

### 2. stalxjason/ZerotierFix（15 提交）

- ZeroTier 核心升到 1.16.2，vendor 源码目录方式、去掉 submodule
- libZeroTierOneJNI.so 按 16KB 页对齐（Android 15+ 强制要求）
- Material 3 UI 重构、扫码/深度链接加入网络、QR 导入 planet
- 每应用 VPN 排除、开机自启、前台服务
- UDP 接收线程竞态 + 畸形包长度防护修复
- 会话流量统计

### 3. z4yx/ZerotierA（18 提交，落后上游 2 个提交）

- Quick Settings 磁贴（单击切换、长按打开应用、后台状态同步、平滑状态迁移）
- 每应用路由（含 UI 和数据库支持）
- 其他 VPN 启动时自动停止本服务
- 中文本地化

## 二、核心升级 + 构建修复（与本 fork 方向重合）

### 4. hengliang8/ZerotierFix（6 提交）

- 独立完成 ZeroTier 1.16.2 核心升级（带 Android patch，不依赖 wongsyrone 的 ZeroTierOne fork）
- 1.0.11 原地升级版本方案、CI 安装 command line tools、签名与重装要求文档

### 5. zwh8800/ZerotierFix（4 提交）

- 核心升 1.16.2
- CI：手动触发、无 secrets 时跳过签名、替换失效的 sparkfabrik action 为直接 gradle 构建、
  tag 自动发 Release

## 三、单点功能

### 6. bingling-sama/ZerotierFix（8 提交）

- Quick Settings 磁贴一键加入/断开网络，含磁贴断开修复、首次启动权限引导、
  长按打开应用、磁贴图标

### 7. JAHNAN00/ZerotierFix（6 提交）

- Quick Settings VPN 磁贴、UI/图标现代化、包含 ZeroTier SDK Java 源码、改名包发布 1.1.0

## 未列入的

- nakido-official：9 个独立提交但 message 全为 `.`，无法评估内容
- 其余 ahead>0 的 fork（xm1h、ligusx、lovelace545、GroverLau 等）仅改 CI workflow；
  obanat、guangtoujihua 混入无关内容，无参考价值

## 对本仓库的可借鉴点

1. 核心升级 1.12.2 → 1.16.2（gmij / hengliang8 / zwh8800 均独立完成）——
   gmij 为官方核心补写的 Android 版 CMakeLists、TunTapAdapter/UdpCom 的 byte[] API 适配，
   以及 hengliang8 的带 Android patch 原地升级方案，均可直接参考
2. 16KB 页对齐（stalxjason）——Android 15+ 硬性要求，本仓库尚未处理，
   建议与核心升级同批进行（都要动 CMakeLists）
3. VPN 重建风暴系列根因修复（gmij）——网络切换时 VPN 反复重建是 kaaass 原版老毛病
4. UDP 竞态与畸形包防护（stalxjason）——UdpCom 本仓库也改过，移植前需比对
5. TUN 读热路径优化（gmij：100KB/s 瓶颈修复、按目的 MAC 快速路径）——
   本仓库 TUN 通道未做过 FileChannel 类重构，可先实测是否存在同类瓶颈再决定
6. CI 改造（zwh8800）——本仓库仍在用 sparkfabrik/android-build-action（该 fork 已验证其失效
   并替换为直接 gradle 构建），另有手动触发、无 secrets 跳过签名、tag 自动发 Release 可借鉴

## 附：签名密钥泄露说明（仅涉 wongsyrone fork，与本仓库无关）

wongsyrone/ZerotierFix 的提交 e044536 将 release 签名密钥库 `wongsyrone.jks`
连同全部口令提交入库，签名身份已视为失守。**本仓库无此问题**：
仓库内无 .jks 文件，CI 通过 secrets（SIGNING_KEY / ALIAS / KEY_STORE_PASSWORD）签名。
仅需注意：未来若从 wongsyrone 分支合并代码，其历史中的密钥文件不得带入，
且其 APK 与本仓库签名不同、无法互相覆盖安装。
