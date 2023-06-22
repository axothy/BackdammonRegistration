package ru.axothy.backdammon.registration.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Getter @Setter @ToString
@RedisHash("Newbie")
public class Newbie implements Serializable {

    private String id;
    private int code;
    @TimeToLive
    private Long expirationInSeconds;
}