package ru.axothy.backdammon.registration.service;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "sms.send.enabled", havingValue = "false")
public class SMSServiceImplDev implements SMSService {
    private static final int SMS_CODE_FOR_DEV = 1111;

    @Override
    public int sendSMS(String phoneNumber, String nickname) {
        int code = generateCode();

        return code;
    }

    @Override
    public int generateCode() {
        return SMS_CODE_FOR_DEV;
    }

}
