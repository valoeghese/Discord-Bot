package tk.valoeghese.discordbot.system.event;

import discord4j.core.event.domain.Event;

public interface EventFlux<T extends Event> {
	Class<T> wrappedEventClass();
}
