# 功能升级清单

> 依据 docs/fork-survey.md 中各 fork 的独立修改整理，并对照本仓库（zivyuan/ZerotierFix，
> 核心版本 1.16.2）现状标注状态。复杂度：★ 低 / ★★ 中 / ★★★ 高。
> 建议按优先级从上到下实施；核心升级是地基，其后的稳定性/性能移植应基于新核心进行，
> 避免二次适配。

## P0 核心与构建（地基，建议一批完成）✅ 已全部完成

| # | 项目 | 来源 | 复杂度 | 说明 |
|---|------|------|--------|------|
| 1 | ZeroTier 核心升级 1.12.2 → 1.16.2 ✅ | gmij / hengliang8 / zwh8800 | ★★★ | 已完成：子模块切官方 1.16.2（fe29cd8），JNI 绑定层外置 core/jni/（官方 1.16.2 版 + kaaass 的 JNIEnvGuard 补丁），树外 CMake externals/CMakeLists.txt。实际改动远小于预估：C API 唯一破坏性变更是 ZT_Node_new 新增 config 参（官方 JNI 已适配），Java SDK 三方零差异，app 侧零改动 |
| 2 | 配置项跟随升级 ✅ | 随 #1 | ★★ | 已核对：VirtualNetworkConfig 无字段变化（SSO 字段 1.12.2 已有）、多播 API 不变、enableEncryptedHello/lowBandwidthMode 按 JNI 默认传 0；UdpCom 9994 端口、自定义 planet 重定向、MTU/DNS 路径均不受影响。核对方法（2026-10-19）：官方 1.12.2 / kaaass@d028a24 / 官方 1.16.2 三方逐文件比对 include/ZeroTierOne.h 与 java/src、java/jni；jni 层实质差异仅 com_zerotierone_sdk_Node.cpp（ZT_Node_new 新参 + 帧回调线程 attach），Java SDK 零差异，C ABI 其余结构体/枚举/回调签名不变 |
| 3 | libZeroTierOneJNI.so 16KB 页对齐 ✅ | stalxjason | ★ | 已完成：externals/CMakeLists.txt 中 `target_link_options(... -Wl,-z,max-page-size=16384)`；NDK 钉 25.2.9519653（NDK 26+ 不支持 minSdk 19） |
| 4 | CI 构建改造 ✅ | zwh8800 | ★ | 已完成：sparkfabrik action 替换为 setup-java（JDK 11）+ gradlew 直接构建，签名步骤保留。手动触发、tag 自动发 Release 未做，后续可加 |

## P1 稳定性（核心升级后移植）

| # | 项目 | 来源 | 复杂度 | 说明 |
|---|------|------|--------|------|
| 5 | UDP 接收线程竞态 + 畸形包长度防护 | stalxjason | ★ | 本仓库 UdpCom.java 有自己的改动，移植前先 diff 比对 |
| 6 | UdpCom 关闭 bug、零长度 UDP 包处理 | gmij | ★ | 同上，与 #5 一并处理 |
| 7 | VPN 重建风暴系列根因修复 | gmij | ★★★ | 网络切换后 VPN 反复重建/图标消失，多重根因（onStartCommand 死锁、重建死循环、TransactionTooSmallException），需真机实测验证 |
| 8 | 其他 VPN 启动时自动停止本服务 | z4yx | ★ | 与 #7 相关的边界场景 |

## P2 性能（先实测确认瓶颈存在，再决定是否移植）

| # | 项目 | 来源 | 复杂度 | 说明 |
|---|------|------|--------|------|
| 9 | TUN fd 阻塞 + 10ms poll sleep 导致的 ~100KB/s 瓶颈修复 | gmij | ★★ | 本仓库 TUN 通道未做过重构，可能存在同类瓶颈 |
| 10 | 热路径分配消除（InetAddress/ArrayList） | gmij | ★ | 随 #9 |
| 11 | 按目的 MAC 快速路径 | gmij | ★★ | 随 #9 |
| 12 | 耗电优化（轮询线程、通知渠道） | gmij | ★ | |

## P3 功能增强（按需挑选）

| # | 项目 | 来源 | 复杂度 | 说明 |
|---|------|------|--------|------|
| 13 | Quick Settings 磁贴（单击切换、长按打开、后台状态同步） | z4yx / bingling-sama / JAHNAN00 | ★★ | 三个 fork 均实现，z4yx 的状态同步最完整 |
| 14 | 扫码 / 深度链接加入网络 | stalxjason | ★★ | |
| 15 | QR 导入 Planet 文件 | stalxjason | ★ | 本仓库已支持本地文件 / HTTP URL 导入，QR 是补充 |
| 16 | 每应用 VPN 排除（per-app VPN） | stalxjason | ★★ | 分流场景刚需；z4yx 的每应用路由是更重的变体 |
| 17 | 开机自启增强（前台服务） | stalxjason | ★ | 本仓库已有 StartupReceiver，需比对差异 |
| 18 | 会话流量统计 | stalxjason | ★ | |
| 19 | OTA 更新提示 | gmij | ★ | |
| 20 | 面向使用者的业务日志视图 | gmij | ★★ | 排障时有用 |
| 21 | 中文本地化 | z4yx | ★ | |

## 可选大项（体量大，价值看需求）

| # | 项目 | 来源 | 复杂度 | 说明 |
|---|------|------|--------|------|
| 22 | 智能分流（CHINA_DIRECT / chnroutes、GFW list、组合模式、CIDR 超聚合、直播 CDN 自学习） | gmij | ★★★ | gmij 的主体功能，接近独立分支项目 |
| 23 | Fake-IP 模式 | gmij | ★★★ | 完整 VPN Fake-IP 实现，依赖 #22 |
| 24 | Material 3 UI 重构 | stalxjason | ★★ | 纯外观 |

## 无需移植（本仓库已具备或已覆盖）

- ICMPv6 NS 崩溃修复 —— 本仓库 0e3a520 已有
- 组播修复 —— 本仓库 b481035 已有（升级核心时需重新核对，见 #1）
- desugaring 旧设备 NIO 适配 —— 本仓库 b1113dd 已有
- 自定义 Planet 文件 —— 本仓库已支持非官方 Planet ID / 本地 / HTTP URL（stalxjason 的 QR 导入是补充，见 #15）
