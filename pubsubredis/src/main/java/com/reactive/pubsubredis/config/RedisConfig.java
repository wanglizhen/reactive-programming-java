package com.reactive.pubsubredis.config;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.internal.LettuceFactories;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import io.lettuce.core.resource.ClientResources;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
	@Value("${redis.server}")
	private String host;
	@Value("${redis.port}")
	private int port;
    @Bean("redis-commands")
    public RedisStringReactiveCommands<String, String> redisPrimaryReactiveCommands(RedisClient redisClient) {
        return redisClient.connect().reactive();
    }

    @Bean("redis-subscription-commands")
    public RedisPubSubReactiveCommands<String, String> redisSubReactiveCommands(RedisClient redisClient) {
        return redisClient.connectPubSub().reactive();
    }

    @Bean("redis-publishion-commands")
    public RedisPubSubReactiveCommands<String, String> redisPubReactiveCommands(RedisClient redisClient) {
        return redisClient.connectPubSub().reactive();
    }
    
    @Bean("redis-client")
    public RedisClient redisClient() {
        return RedisClient.create(
                // adjust things like thread pool size with client resources
                ClientResources.builder().build(),
                "redis://" + host + ":" + port
        );
    }

}
