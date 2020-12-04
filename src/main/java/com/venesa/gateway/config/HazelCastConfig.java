//package com.venesa.gateway.config;
//
//import com.hazelcast.config.Config;
//import com.hazelcast.config.EvictionPolicy;
//import com.hazelcast.config.MapConfig;
//import com.hazelcast.config.MaxSizeConfig;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class HazelCastConfig {
//
//    @Bean
//    public Config config() {
//        return new Config().setInstanceName("hazlecast-insatnce")
//                .addMapConfig(new MapConfig().setName("userCache")
//                        .setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
//                        .setEvictionPolicy(EvictionPolicy.LRU).setTimeToLiveSeconds(2000));
//    }
//}

