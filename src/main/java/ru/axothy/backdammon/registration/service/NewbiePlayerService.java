package ru.axothy.backdammon.registration.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface NewbiePlayerService {
    String sendSmsForNewbiePlayer(String newNickname, String newPhoneNumber);
    String verifyCode(String phoneNumber, int code);
    void registerNewPlayer(String nickname, String password, String phoneNumber, int code);
}
