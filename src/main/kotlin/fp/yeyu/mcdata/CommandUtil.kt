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
            FileWriter(FileUtil.logDestination, true).use {
                it.write(
                        strings = arrayOf(
                                "Log local",
                                AttributeUtil.getTimeStamp().also { logger.info("Writing timestamp") },
                                AttributeUtil.getUUID(player).also { logger.info("Writing uuid") },
                                AttributeUtil.getBlockPosition(player).also { logger.info("Writing blockPos") },
                                AttributeUtil.getHeadRotation(player).also { logger.info("Writing headRotation") },
                                AttributeUtil.getCrossHairBlock(player).also { logger.info("Writing crossHairBlock") },
                                AttributeUtil.getKeyPresses().also { logger.info("Writing key presses") },
                                AttributeUtil.getVisibleMob(player).also { logger.info("Writing visible mobs") },
                                AttributeUtil.getHotBarCursor(player).also { logger.info("Writing hot bar cursor") },
                                AttributeUtil.getInventory(player).also { logger.info("Writing inventory") },
                                AttributeUtil.getVisibleBlocks(player).also { logger.info("Writing visible blocks") },
                                AttributeUtil.getEndStamp().also { logger.info("Writing end stamp") },)
                )
            }
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
            FileWriter(FileUtil.logDestination, true).use {
                it.write(
                        strings = arrayOf(
                                "Log server",
                                AttributeUtil.getTimeStamp(),
                                AttributeUtil.getUUID(player),
                                AttributeUtil.getBlockPosition(player),
                                AttributeUtil.getHeadRotation(player),
                                AttributeUtil.getCrossHairBlock(player),
                                AttributeUtil.getKeyPresses(packetByteBuf),
                                AttributeUtil.getVisibleMob(player),
                                AttributeUtil.getHotBarCursor(player),
                                AttributeUtil.getInventory(player),
                                AttributeUtil.getVisibleBlocks(player),
                                AttributeUtil.getEndStamp())
                )
            }
        }

    }

    private fun FileWriter.write(separator: String = "\n", end: String = "\n", vararg strings: String) {
        write(strings.joinToString(separator))
        write(end)
    }
}