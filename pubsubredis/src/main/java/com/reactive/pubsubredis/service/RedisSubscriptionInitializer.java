package com.reactive.pubsubredis.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisSubscriptionInitializer {

	@Qualifier("redis-subscription-commands")
	private RedisPubSubReactiveCommands<String, String> redisSubReactiveCommands;

	public RedisSubscriptionInitializer(
			@Qualifier("redis-subscription-commands") RedisPubSubReactiveCommands<String, String> redisSubReactiveCommands) {
		this.redisSubReactiveCommands = redisSubReactiveCommands;
	}
	@PostConstruct
	public void setupSubscriber() throws Exception {
		//redisSubReactiveCommands.subscribe("channel-1","channel-1").subscribe();
	}
}
