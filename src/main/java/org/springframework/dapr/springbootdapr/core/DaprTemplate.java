package org.springframework.dapr.springbootdapr.core;

import org.springframework.lang.Nullable;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.Metadata;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static java.util.Collections.singletonMap;


public class DaprTemplate<T> implements DaprOperations<T>, ApplicationContextAware, BeanNameAware {
    
    private String beanName = "";

    @Nullable
	private ApplicationContext applicationContext;

	private DaprClient client = new DaprClientBuilder().build();

	private String MESSAGE_TTL_IN_SECONDS = "10";

    @Override
	public Void send(@Nullable String topic, @Nullable T message) {
		return doSend(topic, message);
	}

    @Override
    public void setBeanName(String beanName){
        this.beanName = beanName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
	public SendMessageBuilder<T> newMessage(@Nullable T message) {
		return new SendMessageBuilderImpl<>(this, message);
	}



	private Void doSend(@Nullable String topic, @Nullable T message){
			//, @Nullable Schema<T> schema,
			//@Nullable Collection<String> encryptionKeys,
			//@Nullable TypedMessageBuilderCustomizer<T> typedMessageBuilderCustomizer,
			//@Nullable ProducerBuilderCustomizer<T> producerCustomizer) {
		
			return doSendAsync(topic, message).block();
					//, schema, encryptionKeys, typedMessageBuilderCustomizer,
					//producerCustomizer)
				
	
		
	}

	private Mono<Void> doSendAsync(@Nullable String topic, @Nullable T message){
	//		@Nullable Schema<T> schema, @Nullable Collection<String> encryptionKeys,
	//		@Nullable TypedMessageBuilderCustomizer<T> typedMessageBuilderCustomizer,
	//		@Nullable ProducerBuilderCustomizer<T> producerCustomizer) {
		
			return client.publishEvent("default",
				topic,
				message,
				singletonMap(Metadata.TTL_IN_SECONDS, MESSAGE_TTL_IN_SECONDS));
		
	}

    public static class SendMessageBuilderImpl<T> implements SendMessageBuilder<T> {

		private final DaprTemplate<T> template;

		@Nullable
		private final T message;

		@Nullable
		private String topic;

		// @Nullable
		// private Schema<T> schema;

		// @Nullable
		// private Collection<String> encryptionKeys;

		// @Nullable
		// private TypedMessageBuilderCustomizer<T> messageCustomizer;

		// @Nullable
		// private ProducerBuilderCustomizer<T> producerCustomizer;

		SendMessageBuilderImpl(DaprTemplate<T> template, @Nullable T message) {
			this.template = template;
			this.message = message;
		}

		@Override
		public SendMessageBuilder<T> withTopic(String topic) {
			this.topic = topic;
			return this;
		}

		// @Override
		// public SendMessageBuilder<T> withSchema(Schema<T> schema) {
		// 	this.schema = schema;
		// 	return this;
		// }

		// @Override
		// public SendMessageBuilder<T> withEncryptionKeys(Collection<String> encryptionKeys) {
		// 	this.encryptionKeys = encryptionKeys;
		// 	return this;
		// }

		// @Override
		// public SendMessageBuilder<T> withMessageCustomizer(TypedMessageBuilderCustomizer<T> messageCustomizer) {
		// 	this.messageCustomizer = messageCustomizer;
		// 	return this;
		// }

		// @Override
		// public SendMessageBuilder<T> withProducerCustomizer(ProducerBuilderCustomizer<T> producerCustomizer) {
		// 	this.producerCustomizer = producerCustomizer;
		// 	return this;
		// }

		@Override
		public Void send() {
			return this.template.doSend(this.topic, this.message);
			// return this.template.doSend(this.topic, this.message, this.schema, this.encryptionKeys,
			// 		this.messageCustomizer, this.producerCustomizer);
		}

		@Override
		public Mono<Void> sendAsync() {
			// return this.template.doSendAsync(this.topic, this.message, this.schema, this.encryptionKeys,
			// 		this.messageCustomizer, this.producerCustomizer);

			return this.template.doSendAsync(this.topic, this.message);
		}

	}


}

