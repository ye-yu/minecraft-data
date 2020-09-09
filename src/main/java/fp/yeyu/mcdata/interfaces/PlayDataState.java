package fp.yeyu.mcdata.interfaces;

public interface PlayDataState {
	boolean hasNotLogged();

	boolean worldHasChanged();

	void setHasNotLogged(boolean bl);
}
