# GKD (Root KeepAlive 定制分支)

> 基于 GKD 官方项目 ([gkd-kit/gkd](https://github.com/gkd-kit/gkd)) v1.12.1 二次开发的无障碍深度保活分支。  
> 维护者：**life**  
> 产物下载：[GitHub Releases](https://github.com/lifefuck/gkd-root-keepalive/releases)（已签名 APK：`GKD_Root_KeepAlive_v1.12.1.apk`）

---

## 解决的问题

在 HyperOS / MIUI、OriginOS、ColorOS 等厂商系统上，原生 GKD 无障碍服务容易出现两类问题：
1. **被系统强杀**：内存紧张时系统 LMK (LowMemoryKiller) 会直接干掉无障碍进程；
2. **常规保活耗电严重**：传统的死循环轮询或常驻 WakeLock 导致 CPU 无法进入深度休眠，机身发热、异常掉电。

---

## 核心机制

### 1. 内核级 OOM -1000 免疫
- 无障碍服务启动连接（`onServiceConnected`）后，通过底层 Root 权限将应用进程的 `oom_score_adj` 直接置为 **`-1000`**。
- 提权至系统核心守护进程级别，内核层面豁免查杀。纯内核机制维护，0 额外 CPU 占用。

### 2. 事件驱动保活（无死循环轮询）
- 不常驻 CPU 唤醒锁，不跑后台死循环。
- 通过 `A11yKeepAliveReceiver` 监听亮屏与用户解锁事件（`ACTION_USER_PRESENT`），仅在触发时执行单次状态检查；若发现无障碍被系统误关则静默重新拉起，息屏期间手机 100% 正常深度休眠。

### 3. Root 静默授权
- 启动自动通过 Root 授予 `WRITE_SECURE_SETTINGS` 权限；
- 自动加入系统电池优化白名单与解除后台 AppOps 限制，无需电脑 ADB 调试授权。

### 4. 完整继承官方功能
- 完整保留官方规则订阅、高级选择器、规则调试与快照审查功能，不破坏原有业务逻辑。

---

## 安装说明

1. 前往 [Releases 页面](https://github.com/lifefuck/gkd-root-keepalive/releases) 下载最新 APK 安装；
2. 首次打开应用请授予 Root 权限，应用会自动配置底层防护策略与安全设置。

---

## 协议与署名

- 本项目基于 [gkd-kit/gkd](https://github.com/gkd-kit/gkd) 定制，遵循 **GPL-3.0** 开源协议。
- 定制分支维护：**life**
