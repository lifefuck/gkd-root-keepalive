package li.gkd.app.util

import android.os.Process
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Root 权限与系统级保活工具类
 * 提供 Root 检测、静默授权写入安全设置、无障碍恢复、OOM 保护与防杀
 */
object RootUtils {

    /**
     * 检查系统是否存在 su 二进制文件或具备 root 权限
     */
    fun isRootAvailable(): Boolean {
        val paths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/data/local/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return runCatching {
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            process.waitFor() == 0
        }.getOrDefault(false)
    }

    /**
     * 执行 root shell 命令
     * @param command 命令内容
     * @return 执行是否成功 (exitCode == 0)
     */
    suspend fun execRoot(vararg commands: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val process = Runtime.getRuntime().exec("su")
            process.outputStream.bufferedWriter().use { writer ->
                for (cmd in commands) {
                    writer.write(cmd)
                    writer.newLine()
                }
                writer.write("exit")
                writer.newLine()
                writer.flush()
            }
            process.waitFor() == 0
        }.getOrElse {
            LogUtils.d(it)
            false
        }
    }

    /**
     * 授予 WRITE_SECURE_SETTINGS 权限并设为电池白名单
     * 仅在获得 root 权限时调用一次，纯事件触发，零耗电
     */
    suspend fun grantPermissionsSilently(packageName: String): Boolean {
        return execRoot(
            "pm grant $packageName android.permission.WRITE_SECURE_SETTINGS",
            "dumpsys deviceidle whitelist +$packageName"
        )
    }

    /**
     * 将当前 App 进程的 OOM 调整值设置为 -1000（系统级核心服务，不可被杀）
     * 0 耗电，纯内核级常驻保护
     */
    suspend fun applyOomProtection() {
        val pid = Process.myPid()
        execRoot(
            "echo -1000 > /proc/$pid/oom_score_adj",
            "echo -17 > /proc/$pid/oom_adj 2>/dev/null || true"
        )
    }

    /**
     * 通过 Root 强制唤醒并重新绑定无障碍服务
     * 避免系统因深睡将无障碍客户端解绑
     */
    suspend fun forceRestoreAccessibility(serviceComponent: String) {
        execRoot(
            "settings put secure accessibility_enabled 1",
            "settings put secure enabled_accessibility_services $serviceComponent",
            "cmd accessibility set-bind-instant-rules 2>/dev/null || true"
        )
    }
}
