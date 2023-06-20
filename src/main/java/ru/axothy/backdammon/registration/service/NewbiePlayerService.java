package ru.axothy.backdammon.registration.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface NewbiePlayerService {
    String sendSmsForNewbiePlayer(String newNickname, String newPhoneNumber);
    String verifyCode(String nickname, int code);

}
