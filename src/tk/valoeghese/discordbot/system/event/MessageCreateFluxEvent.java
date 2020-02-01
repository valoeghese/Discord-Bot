package tk.valoeghese.discordbot.system.event;

import discord4j.core.event.domain.message.MessageCreateEvent;

public class MessageCreateFluxEvent extends BasicFluxEvent<MessageCreateEvent> {
	@Override
	public Class<MessageCreateEvent> wrappedEventClass() {
		return MessageCreateEvent.class;
	}
}
