package ru.axothy.backdammon.registration.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.axothy.backdammon.registration.model.Player;

import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Collections;
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

    @Autowired
    private Keycloak keycloak;

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

    private void saveCodeToCash(String nickname, int code) {

    }

    private void deleteCodeFromCash(String nickname) {

    }

    @Override
    public String verifyCode(String phoneNumber, int code) {
        return null;
    }

    @Override
    public void registerNewPlayer(String nickname, String password, String phoneNumber, int code) {
        //createNewbie(nickname, phoneNumber);
        createKeycloakUser(nickname, password);
    }

    private void createNewbie(String nickname, String phoneNumber) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAdminToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8081/admin/players/newbie")
                .queryParam("nickname", nickname)
                .queryParam("phone", phoneNumber);
        HttpEntity request = new HttpEntity(headers);

        restTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, Player.class).getBody();
    }

    private void createKeycloakUser(String username, String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);

        UserRepresentation user = new UserRepresentation();
        user.setCredentials(Collections.singletonList(credential));
        user.setUsername(username);
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setRealmRoles(Arrays.asList("player"));

        var respone = keycloak.realm("backdammon-realm").users().create(user);
        System.out.println(respone.getStatus());
        System.out.println(respone.getMetadata());

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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAdminToken());
        HttpEntity request = new HttpEntity(headers);


        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8081/players")
                .queryParam("nickname", newNickname);

        Player player = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, Player.class).getBody();
        if (player != null) return true;

        return false;
    }

    private boolean isPhoneNumberAlreadyUsed(String newPhoneNumber) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAdminToken());
        HttpEntity request = new HttpEntity(headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8081/admin/players")
                .queryParam("phone", newPhoneNumber);

        Player player = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, Player.class).getBody();
        if (player != null) return true;

        return false;
    }

    private String getAdminToken() {
        return keycloak.tokenManager().getAccessTokenString();
    }

}
