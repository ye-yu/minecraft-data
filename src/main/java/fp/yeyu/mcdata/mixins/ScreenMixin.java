package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.KeyLogger;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.screen.Screen;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractParentElement implements KeyLogger {

	private final ArrayList<Integer> pressedKeys = new ArrayList<>();
	private final ArrayList<Integer> mouseButton = new ArrayList<>();
	private double mouseX = 0.0;
	private double mouseY = 0.0;

	@Override
	public @NotNull ArrayList<Integer> getPressedKeys() {
		return pressedKeys;
	}

	@Override
	public @NotNull ArrayList<Integer> getPressedMouseButton() {
		return mouseButton;
	}

	@Override
	public double getMouseX() {
		return mouseX;
	}

	@Override
	public double getMouseY() {
		return mouseY;
	}

	@Inject(method = "keyPressed", at = @At("HEAD"))
	void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		System.out.println("Pressed " + keyCode);
		if (!pressedKeys.contains(keyCode)) pressedKeys.add(keyCode);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		System.out.println("Released " + keyCode);
		pressedKeys.remove(Integer.valueOf(keyCode));
		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		System.out.println("Mouse down " + button);
		if (!mouseButton.contains(button)) mouseButton.add(button);
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		System.out.println("Mouse up " + button);
		mouseButton.remove(Integer.valueOf(button));
		System.out.println("Now mouse button contains " + StringUtils.join(mouseButton, ","));
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		return super.mouseReleased(mouseX, mouseY, button);
	}
}
