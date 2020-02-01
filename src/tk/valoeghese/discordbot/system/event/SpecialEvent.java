package tk.valoeghese.discordbot.system.event;

import java.lang.reflect.InvocationTargetException;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Flux;

// Abstract Class instead of interface to make incompatible with BasicFluxEvent.
public abstract class SpecialEvent<T extends Event> implements EventFlux<T> {
	public abstract void setup(Flux<T> eventFlux, SpecialEventRunnable invokeEvent) throws InvocationTargetException, IllegalAccessException;;
	public abstract void onMainSubscribe(T event, SpecialEventRunnable invokeEvent) throws InvocationTargetException, IllegalAccessException;;

	@FunctionalInterface
	public static interface SpecialEventRunnable {
		void run() throws InvocationTargetException, IllegalAccessException;
	}
}
