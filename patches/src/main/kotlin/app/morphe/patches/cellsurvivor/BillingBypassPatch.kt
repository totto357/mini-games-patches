package app.morphe.patches.cellsurvivor

import app.morphe.patches.cellsurvivor.Constants.COMPATIBILITY_CELL_SURVIVOR
import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch

private const val GAME_HELPER = "Lcom/common/sdk/GameHelper;"
private const val PAY_MANAGER_COM = "Lcom/common/pay/PayManagerCom;"

internal object NewOrderInfoByGameStaticFingerprint : Fingerprint(
    definingClass = GAME_HELPER,
    name = "newOrderInfoByGameStatic",
    parameters = listOf("Ljava/lang/String;", "Ljava/lang/String;"),
)

internal object TrackPlatPayResultToServerFingerprint : Fingerprint(
    definingClass = PAY_MANAGER_COM,
    name = "trackPlatPayResultToServer",
    parameters = listOf("Ljava/lang/String;", "Ljava/lang/String;"),
)

internal object AddUploadServerCacheFingerprint : Fingerprint(
    definingClass = PAY_MANAGER_COM,
    name = "addUploadServerCache",
    parameters = listOf("Ljava/lang/String;"),
)

internal object GetAllUnFinishOrderIDFingerprint : Fingerprint(
    definingClass = PAY_MANAGER_COM,
    name = "getAllUnFinishOrderID",
    parameters = emptyList(),
)

internal object QueryUnFinishOrderFromServerFingerprint : Fingerprint(
    definingClass = PAY_MANAGER_COM,
    name = "queryUnFinishOrderFromServer",
    parameters = emptyList(),
)

@Suppress("unused")
val billingBypassPatch =
    bytecodePatch(
        name = "Free In-App Purchases",
        description = "Bypasses Google Play billing and immediately grants purchased diamonds/packs.",
    ) {
        compatibleWith(COMPATIBILITY_CELL_SURVIVOR)

        execute {
            NewOrderInfoByGameStaticFingerprint.method.ensureLocals(7)
            NewOrderInfoByGameStaticFingerprint.method.addInstructions(
                0,
                """
                const/4 v0, 0x1
                new-instance v1, Ljava/util/HashMap;
                invoke-direct {v1}, Ljava/util/HashMap;-><init>()V

                const-string v2, "productId"
                invoke-virtual {v1, v2, p0}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

                const-string v2, "platOrderNo"
                const-string v3, "test_plat_order"
                invoke-virtual {v1, v2, v3}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

                invoke-static {}, Ljava/util/UUID;->randomUUID()Ljava/util/UUID;
                move-result-object v3
                invoke-virtual {v3}, Ljava/util/UUID;->toString()Ljava/lang/String;
                move-result-object v3
                const-string v2, "payOrderNo"
                invoke-virtual {v1, v2, v3}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

                const-string v2, "orderSign"
                const-string v3, "test_order_sign"
                invoke-virtual {v1, v2, v3}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

                const-string v2, "token"
                const-string v3, "test_token"
                invoke-virtual {v1, v2, v3}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

                const-string v2, "quantity"
                const-string v3, "1"
                invoke-virtual {v1, v2, v3}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

                const-string v2, "purchaseType"
                const-string v3, "inapp"
                invoke-virtual {v1, v2, v3}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

                const/4 v4, 0x0
                invoke-static {}, Lcom/common/ad/PayManager;->getInstance()Lcom/common/ad/PayManager;
                move-result-object v5
                if-eqz v5, :cond_pay_end
                iget-object v6, v5, Lcom/common/ad/PayManager;->payHelper:Lcom/common/ad/GooglePayUtil;
                if-eqz v6, :cond_pay_end
                iget-object v2, v6, Lcom/common/ad/GooglePayUtil;->mConsumeCallback:Lcom/common/ad/GooglePayCallback;
                if-eqz v2, :cond_pay_end
                invoke-interface {v2, v0, v1, v4}, Lcom/common/ad/GooglePayCallback;->doServerResult(ZLjava/util/Map;Ljava/lang/Runnable;)V
                :cond_pay_end
                return-void
                """.trimIndent(),
            )

            TrackPlatPayResultToServerFingerprint.method.addInstructions(
                0,
                """
                return-void
                """.trimIndent(),
            )

            AddUploadServerCacheFingerprint.method.addInstructions(
                0,
                """
                return-void
                """.trimIndent(),
            )

            GetAllUnFinishOrderIDFingerprint.method.addInstructions(
                0,
                """
                const-string v0, ""
                return-object v0
                """.trimIndent(),
            )

            QueryUnFinishOrderFromServerFingerprint.method.addInstructions(
                0,
                """
                const/4 v0, 0x0
                return v0
                """.trimIndent(),
            )
        }
    }
