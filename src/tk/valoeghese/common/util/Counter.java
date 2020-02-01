package tk.valoeghese.common.util;

import java.util.function.IntPredicate;

public class Counter {
	public Counter(IntPredicate target) {
		this.target = target;
		this.counter = 0;
	}

	public Counter(IntPredicate target, int startValue) {
		this.target = target;
		this.counter = startValue;
	}

	protected IntPredicate target;
	private int counter;

	public boolean count() {
		return this.target.test(this.counter++);
	}

	public int value() {
		return this.counter;
	}
}
