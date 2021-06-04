package chav1961.ji.interfaces;

public interface ThreeStateSwitchKeeper<T> {
	public enum SwitchState {
		ALL_OFF, LEFT_ON, RIGHT_ON, UNAVAILABLE;
	}
	
	SwitchState getState();
	T getCargo();
}
