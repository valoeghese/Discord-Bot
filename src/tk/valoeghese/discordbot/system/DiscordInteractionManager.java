package tk.valoeghese.discordbot.system;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.Event;
import reactor.core.publisher.Flux;
import tk.valoeghese.discordbot.system.event.BasicFluxEvent;
import tk.valoeghese.discordbot.system.event.EventFlux;
import tk.valoeghese.discordbot.system.event.SpecialEvent;

public final class DiscordInteractionManager {
	private DiscordClient client;
	private final List<Class<?>> subscribers = new ArrayList<>();
	private final Map<String, EventSubscribeInfo<?, ?>> eventPairs = new HashMap<>();

	private DiscordInteractionManager() {
	}

	public void setDiscordClient(DiscordClient client) {
		this.client = client;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void register(Class<?> clazz) {
		subscribers.add(clazz);

		for (Method m : clazz.getMethods()) {
			D4jSubscriber annotation = m.getAnnotation(D4jSubscriber.class);

			if (annotation != null) {
				String name = m.getName();
				System.out.println("DiscordInteractionManager: detected subscribe method, " + clazz.getCanonicalName() + "#" + name);

				if (m.getParameterCount() == 1) {
					Class<?> event = m.getParameters()[0].getType();

					if (EventFlux.class.isAssignableFrom(event)) {
						try {
							EventFlux eventInst = (EventFlux) event.newInstance();

							if (eventInst instanceof BasicFluxEvent) {
								EventSubscribeInfo pair = eventPairs.computeIfAbsent(eventInst.wrappedEventClass().getName(), t -> new EventSubscribeInfo<>());
								pair.flux = (BasicFluxEvent) eventInst;
								pair.fluxSubscriber = m;

								if (pair.eventSubscribed) {
									subscribe(pair);
								}
							} else if (eventInst instanceof SpecialEvent) {
								Flux<Event> eventFlux = this.client.getEventDispatcher().on(eventInst.wrappedEventClass());
								SpecialEvent.SpecialEventRunnable invoker = () -> m.invoke(null, eventInst);
								((SpecialEvent) eventInst).setup(eventFlux, invoker);
								eventFlux.subscribe(e -> {
									try {
										((SpecialEvent) eventInst).onMainSubscribe(e, invoker);
									} catch (InvocationTargetException | IllegalAccessException e1) {
										throw new RuntimeException(e1);
									}
								});
							} else {
								System.err.println("D4jSubscriber \"" + name + "\" is subscribed through the handler \"DiscordInteractionManager\", but does not have an event parameter which extends BasicFluxEvent or SpecialEvent!");
							}
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException(e);
						}
					} else if (Event.class.isAssignableFrom(event)) {
						EventSubscribeInfo pair = eventPairs.computeIfAbsent(event.getName(), t -> new EventSubscribeInfo<>());
						pair.eventSubscriber = m;

						if (pair.flux != null) {
							subscribe(pair);
						}
					} else {
						throw new RuntimeException("D4jSubscriber \"" + name + "\" does not have a valid parameter!");
					}
				} else {
					throw new RuntimeException("D4jSubscriber \"" + name + "\" must have 1 parameter!");
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void subscribe(EventSubscribeInfo pair) {
		((BasicFluxEvent) pair.flux).init(this.client.getEventDispatcher().on(pair.flux.wrappedEventClass()));

		try {
			pair.fluxSubscriber.invoke(null, pair.flux);
			pair.flux.getFlux().subscribe(event -> {
				try {
					pair.eventSubscriber.invoke(null, event);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isSubscriber(Class<?> clazz) {
		return subscribers.contains(clazz);
	}

	public static final DiscordInteractionManager MAIN = new DiscordInteractionManager();
}
