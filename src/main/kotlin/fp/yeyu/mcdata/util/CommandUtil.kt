package fp.yeyu.mcdata.util

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object CommandUtil {

    val logger: Logger = LogManager.getLogger()

    object Identifiers {
        val logByteLocal = Identifier("playdata", "logbyte")
        val logLocal = Identifier("playdata", "logstring")
        val publish = Identifier("playdata", "publish")
        val consume = Identifier("playdata", "consume")

        val ids = arrayOf(
                logByteLocal, logLocal, publish, consume
        )
    }

    fun initMain() {
        CommandRegistrationCallback.EVENT.register{ dispatcher, _ ->
            registerCommands(dispatcher)
        }
    }

    fun initClient() {
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.logLocal) { _, _ -> LogUtil.onLogLocalRequest() }
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.logByteLocal) { _, _ -> LogUtil.onLogByteLocalRequest() }
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.publish) { _, _ -> LogUtil.onPublishRequest() }
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.consume) { _, _ -> LogUtil.onConsumeRequest() }
    }

    private fun registerCommands(commandDispatcher: CommandDispatcher<ServerCommandSource>) {
        Identifiers.ids.forEach { id ->
            commandDispatcher
                    .register(CommandManager.literal(id.path)
                            .executes { context ->
                                request(id, context)
                            })

        }
    }

    private fun request(id: Identifier, context: CommandContext<out ServerCommandSource>): Int {
        val player = context.source.entity as ServerPlayerEntity?
        return if (player != null) {
            (player.world as ServerWorld).players.let {
                it.forEach { player ->
                    ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, id, PacketByteBuf(Unpooled.buffer()))
                }
                it.size
            }
        } else {
            ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, id, PacketByteBuf(Unpooled.buffer()))
            1
        }
    }
}