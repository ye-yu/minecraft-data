package fp.yeyu.mcdata.data

import fp.yeyu.mcdata.data.EncodingKey
import fp.yeyu.mcdata.interfaces.ByteSerializable
import net.minecraft.client.MinecraftClient
import net.minecraft.client.options.KeyBinding
import net.minecraft.network.PacketByteBuf

enum class GameKey(keySupplier: () -> KeyBinding) : ByteSerializable {
    KEY_ATTACK({ MinecraftClient.getInstance().options.keyAttack }),
    KEY_USE({ MinecraftClient.getInstance().options.keyUse }),
    KEY_FORWARD({ MinecraftClient.getInstance().options.keyForward }),
    KEY_LEFT({ MinecraftClient.getInstance().options.keyLeft }),
    KEY_BACK({ MinecraftClient.getInstance().options.keyBack }),
    KEY_RIGHT({ MinecraftClient.getInstance().options.keyRight }),
    KEY_JUMP({ MinecraftClient.getInstance().options.keyJump }),
    KEY_SNEAK({ MinecraftClient.getInstance().options.keySneak }),
    KEY_SPRINT({ MinecraftClient.getInstance().options.keySprint }),
    KEY_DROP({ MinecraftClient.getInstance().options.keyDrop }),
    KEY_INVENTORY({ MinecraftClient.getInstance().options.keyInventory }),
    KEY_CHAT({ MinecraftClient.getInstance().options.keyChat }),
    KEY_PLAYER_LIST({ MinecraftClient.getInstance().options.keyPlayerList }),
    KEY_PICK_ITEM({ MinecraftClient.getInstance().options.keyPickItem }),
    KEY_COMMAND({ MinecraftClient.getInstance().options.keyCommand }),
    KEY_SCREENSHOT({ MinecraftClient.getInstance().options.keyScreenshot }),
    KEY_TOGGLE_PERSPECTIVE({ MinecraftClient.getInstance().options.keyTogglePerspective }),
    KEY_SMOOTH_CAMERA({ MinecraftClient.getInstance().options.keySmoothCamera }),
    KEY_FULLSCREEN({ MinecraftClient.getInstance().options.keyFullscreen }),
    KEY_SPECTATOR_OUTLINES({ MinecraftClient.getInstance().options.keySpectatorOutlines }),
    KEY_SWAP_HANDS({ MinecraftClient.getInstance().options.keySwapHands }),
    KEY_SAVE_TOOLBAR_ACTIVATOR({ MinecraftClient.getInstance().options.keySaveToolbarActivator }),
    KEY_LOAD_TOOLBAR_ACTIVATOR({ MinecraftClient.getInstance().options.keyLoadToolbarActivator }),
    KEY_ADVANCEMENTS({ MinecraftClient.getInstance().options.keyAdvancements }),
    KEY_HOTBAR_1({ MinecraftClient.getInstance().options.keysHotbar[0] }),
    KEY_HOTBAR_2({ MinecraftClient.getInstance().options.keysHotbar[1] }),
    KEY_HOTBAR_3({ MinecraftClient.getInstance().options.keysHotbar[2] }),
    KEY_HOTBAR_4({ MinecraftClient.getInstance().options.keysHotbar[3] }),
    KEY_HOTBAR_5({ MinecraftClient.getInstance().options.keysHotbar[4] }),
    KEY_HOTBAR_6({ MinecraftClient.getInstance().options.keysHotbar[5] }),
    KEY_HOTBAR_7({ MinecraftClient.getInstance().options.keysHotbar[6] }),
    KEY_HOTBAR_8({ MinecraftClient.getInstance().options.keysHotbar[7] }),
    KEY_HOTBAR_9({ MinecraftClient.getInstance().options.keysHotbar[8] });

    val key: KeyBinding by lazy { keySupplier() }

    override fun serialize(buffer: PacketByteBuf) {
        buffer.writeEnumConstant(this)
    }

    companion object {
        val encodingKey get() = EncodingKey.ACTION
    }
}