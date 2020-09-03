package fp.yeyu.mcdata

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import io.netty.buffer.Unpooled
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
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
import java.io.File
import java.io.FileWriter
import kotlin.random.Random

/**
 * Tracked data:
 *  - block position
 *  - head rotation
 *  - cross-hair block
 *  - key press*
 *  - mouse press*
 *  - visible-mob
 *  - hot bar cursor
 *  - current gui inventories
 *  - visible blocks
 *
 * */
object PlayData : ModInitializer, ClientModInitializer {
    private val logRequest = Identifier("playdata", "requestlog")
    private val logSender = Identifier("playdata", "requestlog")
    private val logDestination by lazy(PlayData::createLogDestination)
    private val random = Random(System.currentTimeMillis())
    private const val logDirectory = "playdata-log"
    private val logDirectoryInstance = File(logDirectory).also {
        if (!it.exists() && it.mkdir()) println("created directory $logDirectory");
        else if (it.isFile) throw FileAlreadyExistsException(it, reason = "$logDirectory already exists and it is a file")
    }

    override fun onInitialize() {
        CommandRegistrationCallback.EVENT.register(PlayData::registerCommands)
        ServerSidePacketRegistryImpl.INSTANCE.register(logSender, PlayData::logPlayer)
    }

    private fun createLogDestination(): File = File(logDirectoryInstance, "${random.nextLong()}")

    private fun registerCommands(commandDispatcher: CommandDispatcher<ServerCommandSource>, isDedicated: Boolean) {
        commandDispatcher.register(CommandManager.literal("logeverything").executes(PlayData::requestInfoCommand))
    }

    private fun requestInfoCommand(context: CommandContext<out ServerCommandSource>): Int {
        val player = context.source.entity as ServerPlayerEntity?
                ?: return logAllPlayers(context.source.world as ServerWorld)
        requestInfo(player)
        return 1
    }

    private fun logAllPlayers(serverWorld: ServerWorld): Int {
        serverWorld.players.forEach { requestInfo(it) }
        return serverWorld.players.size
    }

    private fun requestInfo(it: ServerPlayerEntity) {
        ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(it, logRequest, PacketByteBuf(Unpooled.buffer()))
    }

    private fun logPlayer(context: PacketContext, packetByteBuf: PacketByteBuf) = logPlayer(context.player as ServerPlayerEntity, packetByteBuf)

    private fun logPlayer(player: ServerPlayerEntity, packetByteBuf: PacketByteBuf) {
        FileWriter(logDestination, true).use {
            it.write(
                    "\n",
                    "\n",
                    LogUtil.getTimeStamp(),
                    LogUtil.getUUID(player),
                    LogUtil.getBlockPosition(player),
                    LogUtil.getHeadRotation(player),
                    LogUtil.getCrossHairBlock(player),
                    LogUtil.getKeyPresses(packetByteBuf),
                    LogUtil.getVisibleMob(player),
                    LogUtil.getHotBarCursor(player),
                    LogUtil.getInventory(player),
                    LogUtil.getVisibleBlocks(player),
                    LogUtil.getEndStamp()
            )
        }
    }

    override fun onInitializeClient() {
        ClientSidePacketRegistryImpl.INSTANCE.register(logRequest, PlayData::onLogInfoRequest)
    }

    private fun onLogInfoRequest(context: PacketContext, packetByteBuf: PacketByteBuf) {
        ClientSidePacketRegistryImpl.INSTANCE.sendToServer(logSender, PacketByteBuf(Unpooled.buffer()).also(LogUtil::writeKeyPresses))
    }


}

private fun FileWriter.write(separator: String, end: String = "\n", vararg strings: String) {
    write(strings.joinToString(separator))
    write(end)
}
