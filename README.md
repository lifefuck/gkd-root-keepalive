# GKD (Root KeepAlive 定制分支)

基于 GKD 官方 v1.12.1 定制，主要解决在 HyperOS、OriginOS 等系统上无障碍服务经常被系统杀死或掉权限的问题。

APK 下载：[GitHub Releases](https://github.com/lifefuck/gkd-root-keepalive/releases)

## 改动内容

- **防杀**：无障碍服务启动后，通过 Root 将进程 `oom_score_adj` 设为 `-1000`，防止内存紧张时被系统杀死。
- **唤醒检查**：不挂后台唤醒锁也不跑死循环，仅在用户亮屏或解锁时检测一次服务状态，若被误关则静默拉起，息屏正常休眠。
- **自动授权**：首次启动给 Root 后，自动通过 Root 申请安全设置权限并加入电池优化白名单，不用连电脑输 ADB 命令。
- **功能完整**：完全保留官方规则订阅、选择器和审查功能，不影响原有业务。

## 使用

1. 在 Releases 下载 APK 安装。
2. 打开应用授予 Root 权限即可。

## 说明

本项目基于 [gkd-kit/gkd](https://github.com/gkd-kit/gkd) 定制，遵循 GPL-3.0 协议。
