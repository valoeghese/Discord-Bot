package tk.valoeghese.discordbot.command;

public class Cacher {
	private String s = null;
	
	public String get() {
		return this.s;
	}
	
	public void cache(String s) {
		this.s = new String(s);
	}
}
