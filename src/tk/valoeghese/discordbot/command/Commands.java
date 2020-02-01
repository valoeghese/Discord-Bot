package tk.valoeghese.discordbot.command;

import java.util.HashMap;
import java.util.Map;

public class Commands {
	public static String handle(String user, String command) {
		boolean replace = false;

		if (COMMANDS.containsKey(user)) {
			replace = true;
		}

		try {
			Command cmd = new Command(command);
			COMMANDS.put(user, cmd);
			return (replace ? "Replaced" : "Added") + " user function: " + user;
		} catch (InvalidArgumentException e) {
			return "Function either does not give a response or is invalid!";
		} catch (Throwable e) {
			String result = e.toString();

			if (result.length() > 100) {
				return result.substring(0, 100);
			}
			return result;
		}
	}

	private static final Map<String, Command> COMMANDS = new HashMap<>();

	public static String invoke(String cmd) {
		int index = cmd.indexOf(' ');

		if (index == -1) {
			if (COMMANDS.containsKey(cmd)) {
				return COMMANDS.get(cmd).execute(""); // no input
			}
		} else {
			String user = cmd.substring(0, index);

			if (COMMANDS.containsKey(user)) {
				String commandInput = cmd.substring(index + 1);
				return COMMANDS.get(user).execute(commandInput);
			}
		}

		return null;
	}
}
