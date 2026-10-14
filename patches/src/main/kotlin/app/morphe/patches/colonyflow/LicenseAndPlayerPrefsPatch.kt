package app.morphe.patches.colonyflow

import app.morphe.patches.colonyflow.Constants.COMPATIBILITY_COLONY_FLOW
import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch

private const val PAIRIP_LICENSE_CLIENT = "Lcom/pairip/licensecheck/LicenseClient;"

internal object LicenseClientCheckLicenseFingerprint : Fingerprint(
    definingClass = PAIRIP_LICENSE_CLIENT,
    name = "checkLicense",
    parameters = listOf("Landroid/content/Context;"),
)

@Suppress("unused")
val licenseAndPrefsPatch =
    bytecodePatch(
        name = "Bypass License Check",
        description = "Removes pairip license check by returning immediately from checkLicense.",
    ) {
        compatibleWith(COMPATIBILITY_COLONY_FLOW)

        execute {
            LicenseClientCheckLicenseFingerprint.method.addInstructions(
                0,
                """
                return-void
                """.trimIndent(),
            )
        }
    }
