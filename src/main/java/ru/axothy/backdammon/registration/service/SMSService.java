package ru.axothy.backdammon.registration.service;

public interface SMSService {
    int sendSMS(String phoneNumber, String nickname);
    int generateCode();
}
