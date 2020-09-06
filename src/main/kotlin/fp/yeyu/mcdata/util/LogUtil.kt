package fp.yeyu.mcdata.util

import fp.yeyu.mcdata.data.EncodingKey
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.FileWriter

@Suppress("UNUSED_PARAMETER")
object LogUtil {
    object Server {
        fun logPlayer(context: PacketContext, packetByteBuf: PacketByteBuf) = logPlayer(context.player as ServerPlayerEntity, packetByteBuf)

        fun logBytePlayer(context: PacketContext, packetByteBuf: PacketByteBuf) = logPlayerByte(context.player as ServerPlayerEntity, packetByteBuf)

        private fun logPlayerByte(player: ServerPlayerEntity, packetByteBuf: PacketByteBuf) {
            val byteBuf = PacketByteBuf(Unpooled.buffer())
            EncodingKey.SERVER.serialize(byteBuf)
            StringAttributeUtil.writeKeyPressesByte(byteBuf)
            EncodingKey.END.serialize(byteBuf)

            BufferedOutputStream(FileOutputStream(FileUtil.logDestinationByte)).use {
                it.write(byteBuf.array())
            }
        }

        private fun logPlayer(player: ServerPlayerEntity, packetByteBuf: PacketByteBuf) {
            FileWriter(FileUtil.logDestination, true).use {
                it.write("Log server")
                it.write("\n")
                CommandUtil.logger.info("Writing timestamp")
                it.write(StringAttributeUtil.getTimeStamp())
                it.write("\n")
                CommandUtil.logger.info("Writing uuid")
                it.write(StringAttributeUtil.getUUID(player))
                it.write("\n")
                CommandUtil.logger.info("Writing blockPos")
                it.write(StringAttributeUtil.getBlockPosition(player))
                it.write("\n")
                CommandUtil.logger.info("Writing headRotation")
                it.write(StringAttributeUtil.getHeadRotation(player))
                it.write("\n")
                CommandUtil.logger.info("Writing crossHairBlock")
                it.write(StringAttributeUtil.getCrossHairBlock(player))
                it.write("\n")
                CommandUtil.logger.info("Writing key presses")
                it.write(StringAttributeUtil.getKeyPresses(packetByteBuf))
                it.write("\n")
                CommandUtil.logger.info("Writing visible mobs")
                it.write(StringAttributeUtil.getVisibleMob(player))
                it.write("\n")
                CommandUtil.logger.info("Writing hot bar cursor")
                it.write(StringAttributeUtil.getHotBarCursor(player))
                it.write("\n")
                CommandUtil.logger.info("Writing inventory")
                it.write(StringAttributeUtil.getInventory(player))
                it.write("\n")
                CommandUtil.logger.info("Writing visible blocks")
                it.write(StringAttributeUtil.getVisibleBlocks(player))
                it.write("\n")
                CommandUtil.logger.info("Writing end stamp")
                it.write(StringAttributeUtil.getEndStamp())
                it.write("\n")
            }
        }
    }

    object Client {

        fun onLogLocalRequest(context: PacketContext, packetByteBuf: PacketByteBuf) = logPlayer()

        fun onLogByteLocalRequest(context: PacketContext, packetByteBuf: PacketByteBuf) = logByte()

        private fun logByte() {
//            val player = MinecraftClient.getInstance().player ?: return
            val byteBuf = PacketByteBuf(Unpooled.buffer())
            EncodingKey.LOCAL.serialize(byteBuf)
            StringAttributeUtil.writeKeyPressesByte(byteBuf)
            EncodingKey.END.serialize(byteBuf)
            BufferedOutputStream(FileOutputStream(FileUtil.logDestinationByte)).use {
                it.write(byteBuf.array())
            }
        }

        private fun logPlayer() {
            val player = MinecraftClient.getInstance().player ?: return

            FileWriter(FileUtil.logDestination, true).use {
                it.write("Log local")
                it.write("\n")
                CommandUtil.logger.info("Writing timestamp")
                it.write(StringAttributeUtil.getTimeStamp())
                it.write("\n")
                CommandUtil.logger.info("Writing uuid")
                it.write(StringAttributeUtil.getUUID(player))
                it.write("\n")
                CommandUtil.logger.info("Writing blockPos")
                it.write(StringAttributeUtil.getBlockPosition(player))
                it.write("\n")
                CommandUtil.logger.info("Writing headRotation")
                it.write(StringAttributeUtil.getHeadRotation(player))
                it.write("\n")
                CommandUtil.logger.info("Writing crossHairBlock")
                it.write(StringAttributeUtil.getCrossHairBlock(player))
                it.write("\n")
                CommandUtil.logger.info("Writing key presses")
                it.write(StringAttributeUtil.getKeyPresses())
                it.write("\n")
                CommandUtil.logger.info("Writing visible mobs")
                it.write(StringAttributeUtil.getVisibleMob(player))
                it.write("\n")
                CommandUtil.logger.info("Writing hot bar cursor")
                it.write(StringAttributeUtil.getHotBarCursor(player))
                it.write("\n")
                CommandUtil.logger.info("Writing inventory")
                it.write(StringAttributeUtil.getInventory(player))
                it.write("\n")
                CommandUtil.logger.info("Writing visible blocks")
                it.write(StringAttributeUtil.getVisibleBlocks(player))
                it.write("\n")
                CommandUtil.logger.info("Writing end stamp")
                it.write(StringAttributeUtil.getEndStamp())
                it.write("\n")
            }
        }

    }
}