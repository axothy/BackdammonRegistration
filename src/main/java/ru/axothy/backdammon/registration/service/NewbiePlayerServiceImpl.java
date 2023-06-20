package ru.axothy.backdammon.registration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NewbiePlayerServiceImpl implements NewbiePlayerService {
    private static final String NICKNAME_ALREADY_USED = "Данный никнейм уже зарегистрирован";
    private static final String PHONE_NUMBER_ALREADY_USED = "Данный номер телефона уже зарегистрирован";
    private static final String INVALID_NICKNAME = "Введите корректный никнейм (только латиница, 3-16 символов, без знаков)";
    private static final String INVALID_PHONE_NUMBER = "Введите корректный номер телефона";
    private static final String SMS_SENT_SUCCESSFULL = "СМС с кодом был отправлен на ваш номер";

    @Autowired
    private SMSService smsService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String sendSmsForNewbiePlayer(String newNickname, String newPhoneNumber) {
        if (isNicknameValid(newNickname) != true) return INVALID_NICKNAME;
        if (isPhoneNumberValid(newPhoneNumber) != true) return INVALID_PHONE_NUMBER;

        if (isNicknameAlreadyUsed(newNickname) == true) return NICKNAME_ALREADY_USED;
        if (isPhoneNumberAlreadyUsed(newPhoneNumber) == true) return PHONE_NUMBER_ALREADY_USED;

        int code = smsService.sendSMS(newPhoneNumber, newNickname);
        saveCodeToCash(newNickname, code);
        return SMS_SENT_SUCCESSFULL;
    }

    @Override
    public void saveCodeToCash(String nickname, int code) {

    }

    @Override
    public void deleteCodeFromCash(String nickname) {

    }

    @Override
    public String verifyCode(String nickname, int code) {
        return null;
    }

    private boolean isNicknameValid(String nickname) {
        if (nickname.length() < 3 || nickname.length() > 16 || nickname == null) return false;

        String regex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nickname);

        return matcher.matches();
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        String regex = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);

        return matcher.matches();
    }

    private boolean isNicknameAlreadyUsed(String newNickname) {
        return false;
    }

    private boolean isPhoneNumberAlreadyUsed(String newPhoneNumber) {
        return false;
    }
}
