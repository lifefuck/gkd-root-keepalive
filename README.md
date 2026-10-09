# GKD (Root Zero-Polling KeepAlive)

> **本项目为 GKD 官方项目 (gkd-kit/gkd) 的深度保活定制分支**  
> **分支维护者**：life  
> **基座版本**：GKD 官方最新发行版 v1.12.1  
> **下载地址**：可在本仓库的 [Releases 页面](https://github.com/lifefuck/gkd-root-keepalive/releases) 直接下载打包完成的 APK 安装包（**`v1.12.1-keepalive`**）。

---

## 💡 为什么需要本项目？

在原生 Android 以及各大厂商定制系统（小米 HyperOS/MIUI、vivo OriginOS、OPPO ColorOS、华为鸿蒙等）上，原版 GKD 的无障碍服务经常面临以下痛点：
1. **频繁被杀**：系统在内存紧张时触发 LowMemoryKiller (LMK) 强制回收无障碍进程；
2. **死循环轮询耗电**：常规保活方案依赖 `while(true)` 频繁唤醒 CPU，导致手机异常发热、无法进入深度睡眠（Deep Sleep）；
3. **权限意外丢失**：系统更新或智能省电经常重置无障碍授权，需要反复手动开启或连接电脑授权。

本项目通过深度整合 **Root 底层能力**，实现了真正的**零额外能耗、零死循环轮询、常驻免查杀**。

---

## ⚡ 核心功能与技术实现

### 1. 内核级 OOM -1000 防杀保护
- 无障碍服务启动连接（`onServiceConnected`）后，通过底层 Root 权限直接将当前应用进程的 `oom_score_adj` 设置为 **`-1000`**。
- 由 Linux 内核底层直接豁免，进程优先级提升至系统级核心守护进程级别，在内存极度紧张时绝对免遭系统杀后台。
- **纯内核机制维护，0 额外 CPU 占用，0 耗电**。

### 2. 纯事件驱动保活（杜绝死循环）
- 坚决杜绝死循环后台轮询，不常驻 CPU 唤醒锁。
- 引入 `A11yKeepAliveReceiver`，仅在系统关键生命周期事件（如亮屏、解锁、用户呈现 `ACTION_USER_PRESENT`）时单次触发状态检查；若检测到系统意外关闭无障碍，立即通过底层静默重新激活，息屏时保持 100% 深度休眠。

### 3. 免电脑一键静默授权
- 启动时自动通过 Root 静默授予 `WRITE_SECURE_SETTINGS` 权限；
- 自动加入系统电池优化白名单（Doze 模式豁免）；
- 自动解除系统的后台运行限制（AppOps），无需再连电脑通过 ADB 手动敲命令。

### 4. 完整保留官方高级功能
- 100% 完整继承 GKD 官方高级选择器、第三方规则订阅、规则调试与快照审查功能。

---

## 📦 下载与安装

请前往 [Releases 页面](https://github.com/lifefuck/gkd-root-keepalive/releases) 获取最新预编译安装包：
- **`GKD_Root_KeepAlive_v1.12.1.apk`**

> **注意**：首次安装或运行后，请授予应用 Root 权限，应用将自动完成底层常驻防护与权限配置。

---

## 📄 开源与免责声明

- 本项目基于 [gkd-kit/gkd](https://github.com/gkd-kit/gkd) 二次开发，严格遵循 **[GPL-3.0-only](LICENSE)** 开源协议。
- 本项目仅供个人学习、自动化体验与技术探索使用，请勿用于非法用途。
