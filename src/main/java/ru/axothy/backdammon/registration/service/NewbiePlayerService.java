package ru.axothy.backdammon.registration.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface NewbiePlayerService {
    String sendSmsForNewbiePlayer(String newNickname, String newPhoneNumber);
    boolean verifyCode(String phoneNumber, int code);
    boolean registerNewPlayer(String nickname, String password, String phoneNumber, int code);
}
