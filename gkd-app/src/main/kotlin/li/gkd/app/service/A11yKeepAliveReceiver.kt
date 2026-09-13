package li.gkd.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import li.gkd.app.appScope
import li.gkd.app.store.AppStore.storeFlow
import li.gkd.app.util.LogUtils
import li.gkd.app.util.RootUtils

/**
 * 屏幕解锁 / 电源恢复广播接收器
 * 核心原理：纯事件驱动，绝不写死循环！
 * 仅当用户亮屏、解锁（USER_PRESENT）时被动触发一次检查，
 * 若无障碍服务被系统杀死，则瞬时通过 Root 唤醒并恢复，平时完全休眠，0 额外耗电。
 */
class A11yKeepAliveReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        LogUtils.d("A11yKeepAliveReceiver received action: $action")

        // 仅在用户启用了无障碍自启，且当前服务未在运行状态时触发轻量自愈
        if (storeFlow.value.enableAutomator && currentAppUseA11y && !A11yService.isRunning.value) {
            appScope.launch(Dispatchers.IO) {
                if (RootUtils.isRootAvailable()) {
                    // 应用 OOM -1000 保护
                    RootUtils.applyOomProtection()
                    // 恢复无障碍注册并唤醒
                    RootUtils.forceRestoreAccessibility(A11yService.a11yCn.flattenToString())
                }
                // 触发内部状态刷新
                fixRestartAutomatorService()
            }
        }
    }
}
