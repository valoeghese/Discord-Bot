package tk.valoeghese.discordbot.command;

import java.util.ArrayList;
import java.util.List;

import tk.valoeghese.common.util.Counter;

public class Command {
	public Command(String command) throws NumberFormatException, InvalidArgumentException {
		char[] carr = command.toCharArray();
		boolean collectingString = false;
		boolean apply = false;
		StringBuilder sb = new StringBuilder();
		String resultCache = null;
		List<Action> result = new ArrayList<>();

		for (char c : carr) {
			if (c == '<') {
				resultCache = null;
				collectingString = true;
			} else if (c == '>') {
				resultCache = sb.toString();
				sb = new StringBuilder();
				collectingString = false;
			} else if (collectingString) {
				sb.append(c);
			} else if (c == '.') {
				apply = true;
			} else if (c == '#') {
				if (apply) {
					System.out.print("apply:");
					final String check = new String(resultCache); // copy value
					result.add((stores, cacher, supplier) -> {
						StringBuilder rsb = new StringBuilder(); // result string builder

						while (!rsb.toString().matches(check)) {
							char next = supplier.next();

							if (next == '\u0003') {
								break;
							}
							rsb.append(next);
						}

						cacher.cache(rsb.toString());
					});
				} else {
					result.add((stores, cacher, supplier) -> {
						String val = String.valueOf(supplier.next());
						val = val.equals("\u0003") ? "" : val;
						cacher.cache(val);
					});
				}
			} else if (c == '$') {
				result.add((stores, cacher, supplier) -> {
					stores.add(cacher.get());
				});
			} else if (c == '%') {
				int s = Integer.valueOf(sb.toString()).intValue();
				result.add((stores, cacher, supplier) -> {
					cacher.cache(stores.get(s));
				});
				sb = new StringBuilder();
			} else if (c == '!') {
				String value = new String(resultCache); // copy
				result.add((stores, cacher, supplier) -> {
					cacher.cache(value);
				});
			} else {
				sb.append(c);
			}

			if (c != '.') {
				apply = false;
			}
		}

		this.command = result.toArray(new Action[0]);
	}

	private Action[] command;

	public String execute(String messageContent) {
		final char[] content = messageContent.toCharArray();
		final int bound = content.length - 1;
		Counter counter = new Counter(i -> i > bound);

		CharacterSupplier charStream = () -> {
			int val = counter.value();
			if (counter.count()) {
				return '\u0003'; // end string transmission
			}

			char result = content[val];
			return result;
		};

		List<String> stores = new ArrayList<>();
		Cacher cacher = new Cacher();

		for (Action action : this.command) {
			action.exec(stores, cacher, charStream);
		}

		return cacher.get();
	}
}

class InvalidArgumentException extends RuntimeException {
	private static final long serialVersionUID = 547024854090836386L;
}

// # on its own gets a char of input and caches it
// <regex>.# collects an input string until the regex is satisfied, then caches it
// <> collects a string of command characters
// . applies
// $ stores item in the cache
// ! stores the string, usage <string>!
// % retrieves item from storage to the cache, usage indexnumber%