package chav1961.ji.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import chav1961.ji.models.interfaces.Actor;
import chav1961.ji.models.interfaces.GoodsType;

public class ActorsRepository implements Iterable<Actor> {
	private final List<Actor>	actors = new ArrayList<>();
	
	public ActorsRepository() {
		
	}
	
	public void addActor(final Actor actor) throws NullPointerException {
		if (actor == null) {
			throw new NullPointerException("Actor to add can't be null");
		}
		else {
			this.actors.add(actor);
			
			this.actors.sort((o1,o2)->{
				if (o1.getSourceType() != o2.getSourceType()) {
					return o1.getSourceType().ordinal() - o2.getSourceType().ordinal(); 
				}
				else {
					return o1.getPriority() - o2.getPriority();
				}
			});
		}
	}

	public Actor[] getActors(final GoodsType type) throws NullPointerException {
		if (type == null) {
			throw new NullPointerException("Type to get content for can't be null");
		}
		else {
			int	arraySize = 0;
			
			for (int index = 0, maxIndex = actors.size(); index < maxIndex; index++) {
				if (actors.get(index).getContentType() == type) {
					arraySize++;
				}
			}
			
			final Actor[]	result = new Actor[arraySize];

			for (int index = 0, maxIndex = actors.size(), to = 0; index < maxIndex; index++) {
				final Actor	actor = actors.get(index); 
				
				if (actor.getContentType() == type) {
					result[to++] = actor;
				}
			}
			return result;
		}
	}

	@Override
	public Iterator<Actor> iterator() {
		return actors.iterator();
	}

	@Override
	public String toString() {
		return "ActorsRepository [actors=" + actors + "]";
	}
}
