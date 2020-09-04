package fp.yeyu.mcdata

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import io.netty.buffer.Unpooled
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
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
import java.io.FileWriter

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
    override fun onInitialize() {
        CommandUtil.initMain()
    }

    override fun onInitializeClient() {
        CommandUtil.initClient()
    }

}
