package tk.valoeghese.discordbot;

import java.util.Optional;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import tk.valoeghese.discordbot.command.Commands;
import tk.valoeghese.discordbot.system.D4jSubscriber;
import tk.valoeghese.discordbot.system.event.MessageRespondEvent;
import tk.valoeghese.discordbot.system.event.ReadyFluxEvent;

public final class EventHandler {
	@D4jSubscriber
	public static void setupReady(ReadyFluxEvent e) {
		System.out.println("Hello, World!");
	}

	@D4jSubscriber
	public static void onReady(ReadyEvent e) {
	}

	@D4jSubscriber
	public static void respondToMessage(MessageRespondEvent e) {
		Optional<String> messageContent = e.getMessage().getContent();

		if (messageContent.isPresent()) {
			String content = messageContent.get();

			if (content.startsWith("funcdef:")) {
				e.setResponse(Commands.handle(e.getMessage().getAuthor().get().getUsername(), content.substring(8, content.length()).trim()));
			} else if (content.startsWith("\u26a1")) {
				String response = Commands.invoke(content.substring(1, content.length()).trim());

				if (response != null) {
					e.setResponse(response);
				}
			}
		}
	}
}
