package app.morphe.patches.colonyflow

import app.morphe.patches.colonyflow.Constants.COMPATIBILITY_COLONY_FLOW
import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch

private const val MAX_UNITY_AD_MANAGER = "Lcom/applovin/mediation/unity/MaxUnityAdManager;"

internal object IsInterstitialReadyFingerprint : Fingerprint(
    definingClass = MAX_UNITY_AD_MANAGER,
    name = "isInterstitialReady",
    returnType = "Z",
)

internal object IsAppOpenAdReadyFingerprint : Fingerprint(
    definingClass = MAX_UNITY_AD_MANAGER,
    name = "isAppOpenAdReady",
    returnType = "Z",
)

@Suppress("unused")
val adBlockPatch =
    bytecodePatch(
        name = "Disable Interstitial & App Open Ads",
        description = "Bypasses interstitial ads and app-open ads by always returning false from readiness checks.",
    ) {
        compatibleWith(COMPATIBILITY_COLONY_FLOW)

        execute {
            IsInterstitialReadyFingerprint.method.addInstructions(
                0,
                """
                const/4 v0, 0x0
                return v0
                """.trimIndent(),
            )

            IsAppOpenAdReadyFingerprint.method.addInstructions(
                0,
                """
                const/4 v0, 0x0
                return v0
                """.trimIndent(),
            )
        }
    }
