package tk.valoeghese.discordbot.system.event;

import discord4j.core.event.domain.lifecycle.ReadyEvent;

public class ReadyFluxEvent extends BasicFluxEvent<ReadyEvent> {
	@Override
	public Class<ReadyEvent> wrappedEventClass() {
		return ReadyEvent.class;
	}
}
