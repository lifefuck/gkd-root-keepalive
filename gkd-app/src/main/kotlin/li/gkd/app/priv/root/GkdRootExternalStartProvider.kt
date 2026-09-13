package li.gkd.app.priv.root

import android.content.Context
import li.gkd.app.util.RootUtils
import priv.kit.ui.PrivilegeUiExternalStartSnapshot
import priv.kit.ui.PrivilegeUiStreamingExternalStartProvider
import priv.kit.core.PrivilegeStartupLogListener

/**
 * Root 方式的特权服务启动器
 * 提供给 priv-kit 界面与特权服务，允许通过直接执行 root shell 命令激活特权后台
 */
object GkdRootExternalStartProvider : PrivilegeUiStreamingExternalStartProvider {
    override val id: String = "root"
    override val label: CharSequence = "Root 强制激活"

    override suspend fun snapshot(context: Context): PrivilegeUiExternalStartSnapshot {
        val available = RootUtils.isRootAvailable()
        return if (available) {
            PrivilegeUiExternalStartSnapshot(
                available = true,
                authorized = true,
                uid = 0,
                version = 1,
                message = "Root 环境就绪",
            )
        } else {
            PrivilegeUiExternalStartSnapshot(
                available = false,
                message = "未检测到 Root 权限",
            )
        }
    }

    override suspend fun requestAuthorization(context: Context): PrivilegeUiExternalStartSnapshot {
        return snapshot(context)
    }

    override suspend fun start(context: Context, commandLine: String) {
        RootUtils.execRoot(commandLine)
    }

    override suspend fun start(
        context: Context,
        commandLine: String,
        startupLogListener: PrivilegeStartupLogListener,
    ) {
        RootUtils.execRoot(commandLine)
    }
}
