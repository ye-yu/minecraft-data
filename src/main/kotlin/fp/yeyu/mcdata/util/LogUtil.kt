package fp.yeyu.mcdata.util

import fp.yeyu.mcdata.LogRingBuffer
import fp.yeyu.mcdata.VariableByteBuf
import fp.yeyu.mcdata.data.EncodingKey
import io.netty.buffer.Unpooled
import net.minecraft.client.MinecraftClient
import java.io.FileOutputStream
import java.io.FileWriter

object LogUtil {

    fun onLogLocalRequest() = Thread(PlayDataGroup, this::logPlayer).start()
    fun onLogByteLocalRequest() = Thread(PlayDataGroup, this::logByte).start()
    fun onPublishRequest() = Thread(PlayDataGroup, this::publish).start()
    fun onConsumeRequest() = Thread(PlayDataGroup, this::consume).start()

    private fun publish() {
        val player = MinecraftClient.getInstance().player ?: return
        val publisher = LogRingBuffer
        EncodingKey.LOCAL.serialize(publisher)
        ByteAttributeUtil.writeTimeStamp(publisher)
        ByteAttributeUtil.writePlayerStats(publisher, player)
        ByteAttributeUtil.writeKeyPresses(publisher)
        ByteAttributeUtil.writeVisibleMobs(publisher, player)
        ByteAttributeUtil.writeVisibleBlock(publisher, player)
        EncodingKey.END.serialize(publisher)
        publisher.publish()
    }

    private fun consume() {
        val consumer = LogRingBuffer
        FileOutputStream(consumer.nextFileWrite(), true).use {
            it.channel.use { ch -> ch.write(consumer.toByteBuffer()) }
        }
        consumer.consume()
    }

    private fun logByte() {
        val player = MinecraftClient.getInstance().player ?: return
        val byteBuf = VariableByteBuf(Unpooled.buffer())
        EncodingKey.LOCAL.serialize(byteBuf)
        ByteAttributeUtil.writeTimeStamp(byteBuf)
        ByteAttributeUtil.writePlayerStats(byteBuf, player)
        ByteAttributeUtil.writeKeyPresses(byteBuf)
        ByteAttributeUtil.writeVisibleMobs(byteBuf, player)
        ByteAttributeUtil.writeVisibleBlock(byteBuf, player)
        EncodingKey.END.serialize(byteBuf)

        FileOutputStream(FileUtil.logDestinationByte, true).use {
            it.channel.write(byteBuf.toByteBuffer())
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