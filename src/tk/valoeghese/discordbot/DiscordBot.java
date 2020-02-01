package tk.valoeghese.discordbot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import tk.valoeghese.discordbot.system.DiscordInteractionManager;

// https://discordapp.com/api/oauth2/authorize?client_id=672735699128287243&permissions=261184&scope=bot
public class DiscordBot {
	private static DiscordClient client;
	public static final int permissions = 261184;

	public static void main(String[] args) {
		client = new DiscordClientBuilder(args[0]).build();
		DiscordInteractionManager.MAIN.setDiscordClient(client);
		init();
		client.login().block();
	}

	private static void init() {
		DiscordInteractionManager.MAIN.register(EventHandler.class);
	}
}
