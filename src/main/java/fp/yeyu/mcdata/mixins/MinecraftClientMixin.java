package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.PlayDataState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements PlayDataState {

	@Shadow public ClientWorld world;
	private boolean hasNotLogged = true; // first loading must be exited
	private World previousWorld = null;

	@Override
	public boolean hasNotLogged() {
		return hasNotLogged;
	}

	@Override
	public boolean worldHasChanged() {
		boolean changed = previousWorld != world;
		previousWorld = world;
		if (changed) hasNotLogged = true;
		return changed;
	}

	@Override
	public void setHasNotLogged(boolean bl) {
		hasNotLogged = bl;
	}


}
