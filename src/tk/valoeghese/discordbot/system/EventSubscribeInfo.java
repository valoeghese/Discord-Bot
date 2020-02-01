package tk.valoeghese.discordbot.system;

import java.lang.reflect.Method;

import discord4j.core.event.domain.Event;
import tk.valoeghese.discordbot.system.event.BasicFluxEvent;

public final class EventSubscribeInfo<F extends BasicFluxEvent<T>, T extends Event> {
	public F flux = null;
	public Method fluxSubscriber;
	public boolean eventSubscribed = false;
	public Method eventSubscriber;
}
