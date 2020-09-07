package fp.yeyu.mcdata.util

import fp.yeyu.mcdata.data.EncodingKey
import fp.yeyu.mcdata.data.GameKey
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
        fun logPlayer(context: PacketContext, packetByteBuf: PacketByteBuf) {
            Thread(PlayDataGroup) { logPlayer(context.player as ServerPlayerEntity, packetByteBuf) }.start()
        }

        fun logBytePlayer(context: PacketContext, packetByteBuf: PacketByteBuf) {
            Thread(PlayDataGroup) { logPlayerByte(context.player as ServerPlayerEntity, packetByteBuf) }.start()
        }

        private fun logPlayerByte(player: ServerPlayerEntity, packetByteBuf: PacketByteBuf) {
            val byteBuf = PacketByteBuf(Unpooled.buffer())
            EncodingKey.SERVER.serialize(byteBuf)
            ByteAttributeUtil.writeTimeStamp(byteBuf)
            ByteAttributeUtil.writePlayerStats(byteBuf, player)
            ByteAttributeUtil.parseKeyPresses(packetByteBuf).also {
                if (it.isNotEmpty()) {
                    GameKey.encodingKey.serialize(byteBuf)
                    byteBuf.writeVarInt(it.size)
                    it.forEach { key -> key.serialize(byteBuf) }
                }
            }
            ByteAttributeUtil.writeVisibleMobs(byteBuf, player)
            ByteAttributeUtil.writeVisibleBlock(byteBuf, player)
            EncodingKey.END.serialize(byteBuf)
            val array = byteBuf.array()
            var pointer = array.size - 1
            while (array[pointer] == EncodingKey.BUFFER.byte) pointer--

            FileOutputStream(FileUtil.logDestinationByte, true).use {
                for(i in 0..pointer) {
                    it.write(array[i].toInt())
                }
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

        fun onLogLocalRequest(context: PacketContext, packetByteBuf: PacketByteBuf) = Thread(PlayDataGroup, this::logPlayer).start()

        fun onLogByteLocalRequest(context: PacketContext, packetByteBuf: PacketByteBuf) = Thread(PlayDataGroup, this::logByte).start()

        private fun logByte() {
            val player = MinecraftClient.getInstance().player ?: return
            val byteBuf = PacketByteBuf(Unpooled.buffer())
            EncodingKey.LOCAL.serialize(byteBuf)
            ByteAttributeUtil.writeTimeStamp(byteBuf)
            ByteAttributeUtil.writePlayerStats(byteBuf, player)
            ByteAttributeUtil.writeKeyPresses(byteBuf)
            ByteAttributeUtil.writeVisibleMobs(byteBuf, player)
            ByteAttributeUtil.writeVisibleBlock(byteBuf, player)
            EncodingKey.END.serialize(byteBuf)

            val array = byteBuf.array()
            var pointer = array.size - 1
            while (array[pointer] == EncodingKey.BUFFER.byte) pointer--

            FileOutputStream(FileUtil.logDestinationByte, true).use {
                for(i in 0..pointer) {
                    it.write(array[i].toInt())
                }
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