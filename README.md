PlayData
---

A Fabric client-side mod that captures the client-side perspective of Minecraft gameplay. 
The list of data that is captured is listed in the [documentation](#documentation)
below.

<p>
    <a title="Fabric Language Kotlin" href="https://minecraft.curseforge.com/projects/fabric-language-kotlin" target="_blank" rel="noopener noreferrer">
        <img style="display: block; margin-left: auto; margin-right: auto;" src="https://i.imgur.com/c1DH9VL.png" alt="" width="171" height="50" />
    </a>
</p>
<small>
    This mod requires 
    <a title="Fabric Language Kotlin" href="https://minecraft.curseforge.com/projects/fabric-language-kotlin" target="_blank" rel="noopener noreferrer">
        Fabric Language Kotlin (FLK)
    </a>
    mod loader and compiled with FLK version <code>v1.4.0-build.1</code>.
    Include this version in the mod folder.
</small>

# Table of content

  - [Download](#download)
  - [How it works](#how-it-works)
  - [Configuration](#configuration)
  - [Documentation](#documentation)
    - [Decoding strategy](#decoding-strategy)
    - [Data capture strategy](#capture-strategy)
  - [Future Plan](#future-plan)
  - [Other source](#other-source)

## <a href="#" name="download"></a> Download

The mod file can be obtained from the [release](https://github.com/ye-yu/minecraft-data/releases) page.
In addition, the CurseForge page is [here](https://www.curseforge.com/minecraft/mc-mods/play-data).

## <a href="#" name="how-it-works"></a> How it works

v0.0.1 spawns two threads: a publisher thread, and a consumer thread.
A publisher thread publishes the data periodically into a shared ring buffer
by executing a series of functions that pushes the data into the ring
buffer. A consumer thread queries next data from the ring buffer and appends
the data as bytes into the log files before flushing the buffer.

These threads only run when the player is in the game and is not pausing
the game. After the player has left the game, a parser will convert the byte
data into a readable `json` format. At the start of the game, the parser will
look for leftover unconverted byte data files and try to convert them again.
The mod does the`json` conversion only if it is configured in the 
configuration file.

In case of a late reader scenario, the publisher will append
the data at the end of the buffer potentially increasing the
size of the buffer. This will result in data being out of the order,
which is observable in the converted `json` file. The time
data for each capture instance can be used to sort them again.

## <a href="#" name="configuration"></a> Configuration

The mod is configurable with the following available options:

| Field | Type | Description |
|---|---|---|
| useRawId | Boolean | When converting to json, `true` if to the use id as their raw form, otherwise to use a namespace identifier [Default: true] |
| ringBufferSize | Int | The size of the ring buffer [Default: 16] |
| useHeapBuffer | Boolean | True to use heap buffer in the ring buffer, otherwise to use direct buffer [Default: false] |
| bufferInitialCapacity | Int | The size of each buffer in the ring buffer [Default 4096] |
| parallelWriteThreads | Int | For consumer/parser to write concurrently using threads [Default: 1] |
| writeMillisecondSleep | Int | The duration of sleep in milliseconds for publisher [Default: 500] |
| convertToJson | Boolean | True to always convert the byte data into `json` format [Default: true] |

## <a href="#" name="documentation"></a> Documentation

The following table is the encoding strategy of the data
into bytes. Each byte in the following row is called a byte key, 
and the encoded data is called a byte data.

| Byte keys | Key name | Description |
|---|---|---|
| 0 | BUFFER | An empty space for buffer if any^ | 
| 1 | START | The start of the capture instance | 
| 2 | TIME | The system time | 
| 3 | WORLD | The world type | 
| 4 | BIOME | The biome type | 
| 5 | POSITION | The position of the player | 
| 6 | ROTATION | The pitch and yaw of the player | 
| 7 | HEALTH | The heart and hunger level of the player | 
| 8 | EFFECT | The list of status effect on the player | 
| 9 | FOCUS | The target block of the player** | 
| 10 | INVENTORY | The current player inventory** | 
| 11 | HOTBAR | The active hotbar | 
| 12 | MENU | The current opened menu** | 
| 13 | KEYBOARD | The list of pressed keys**  | 
| 14 | MOUSE | The list of pressed mouse buttons** | 
| 15 | MOUSE_POSITION | The mouse position** | 
| 16 | MENU_SLOTS | The menu inventory slots** | 
| 17 | MENU_CURSOR_SLOT | The menu item at the cursor** | 
| 18 | ACTION | The in-game action (forward, jump, etc) | 
| 19 | MOBS | The list of visible mobs** | 
| 20 | BLOCKS | The list of visible blocks** | 
| -2 | END | The end of the capture instance | 
| -1 | EOF | Used by the converter; to indicate the end of the file* | 

<small>*Is not present in the byte data</small>

<small>**Is not present in the byte data if the data is not available</small>

<small>^Pretty sure is not present in the byte data</small>

### <a href="#" name="decoding-strategy"></a>  Decoding strategy

Except for the `START` and `END` byte key, all byte keys must be followed by
a key value. The encoding of the byte data complies with the [protocol
buffer](https://developers.google.com/protocol-buffers/docs/encoding) encoding.

The decoding of `long` and `int` data type must follow the decoding of the variable int
strategy. The decoding of `double` follows the Java representation 
(i.e. IEEE 754 floating-point "double format" bit layout). 

The following table is the tabulated decoding strategy for key value for each
byte keys:

| Byte keys | Decoding strategy (What to extract next) |
|---|---|
| START | *None* | 
| TIME |  time: Double | 
| WORLD | worldId: Int | 
| BIOME | biomeId: Int | 
| POSITION | x: Double, y: Double, z: Double | 
| ROTATION | pitch: Double, yaw: Double | 
| HEALTH | heart: Double, hunger: Int | 
| EFFECT | size: Int, (effectId: Int, duration: Int) <- for `size` times* | 
| FOCUS | blockId: Int, x: Int, y: Int, z: Int | 
| INVENTORY | size: Int, (slot: Int, count: Int, itemId: Int) <- for `size` times* | 
| HOTBAR | cursor: Int | 
| MENU | menuId: Int | 
| KEYBOARD | size: Int, (keyId: Int) <- for `size` times* | 
| MOUSE | size: Int, (mouseButton: Int) <- for `size` times* | 
| MOUSE_POSITION | x: Double, y: Double | 
| MENU_SLOTS | size: Int, (slot: Int, count: Int, itemId: Int) <- for `size` times* | 
| MENU_CURSOR_SLOT | count: Int, itemId: Int | 
| ACTION | size: Int, (actionId: Int) <- for `size` times* | 
| MOBS | size: Int, (mobId: Int, x: Double, y: Double, z: Double) <- for `size` times* | 
| BLOCKS | size: Int, (blockId: Int, x: Int, y: Int, z: Int) <- for `size` times* | 
| END | *None* | 

<small>*"for `size` times" means the operation is to be repeated `size` times.</small>

For example, the following is the minimal byte data.

```byte
01 18 02 07 08 -2
```

When converted, the byte data is translated as follows:

```byte
01 - START
18 - ACTION
02 - "there are 2 ints following this"
07 - "an actionId"
08 - "an actionId"
-1 - END
```

The tabulated decoding strategy above is also applied in the conversion to `json`.
See the decoder [here](src/main/kotlin/fp/yeyu/mcdata/data/decoder/Decoder.kt).

### <a href="#" name="capture-strategy"></a> Data capture strategy

This section is to demonstrate the method used to capture data for each byte keys
during each capture instance:

<small>Referring to the Yarn mappings</small>

  1. `TIME` - The system time in milliseconds.
  2. `WORLD` - The world raw ID provided by the custom enum class, [`VanillaWorldType`](src/main/kotlin/fp/yeyu/mcdata/data/VanillaWorldType.kt).
  3. `BIOME` - The biome raw ID provided by the custom enum class, [`VanillaBiomeType`](src/main/kotlin/fp/yeyu/mcdata/data/VanillaBiomeType.kt).
  4. `POSITION` - The player position from `PlayerEntity.getPos(): Vec3d`.
  5. `ROTATION` - The client camera rotation pitch and yaw from `MinecraftClient.getInstance().cameraEntity`.
  6. `HEALTH` - The player health in `double` from `PlayerEntity.getHealth()` and hunger in `int` from `PlayerEntity.hungerManager.getFoodLevel()`.
  7. `EFFECT` - From `PlayerEntity.getStatusEffect()`, if the map is not empty, then push the size of the map, and then the status effect raw ID from the map key using `StatusEffect.getRawId(StatusEffect)`, and then the effect duration from the mapped value `StatusEffectInstance.getDuration()`.
  8. `FOCUS` - Get the `HitBlockResult` from ray-casting from the camera to 20 blocks ahead of the camera. 
  9. `INVENTORY` - Map the slot number with the slot item, filter out the empty slots, and then if the list is not empty, push the slot number, item stack count, and the item raw ID from `Registry.ITEM.getRawId()`.
  10. `HOTBAR` - Get the integer from `PlayerInventory.selectedSlot`
  11. `MENU` - Get the screen from `MinecraftClient.getInstance().currentScreen`. If it is an instance of `HandledScreen`, get the screen handler type. If the returned type is null, then push `-2` if the player is in the creative mode or `-1` if it is not. Otherwise, put the screen handler type raw ID from `Registry.SCREEN_HANDLER.getRawId(ScreenHandlerType)`.
  12. `KEYBOARD` - Record the key activity in the `Screen` instance using `KeyLogger` interface. Get the list of pressed keys from `KeyLogger.getPressedKeys()`. If the list is not empty, then push the size of the list and then each of the key id.
  13. `MOUSE` - Record the mouse activity in the `Screen` instance using `KeyLogger` interface. Get the list of pressed keys from `KeyLogger.getPressedMouseButton()`. If the list is not empty, then push the size of the list and then each of the mouse id.
  14. `MOUSE_POSITION` - Record the mouse activity in the `Screen` instance using `KeyLogger` interface. Obtain the mouse position using `KeyLogger.getMouseX()` and `KeyLogger.getMouseY()`. 
  15. `MENU_CURSOR_SLOT` - Get the item from `PlayerInventory.getCurrentStack()`. If the stack is not empty, push the item count and then the item id from `Registry.ITEM.getRawId()`.
  16. `ACTION`- Filter out the bound game key from the enum class [`GameKey`](src/main/kotlin/fp/yeyu/mcdata/data/GameKey.kt) that is not pressed as a list. If the list is not empty, then push the size of the list and then the enum ordinal.
  17. `MOBS` - Obtain loaded mobs from `World.getEntitiesByClass`. Filter out mobs that is not in the camera's field of view and each corner of the mob's bounding box is not visible. The mob visibility is determined by the ray cast of the bounding box to the camera entity. It is considered as visible if the ray does not collide with any solid block.
  18. `BLOCKS` - Obtain the block in a square of the axis (x, z) centered at the camera position, and then at each block position, find the direct block above and below the block position. Store all blocks in a list and then filter out the blocks the is not in the player's field of view and not visible. If the list is not empty, then push the size of the list and then the block id with their block position.

The capturing strategy may not be optimum. If there are 
better solution, feel free to open an issue in the issue tracker.

## <a href="#" name="future-plan"></a> Future Plan

The following is the future plan for this mod.

  1. Block visibility - I found out that block visibility algorithm discards the block above/below the direct block of the block visibility iteration. I may try to iterate each block around the radius centered at the player, but this may affect game performance.
  2. Replaying of data - It would be cool to be able to replay the data. However, I am still figuring out how to effectively store the data seeds and how to replay keyboard + mouse activity at menu.
  
## <a href="#" name="other-source"></a> Other source

This mod enables you to generate the gameplay data by yourself. However,
if you need an immediate and structured data, you may take a look at [MineRL.io](https://minerl.io/).
MineRL dataset is a task-based data set that is already structured for you
to perform the data mining. 

<small>Play Data Project Copyright (C) 2020 Ye-Yu</small>