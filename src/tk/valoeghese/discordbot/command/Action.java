package tk.valoeghese.discordbot.command;

import java.util.List;

public interface Action {
	void exec(List<String> stores, Cacher cacher, CharacterSupplier next);
}
