package app.morphe.patches.cellsurvivor

import app.morphe.patches.cellsurvivor.Constants.COMPATIBILITY_CELL_SURVIVOR
import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch

private const val GOOGLE_GAME_SERVICE_HELPER = "Lc/w/k/lt0;"
private const val BUGLY_HELPER = "Lc/w/k/sk;"
private const val APPS_FLYER_COMMON = "Lcom/common/third/manager/AppsFlyerCommon;"
private const val PAY_STATISTIC_UTIL = "Lcom/common/pay/PayStatisticUtil;"
private const val ONLINE_CONFIG_TASK = "Lcom/common/tasks/OnlineConfigTask;"

internal object PlayGamesSignInFingerprint : Fingerprint(
    definingClass = GOOGLE_GAME_SERVICE_HELPER,
    name = "q",
    parameters = listOf("Z"),
)

internal object PlayGamesToastFingerprint : Fingerprint(
    definingClass = GOOGLE_GAME_SERVICE_HELPER,
    name = "LQuG",
    parameters = emptyList(),
)

internal object BuglyInitFingerprint : Fingerprint(
    definingClass = BUGLY_HELPER,
    name = "a",
    parameters = listOf("Landroid/content/Context;", "Ljava/lang/String;", "Z"),
)

internal object AppsFlyerInitFingerprint : Fingerprint(
    definingClass = APPS_FLYER_COMMON,
    name = "initSDK",
    parameters = listOf("Landroid/app/Application;"),
)

internal object PayStatisticOnSucceedFingerprint : Fingerprint(
    definingClass = PAY_STATISTIC_UTIL,
    name = "onSucceedEvent",
    parameters = listOf("Ljava/lang/String;"),
)

internal object OnlineConfigTaskRunFingerprint : Fingerprint(
    definingClass = ONLINE_CONFIG_TASK,
    name = "run",
    parameters = emptyList(),
)

@Suppress("unused")
val offlinePatch =
    bytecodePatch(
        name = "Disable Analytics, Crashlytics & Error Toasts",
        description = "Completely disables AppsFlyer, Bugly, OnlineConfig and annoying Google Play Games network toasts.",
    ) {
        compatibleWith(COMPATIBILITY_CELL_SURVIVOR)

        execute {
            PlayGamesSignInFingerprint.method.addInstructions(
                0,
                """
                return-void
                """.trimIndent(),
            )

            PlayGamesToastFingerprint.method.addInstructions(
                0,
                """
                return-void
                """.trimIndent(),
            )

            BuglyInitFingerprint.method.addInstructions(
                0,
                """
                return-void
                """.trimIndent(),
            )

            AppsFlyerInitFingerprint.method.addInstructions(
                0,
                """
                return-void
                """.trimIndent(),
            )

            PayStatisticOnSucceedFingerprint.method.addInstructions(
                0,
                """
                return-void
                """.trimIndent(),
            )

            OnlineConfigTaskRunFingerprint.method.addInstructions(
                0,
                """
                return-void
                """.trimIndent(),
            )
        }
    }
