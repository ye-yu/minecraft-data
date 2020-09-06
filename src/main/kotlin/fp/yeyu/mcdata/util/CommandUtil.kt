package fp.yeyu.mcdata.util

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.network.PacketContext
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
        val logRequest = Identifier("playdata", "requestlog")
        val logByteLocal = Identifier("playdata", "logbytelocal")
        val logByteRequest = Identifier("playdata", "requestlogbyte")
        val logLocal = Identifier("playdata", "loglocal")
        val logSender = Identifier("playdata", "sendlog")
        val logByteSender = Identifier("playdata", "sendbytelog")
    }

    fun initMain() {
        CommandRegistrationCallback.EVENT.register(CommandUtil::registerCommands)
        ServerSidePacketRegistryImpl.INSTANCE.register(Identifiers.logSender, LogUtil.Server::logPlayer)
        ServerSidePacketRegistryImpl.INSTANCE.register(Identifiers.logByteSender, LogUtil.Server::logBytePlayer)
    }

    fun initClient() {
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.logRequest, Client::onLogInfoRequest)
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.logByteRequest, Client::onLogByteInfoRequest)
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.logLocal, LogUtil.Client::onLogLocalRequest)
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.logByteLocal, LogUtil.Client::onLogByteLocalRequest)
    }

    private fun registerCommands(commandDispatcher: CommandDispatcher<ServerCommandSource>, isDedicated: Boolean) {
        commandDispatcher.register(CommandManager.literal("log").executes(Server::requestInfoCommand))
        commandDispatcher.register(CommandManager.literal("logbyte").executes(Server::requestByteInfoCommand))
        commandDispatcher.register(CommandManager.literal("loglocal").executes(Server::requestLogLocalCommand))
        commandDispatcher.register(CommandManager.literal("logbytelocal").executes(Server::requestLogByteLocalCommand))
    }

    object Client {
        fun onLogInfoRequest(context: PacketContext, packetByteBuf: PacketByteBuf) =
                ClientSidePacketRegistryImpl.INSTANCE.sendToServer(Identifiers.logSender, PacketByteBuf(Unpooled.buffer()).also(StringAttributeUtil::writeKeyPresses))

        fun onLogByteInfoRequest(context: PacketContext, packetByteBuf: PacketByteBuf) =
                ClientSidePacketRegistryImpl.INSTANCE.sendToServer(Identifiers.logByteSender, PacketByteBuf(Unpooled.buffer()).also(ByteAttributeUtil::writeKeyPressesEnum))
    }


    object Server {
        fun requestInfoCommand(context: CommandContext<out ServerCommandSource>): Int {
            val player = context.source.entity as ServerPlayerEntity?
                    ?: return requestInfoAllPlayers(context.source.world as ServerWorld)
            requestInfo(player)
            return 1
        }

        fun requestByteInfoCommand(context: CommandContext<out ServerCommandSource>): Int {
            val player = context.source.entity as ServerPlayerEntity?
                    ?: return requestByteInfoAllPlayers(context.source.world as ServerWorld)
            requestByteInfo(player)
            return 1
        }

        private fun requestByteInfoAllPlayers(serverWorld: ServerWorld): Int {
            serverWorld.players.forEach(this::requestByteInfo)
            return serverWorld.players.size
        }

        private fun requestByteInfo(player: ServerPlayerEntity) {
            ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, Identifiers.logByteRequest, PacketByteBuf(Unpooled.buffer()))
        }

        fun requestLogLocalCommand(context: CommandContext<out ServerCommandSource>): Int {
            val player = context.source.entity as ServerPlayerEntity?
                    ?: return requestLogLocalAllPlayers(context.source.world as ServerWorld)
            requestLogLocal(player)
            return 1
        }

        fun requestLogByteLocalCommand(context: CommandContext<out ServerCommandSource>): Int {
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


        private fun requestInfo(it: ServerPlayerEntity) {
            ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(it, Identifiers.logRequest, PacketByteBuf(Unpooled.buffer()))
        }

        private fun requestInfoAllPlayers(serverWorld: ServerWorld): Int {
            serverWorld.players.forEach(this::requestInfo)
            return serverWorld.players.size
        }

        private fun requestLogLocal(player: ServerPlayerEntity) {
            ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, Identifiers.logLocal, PacketByteBuf(Unpooled.buffer()))
        }

        private fun requestLogLocalAllPlayers(serverWorld: ServerWorld): Int {
            serverWorld.players.forEach(this::requestLogLocal)
            return serverWorld.players.size
        }
    }
}