package app.morphe.patches.cellsurvivor

import app.morphe.patches.cellsurvivor.Constants.COMPATIBILITY_CELL_SURVIVOR
import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch

private const val GAME_ACT_HELPER = "Lcom/common/game/GameActHelper;"
private const val GAME_ACT_HELPER_PULL_CALLBACK = "Lcom/common/game/GameActHelper$67;"

internal object UploadCloudArchiveBatchFingerprint : Fingerprint(
    definingClass = GAME_ACT_HELPER,
    name = "uploadCloudArchiveBatch",
    parameters = listOf("Ljava/lang/String;", "Ljava/lang/String;"),
)

internal object PullCloudArchiveBatchCallbackFingerprint : Fingerprint(
    definingClass = GAME_ACT_HELPER_PULL_CALLBACK,
    name = "onResult",
    parameters = listOf("I", "Ljava/lang/String;", "Ljava/lang/String;", "Ljava/lang/String;"),
)

@Suppress("unused")
val saveDataPatch =
    bytecodePatch(
        name = "Save Data Backup & Restore",
        description = "Automatically backs up and restores save data from Android/media directory across installs.",
    ) {
        compatibleWith(COMPATIBILITY_CELL_SURVIVOR)

        execute {
            UploadCloudArchiveBatchFingerprint.method.addInstructions(
                0,
                """
                :try_start_save
                new-instance v0, Ljava/io/File;
                const-string v1, "/sdcard/Android/media/defense.roguelike.cell.shoot.survivor"
                invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V
                invoke-virtual {v0}, Ljava/io/File;->exists()Z
                move-result v1
                if-nez v1, :cond_dir_ok
                invoke-virtual {v0}, Ljava/io/File;->mkdirs()Z
                :cond_dir_ok

                new-instance v1, Ljava/io/File;
                const-string v2, "save_backup.json"
                invoke-direct {v1, v0, v2}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V

                if-eqz p1, :cond_save_end
                invoke-virtual {p1}, Ljava/lang/String;->getBytes()[B
                move-result-object v0

                new-instance v2, Ljava/io/FileOutputStream;
                invoke-direct {v2, v1}, Ljava/io/FileOutputStream;-><init>(Ljava/io/File;)V
                invoke-virtual {v2, v0}, Ljava/io/FileOutputStream;->write([B)V
                invoke-virtual {v2}, Ljava/io/FileOutputStream;->close()V
                :try_end_save
                .catchall {:try_start_save .. :try_end_save} :catch_save
                :catch_save
                :cond_save_end
                """.trimIndent(),
            )

            PullCloudArchiveBatchCallbackFingerprint.method.addInstructions(
                0,
                """
                :try_start_load
                if-eqz p4, :cond_do_load
                invoke-virtual {p4}, Ljava/lang/String;->isEmpty()Z
                move-result v0
                if-nez v0, :cond_do_load
                const-string v0, "{}"
                invoke-virtual {v0, p4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v0
                if-eqz v0, :cond_load_end

                :cond_do_load
                new-instance v0, Ljava/io/File;
                const-string v1, "/sdcard/Android/media/defense.roguelike.cell.shoot.survivor/save_backup.json"
                invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V
                invoke-virtual {v0}, Ljava/io/File;->exists()Z
                move-result v1
                if-eqz v1, :cond_load_end

                invoke-virtual {v0}, Ljava/io/File;->length()J
                move-result-wide v1
                long-to-int v1, v1
                new-array v1, v1, [B

                new-instance v2, Ljava/io/FileInputStream;
                invoke-direct {v2, v0}, Ljava/io/FileInputStream;-><init>(Ljava/io/File;)V
                invoke-virtual {v2, v1}, Ljava/io/FileInputStream;->read([B)I
                invoke-virtual {v2}, Ljava/io/FileInputStream;->close()V

                new-instance p4, Ljava/lang/String;
                invoke-direct {p4, v1}, Ljava/lang/String;-><init>([B)V
                const/4 p1, 0x1
                :try_end_load
                .catchall {:try_start_load .. :try_end_load} :catch_load
                :catch_load
                :cond_load_end
                """.trimIndent(),
            )
        }
    }
