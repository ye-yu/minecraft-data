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

@Suppress("UNUSED_PARAMETER")
object CommandUtil {

    val logger: Logger = LogManager.getLogger()

    object Identifiers {
        val logByteLocal = Identifier("playdata", "logbyte")
        val logLocal = Identifier("playdata", "logstring")
    }

    fun initMain() {
        CommandRegistrationCallback.EVENT.register(CommandUtil::registerCommands)
    }

    fun initClient() {
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.logLocal, LogUtil::onLogLocalRequest)
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.logByteLocal, LogUtil::onLogByteLocalRequest)
    }

    private fun registerCommands(commandDispatcher: CommandDispatcher<ServerCommandSource>, isDedicated: Boolean) {
        commandDispatcher.register(CommandManager.literal("logstring").executes(CommandUtil::requestLogLocalCommand))
        commandDispatcher.register(CommandManager.literal("logbyte").executes(CommandUtil::requestLogByteLocalCommand))
    }

    private fun requestLogLocalCommand(context: CommandContext<out ServerCommandSource>): Int {
        val player = context.source.entity as ServerPlayerEntity?
                ?: return requestLogLocalAllPlayers(context.source.world as ServerWorld)
        requestLogLocal(player)
        return 1
    }

    private fun requestLogByteLocalCommand(context: CommandContext<out ServerCommandSource>): Int {
        val player = context.source.entity as ServerPlayerEntity?
                ?: return requestLogByteLocalAllPlayers(context.source.world as ServerWorld)
        requestLogByteLocal(player)
        return 1
    }

    private fun requestLogByteLocalAllPlayers(serverWorld: ServerWorld): Int {
        serverWorld.players.forEach(this::requestLogByteLocal)
        return serverWorld.players.size
    }

    private fun requestLogByteLocal(player: ServerPlayerEntity) {
        ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, Identifiers.logByteLocal, PacketByteBuf(Unpooled.buffer()))
    }


    private fun requestLogLocal(player: ServerPlayerEntity) {
        ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, Identifiers.logLocal, PacketByteBuf(Unpooled.buffer()))
    }

    private fun requestLogLocalAllPlayers(serverWorld: ServerWorld): Int {
        serverWorld.players.forEach(this::requestLogLocal)
        return serverWorld.players.size
    }
}