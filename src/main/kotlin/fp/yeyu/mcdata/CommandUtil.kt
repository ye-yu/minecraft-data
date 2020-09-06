package fp.yeyu.mcdata

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
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
import java.io.FileWriter

@Suppress("UNUSED_PARAMETER")
object CommandUtil {

    val logger: Logger = LogManager.getLogger()

    object Identifiers {
        val logRequest = Identifier("playdata", "requestlog")
        val logLocal = Identifier("playdata", "loglocal")
        val logSender = Identifier("playdata", "sendlog")
    }

    fun initMain() {
        CommandRegistrationCallback.EVENT.register(CommandUtil::registerCommands)
        ServerSidePacketRegistryImpl.INSTANCE.register(Identifiers.logSender, Server::logPlayer)
    }

    fun initClient() {
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.logRequest, Client::onLogInfoRequest)
        ClientSidePacketRegistryImpl.INSTANCE.register(Identifiers.logLocal, Client::onLogLocalRequest)
    }

    private fun registerCommands(commandDispatcher: CommandDispatcher<ServerCommandSource>, isDedicated: Boolean) {
        commandDispatcher.register(CommandManager.literal("logeverything").executes(Server::requestInfoCommand))
        commandDispatcher.register(CommandManager.literal("loglocal").executes(Server::requestLogLocalCommand))
    }

    object Client {
        fun onLogInfoRequest(context: PacketContext, packetByteBuf: PacketByteBuf) =
                ClientSidePacketRegistryImpl.INSTANCE.sendToServer(Identifiers.logSender, PacketByteBuf(Unpooled.buffer()).also(AttributeUtil::writeKeyPresses))

        fun onLogLocalRequest(context: PacketContext, packetByteBuf: PacketByteBuf) = logPlayer()

        private fun logPlayer() {
            val player = MinecraftClient.getInstance().player ?: return

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

        fun requestLogLocalCommand(context: CommandContext<out ServerCommandSource>): Int {
            val player = context.source.entity as ServerPlayerEntity?
                    ?: return requestLogLocalAllPlayers(context.source.world as ServerWorld)
            requestLogLocal(player)
            return 1
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
            return 1
        }

        fun logPlayer(context: PacketContext, packetByteBuf: PacketByteBuf) = logPlayer(context.player as ServerPlayerEntity, packetByteBuf)

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

    private fun FileWriter.write(separator: String = "\n", end: String = "\n", vararg strings: String) {
        write(strings.joinToString(separator))
        write(end)
    }
}