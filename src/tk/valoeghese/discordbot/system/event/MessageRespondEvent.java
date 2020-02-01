package tk.valoeghese.discordbot.system.event;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Flux;

public class MessageRespondEvent extends SpecialEvent<MessageCreateEvent> {
	protected String response = null;
	protected boolean tts = false;
	protected Consumer<EmbedCreateSpec> embedCreator = null;

	protected Message message;

	@Override
	public void setup(Flux<MessageCreateEvent> eventFlux, SpecialEventRunnable eventInvoker) {
		eventFlux.map(event -> event.getMessage())
		.filter(msg -> {
			return msg.getAuthor().map(user -> !user.isBot()).orElse(false);
		})
		.filter(msg -> {
			this.message = msg;
			this.response = null;
			this.embedCreator = null;

			try {
				eventInvoker.run();
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}

			return true;
		})
		.filter(channel -> (this.response != null && !this.response.isEmpty()))
		.flatMap(msg -> msg.getChannel())
		.flatMap(channel -> channel.createMessage(createSpec -> {
			createSpec.setTts(this.tts).setContent(this.response);

			if (this.embedCreator != null) {
				createSpec.setEmbed(this.embedCreator);
			}
		}))
		.subscribe();
	}

	@Override
	public void onMainSubscribe(MessageCreateEvent event, SpecialEventRunnable eventInvoker) {
	}

	@Override
	public Class<MessageCreateEvent> wrappedEventClass() {
		return MessageCreateEvent.class;
	}

	public Message getMessage() {
		return this.message;
	}

	public void setResponse(String message) {
		this.setResponse(message, null, false);
	}

	public void setResponse(String message, boolean tts) {
		this.setResponse(message, null, tts);
	}

	public void setResponse(Consumer<EmbedCreateSpec> embed) {
		this.setResponse("", embed, false);
	}

	public void setResponse(String message, Consumer<EmbedCreateSpec> embed, boolean tts) {
		this.response = message;
		this.embedCreator = embed;
		this.tts = tts;
	}
}
