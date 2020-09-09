package fp.yeyu.mcdata.interfaces;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface KeyLogger {

	@NotNull ArrayList<Integer> getPressedKeys();

	double getMouseX();

	double getMouseY();

	@NotNull
	ArrayList<Integer> getPressedMouseButton();
}
