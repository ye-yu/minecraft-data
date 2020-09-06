package fp.yeyu.mcdata

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import fp.yeyu.mcdata.data.EncodingKey
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.FileWriter

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
        ServerSidePacketRegistryImpl.INSTANCE.register(Identifiers.logSender, Server::logPlayer)
        ServerSidePacketRegistryImpl.INSTANCE.register(Identifiers.logByteSender, Server::logBytePlayer)
    }

    fun initClient() {
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.logRequest, Client::onLogInfoRequest)
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.logByteRequest, Client::onLogByteInfoRequest)
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.logLocal, Client::onLogLocalRequest)
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.logByteLocal, Client::onLogByteLocalRequest)
    }

    private fun registerCommands(commandDispatcher: CommandDispatcher<ServerCommandSource>, isDedicated: Boolean) {
        commandDispatcher.register(CommandManager.literal("log").executes(Server::requestInfoCommand))
        commandDispatcher.register(CommandManager.literal("logbyte").executes(Server::requestByteInfoCommand))
        commandDispatcher.register(CommandManager.literal("loglocal").executes(Server::requestLogLocalCommand))
        commandDispatcher.register(CommandManager.literal("logbytelocal").executes(Server::requestLogByteLocalCommand))
    }

    object Client {
        fun onLogInfoRequest(context: PacketContext, packetByteBuf: PacketByteBuf) =
                ClientSidePacketRegistryImpl.INSTANCE.sendToServer(Identifiers.logSender, PacketByteBuf(Unpooled.buffer()).also(AttributeUtil::writeKeyPresses))

        fun onLogByteInfoRequest(context: PacketContext, packetByteBuf: PacketByteBuf) =
                ClientSidePacketRegistryImpl.INSTANCE.sendToServer(Identifiers.logByteSender, PacketByteBuf(Unpooled.buffer()).also(AttributeUtil::writeKeyPressesByte))

        fun onLogLocalRequest(context: PacketContext, packetByteBuf: PacketByteBuf) = logPlayer()

        fun onLogByteLocalRequest(context: PacketContext, packetByteBuf: PacketByteBuf) = logByte()

        private fun logByte() {
//            val player = MinecraftClient.getInstance().player ?: return
            val byteBuf = PacketByteBuf(Unpooled.buffer())
            EncodingKey.LOCAL.serialize(byteBuf)
            AttributeUtil.writeKeyPressesByte(byteBuf)
            EncodingKey.END.serialize(byteBuf)
            BufferedOutputStream(FileOutputStream(FileUtil.logDestinationByte)).use {
                it.write(byteBuf.array())
            }
        }

        private fun logPlayer() {
            val player = MinecraftClient.getInstance().player ?: return

            // todo: replace with `use`
            FileWriter(FileUtil.logDestination, true).also {
                try {
                    it.write("Log local")
                    it.write("\n")
                    logger.info("Writing timestamp")
                    it.write(AttributeUtil.getTimeStamp())
                    it.write("\n")
                    logger.info("Writing uuid")
                    it.write(AttributeUtil.getUUID(player))
                    it.write("\n")
                    logger.info("Writing blockPos")
                    it.write(AttributeUtil.getBlockPosition(player))
                    it.write("\n")
                    logger.info("Writing headRotation")
                    it.write(AttributeUtil.getHeadRotation(player))
                    it.write("\n")
                    logger.info("Writing crossHairBlock")
                    it.write(AttributeUtil.getCrossHairBlock(player))
                    it.write("\n")
                    logger.info("Writing key presses")
                    it.write(AttributeUtil.getKeyPresses())
                    it.write("\n")
                    logger.info("Writing visible mobs")
                    it.write(AttributeUtil.getVisibleMob(player))
                    it.write("\n")
                    logger.info("Writing hot bar cursor")
                    it.write(AttributeUtil.getHotBarCursor(player))
                    it.write("\n")
                    logger.info("Writing inventory")
                    it.write(AttributeUtil.getInventory(player))
                    it.write("\n")
                    logger.info("Writing visible blocks")
                    it.write(AttributeUtil.getVisibleBlocks(player))
                    it.write("\n")
                    logger.info("Writing end stamp")
                    it.write(AttributeUtil.getEndStamp())
                    it.write("\n")
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }.close()
        }


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

        fun logPlayer(context: PacketContext, packetByteBuf: PacketByteBuf) = logPlayer(context.player as ServerPlayerEntity, packetByteBuf)

        fun logBytePlayer(context: PacketContext, packetByteBuf: PacketByteBuf) = logPlayerByte(context.player as ServerPlayerEntity, packetByteBuf)

        private fun logPlayerByte(player: ServerPlayerEntity, packetByteBuf: PacketByteBuf) {
            val byteBuf = PacketByteBuf(Unpooled.buffer())
            EncodingKey.SERVER.serialize(byteBuf)
            AttributeUtil.writeKeyPressesByte(byteBuf)
            EncodingKey.END.serialize(byteBuf)

            BufferedOutputStream(FileOutputStream(FileUtil.logDestinationByte)).use {
                it.write(byteBuf.array())
            }
        }

        private fun logPlayer(player: ServerPlayerEntity, packetByteBuf: PacketByteBuf) {
            FileWriter(FileUtil.logDestination, true).also {
                try {
                    it.write("Log server")
                    it.write("\n")
                    logger.info("Writing timestamp")
                    it.write(AttributeUtil.getTimeStamp())
                    it.write("\n")
                    logger.info("Writing uuid")
                    it.write(AttributeUtil.getUUID(player))
                    it.write("\n")
                    logger.info("Writing blockPos")
                    it.write(AttributeUtil.getBlockPosition(player))
                    it.write("\n")
                    logger.info("Writing headRotation")
                    it.write(AttributeUtil.getHeadRotation(player))
                    it.write("\n")
                    logger.info("Writing crossHairBlock")
                    it.write(AttributeUtil.getCrossHairBlock(player))
                    it.write("\n")
                    logger.info("Writing key presses")
                    it.write(AttributeUtil.getKeyPresses(packetByteBuf))
                    it.write("\n")
                    logger.info("Writing visible mobs")
                    it.write(AttributeUtil.getVisibleMob(player))
                    it.write("\n")
                    logger.info("Writing hot bar cursor")
                    it.write(AttributeUtil.getHotBarCursor(player))
                    it.write("\n")
                    logger.info("Writing inventory")
                    it.write(AttributeUtil.getInventory(player))
                    it.write("\n")
                    logger.info("Writing visible blocks")
                    it.write(AttributeUtil.getVisibleBlocks(player))
                    it.write("\n")
                    logger.info("Writing end stamp")
                    it.write(AttributeUtil.getEndStamp())
                    it.write("\n")
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }.close()
        }
    }
}