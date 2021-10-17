package com.reactive.pubsubredis;

import java.sql.Timestamp;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.lettuce.core.pubsub.api.reactive.ChannelMessage;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;

@SpringBootApplication
public class PusSubRedisApplication  implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(PusSubRedisApplication.class, args);
	}
    private final Logger LOG = LoggerFactory.getLogger(PusSubRedisApplication.class);
	@Qualifier("redis-subscription-commands")
	@Autowired
	private RedisPubSubReactiveCommands<String, String> redisSubReactiveCommands;

	@Qualifier("redis-publishion-commands")
	@Autowired
	private RedisPubSubReactiveCommands<String, String> redisPubReactiveCommands;
	@Override
	public void run(String... args) throws Exception {
		new Thread(() -> { // Lambda Expression
			try {
				Thread.sleep(1000);
				this.redisPubReactiveCommands.publish("channel-1", "test10").subscribe();
				LOG.info("Publish: " + "channel-1" + ":" + "test10" + ":timestamp:"
						+ new Timestamp(System.currentTimeMillis()));
				Thread.sleep(1000);
				this.redisPubReactiveCommands.publish("channel-2", "test2").subscribe();
				LOG.info("Publish: " + "channel-2" + ":" + "test2" + ":timestamp:"
						+ new Timestamp(System.currentTimeMillis()));
				Thread.sleep(1000);
				this.redisPubReactiveCommands.publish("channel-1", "test3").subscribe();
				LOG.info("Publish: " + "channel-1" + ":" + "test3" + ":timestamp:"
						+ new Timestamp(System.currentTimeMillis()));
				Thread.sleep(1000);
				this.redisPubReactiveCommands.publish("channel-1", "test4").subscribe();
				LOG.info("Publish: " + "channel-1" + ":" + "test4" + ":timestamp:"
						+ new Timestamp(System.currentTimeMillis()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		//expected error
		new Thread(new ChennalThreadListener("channel-1", "test1")).start();
		//expected error since not subscribed
		new Thread(new ChennalThreadListener("channel-2", "test2")).start();
		//expected result
		new Thread(new ChennalThreadListener("channel-1", "test3")).start();
		//expected result
		new Thread(new ChennalThreadListener("channel-1", "test4")).start();
	
	}
	class ChennalThreadListener implements Runnable {
		String chennal;
		String message;

		public ChennalThreadListener(String chennal, String message) {
			this.chennal = chennal;
			this.message = message;
		}

		@Override
		public void run() {
			try {
				LOG.info("start listening: " + chennal + ":" + message + ":timestamp:"
						+ new Timestamp(System.currentTimeMillis()));
				//get result or error after 10 second
				ChannelMessage<String, String> result = redisSubReactiveCommands.observeChannels()
						.filter(message -> this.message.equals(message.getMessage()) && this.chennal.equals(message.getChannel()))
						.take(1).single()
						.block(Duration.ofMillis(10000));
				LOG.info("get result: " + result.getChannel() + ":" + result.getMessage() + ":timestamp:"
						+ new Timestamp(System.currentTimeMillis()));
			} catch (Exception e) {
				e.printStackTrace();
				LOG.info("get error:  " + chennal + ":" + message +  ":timestamp:"+ new Timestamp(System.currentTimeMillis()));
			}
		}

	}
}
