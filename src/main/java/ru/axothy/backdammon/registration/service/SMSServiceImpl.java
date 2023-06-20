package ru.axothy.backdammon.registration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
@ConditionalOnProperty(name = "sms.send.enabled", havingValue = "true")
public class SMSServiceImpl implements SMSService {
    @Value("${sms.user}")
    private String smsUser;

    @Value("${sms.api-key}")
    private String smsApiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public int sendSMS(String phoneNumber, String nickname) {
        int code = generateCode();
        String smsText = """
                Приветствуем,+%s!+
                Твой+код+для+регистрации+в+нардах:+%d
                """.formatted(nickname, code);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(smsUser, smsApiKey);
        HttpEntity request = new HttpEntity(headers);

        String url = "https://email:api_key@gate.smsaero.ru/v2/sms/send?number=" + phoneNumber + "&text=" + smsText + "&sign=Нарды+от+Сани";
        restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        return code;
    }

    @Override
    public int generateCode() {
        Random random = new Random();
        int code = random.nextInt(9000) + 1000;

        return code;
    }

}
