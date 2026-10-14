package app.morphe.patches.colonyflow

import app.morphe.patches.colonyflow.Constants.COMPATIBILITY_COLONY_FLOW
import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.indexOfFirstInstruction
import com.android.tools.smali.dexlib2.Opcode

private const val PAIRIP_APPLICATION = "Lcom/pairip/application/Application;"

internal object ApplicationAttachBaseContextFingerprint : Fingerprint(
    definingClass = PAIRIP_APPLICATION,
    name = "attachBaseContext",
    parameters = listOf("Landroid/content/Context;"),
)

internal object ApplicationOnCreateFingerprint : Fingerprint(
    definingClass = PAIRIP_APPLICATION,
    name = "onCreate",
    parameters = emptyList(),
)

@Suppress("unused")
val licenseAndPrefsPatch =
    bytecodePatch(
        name = "Bypass License Check & Pre-inject NoAds",
        description = "Removes pairip license check and automatically sets key_local_remove_ads in PlayerPrefs.",
    ) {
        compatibleWith(COMPATIBILITY_COLONY_FLOW)

        execute {
            ApplicationAttachBaseContextFingerprint.method.addInstructions(
                0,
                """
                invoke-super {p0, p1}, Landroid/app/Application;->attachBaseContext(Landroid/content/Context;)V
                return-void
                """.trimIndent(),
            )

            ApplicationOnCreateFingerprint.method.apply {
                val superInvokeIndex = indexOfFirstInstruction(Opcode.INVOKE_SUPER)
                val targetIndex = if (superInvokeIndex != -1) superInvokeIndex + 1 else 0

                addInstructions(
                    targetIndex,
                    """
                    const-string v0, "com.abi.colony.flow.v2.playerprefs"
                    const/4 v1, 0x0
                    invoke-virtual {p0, v0, v1}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;
                    move-result-object v0
                    if-eqz v0, :cond_noads_end
                    invoke-interface {v0}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences${'$'}Editor;
                    move-result-object v0
                    if-eqz v0, :cond_noads_end
                    const-string v1, "key_local_remove_ads"
                    const/4 v2, 0x1
                    invoke-interface {v0, v1, v2}, Landroid/content/SharedPreferences${'$'}Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences${'$'}Editor;
                    move-result-object v0
                    invoke-interface {v0}, Landroid/content/SharedPreferences${'$'}Editor;->apply()V
                    :cond_noads_end
                    """.trimIndent(),
                )
            }
        }
    }
