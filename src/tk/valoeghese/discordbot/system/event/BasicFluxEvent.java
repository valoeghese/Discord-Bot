package tk.valoeghese.discordbot.system.event;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Flux;

public abstract class BasicFluxEvent<T extends Event> implements EventFlux<T> {
	protected Flux<T> eventFlux;

	public void init(Flux<T> eventFlux) {
		this.eventFlux = eventFlux;
	}

	public Flux<T> getFlux() {
		return this.eventFlux;
	}
}
