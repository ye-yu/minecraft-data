package fp.yeyu.mcdata.util

import fp.yeyu.mcdata.LogRingBuffer
import fp.yeyu.mcdata.data.EncodingKey
import net.minecraft.client.MinecraftClient
import java.io.FileOutputStream

object LogUtil {
    fun publish() {
        val player = MinecraftClient.getInstance().player ?: return
        val publisher = LogRingBuffer
        EncodingKey.START.serialize(publisher)
        ByteAttributeUtil.writeTimeStamp(publisher)
        ByteAttributeUtil.writePlayerStats(publisher, player)
        ByteAttributeUtil.writeKeyPresses(publisher)
        ByteAttributeUtil.writeVisibleMobs(publisher, player)
        ByteAttributeUtil.writeVisibleBlock(publisher, player)
        EncodingKey.END.serialize(publisher)
        publisher.publish()
    }

    fun consume() {
        val consumer = LogRingBuffer
        FileOutputStream(consumer.nextFileWrite(), true).use {
            it.channel.use { ch -> ch.write(consumer.toByteBuffer()) }
        }
        consumer.consume()
    }
}