package com.vikas.razorpay.commonlib.config;

import com.vikas.razorpay.commonlib.enums.EventAggregateType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties("app.kafka")
@Getter
@Setter
public class KafkaProperties{


    private Map<String,String> topics=new HashMap<>();

    public String topicFor(EventAggregateType aggregateType){
        String topic=topics.get(aggregateType.name().toLowerCase());

        if(topic==null){
            throw new IllegalArgumentException("No Kafka Topic is configured for aggregateType: "+aggregateType);
        }
        return topic;

    }



}
