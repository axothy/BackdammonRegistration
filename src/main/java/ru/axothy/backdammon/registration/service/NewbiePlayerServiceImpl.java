package ru.axothy.backdammon.registration.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.axothy.backdammon.registration.config.KeycloakConfig;
import ru.axothy.backdammon.registration.model.Newbie;
import ru.axothy.backdammon.registration.model.Player;
import ru.axothy.backdammon.registration.repos.NewbieRepository;

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
    private static final String SMS_ALREADY_SENT = "СМС с кодом уже был отправлен";

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Autowired
    private SMSService smsService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private NewbieRepository newbieRepository;

    @Value("${keycloak.credentials.secret-realm}")
    private String realmSecret;

    @Override
    public String sendSmsForNewbiePlayer(String newNickname, String newPhoneNumber) {
        if (isNicknameValid(newNickname) != true) return INVALID_NICKNAME;
        if (isPhoneNumberValid(newPhoneNumber) != true) return INVALID_PHONE_NUMBER;

        if (isNicknameAlreadyUsed(newNickname) == true) return NICKNAME_ALREADY_USED;
        if (isPhoneNumberAlreadyUsed(newPhoneNumber) == true) return PHONE_NUMBER_ALREADY_USED;

        if (newbieRepository.existsById(newPhoneNumber)) {
            Newbie newbie = newbieRepository.findById(newPhoneNumber).get();
            if (newbie.getExpirationInSeconds() < 60L) return SMS_ALREADY_SENT;
        }

        int code = smsService.sendSMS(newPhoneNumber, newNickname);
        saveCodeToCash(newPhoneNumber, code);
        return SMS_SENT_SUCCESSFULL;
    }

    private void saveCodeToCash(String phoneNumber, int code) {
        Newbie newbie = new Newbie();
        newbie.setId(phoneNumber);
        newbie.setCode(code);
        newbie.setExpirationInSeconds(60L);
        newbieRepository.save(newbie);
    }

    @Override
    public boolean verifyCode(String phoneNumber, int code) {
        Newbie newbie = newbieRepository.findById(phoneNumber).get();
        if (code == newbie.getCode()) return true;
        else {
            newbie.setExpirationInSeconds(600L);
            newbieRepository.save(newbie);
            return false;
        }
    }

    @Override
    public boolean registerNewPlayer(String nickname, String password, String phoneNumber, int code) {
        if (code != newbieRepository.findById(phoneNumber).get().getCode()) return false;
        else {
            createNewbie(nickname, phoneNumber);
            createKeycloakUser(nickname, password);
            return true;
        }
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
        Keycloak keycloak = KeycloakBuilder.builder().serverUrl(keycloakConfig.getAuthServerUrl())
                .grantType("password")
                .realm(keycloakConfig.getRealm())
                .clientId(keycloakConfig.getResource())
                .clientSecret(realmSecret)
                .username(keycloakConfig.getAdminCredentials().getUsername())
                .password(keycloakConfig.getAdminCredentials().getPassword())
                .build();
        return keycloak.tokenManager().getAccessTokenString();
    }

}
