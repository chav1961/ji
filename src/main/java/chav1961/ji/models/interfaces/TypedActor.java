package chav1961.ji.models.interfaces;

public interface TypedActor<Type extends Enum<?>> extends Actor {
	Type[] getItemTypes();
	Actor getForType(Type type);
}
