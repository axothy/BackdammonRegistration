package ru.axothy.backdammon.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.axothy.backdammon.registration.service.NewbiePlayerService;

@RestController
@RequestMapping(value = "/register/")
public class PlayerRegisterController {
    @Autowired
    private NewbiePlayerService newbiePlayerService;

    @GetMapping(value = "/sendsms")
    public ResponseEntity<String> sendSms(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("nickname") String nickname) {
        String response = newbiePlayerService.sendSmsForNewbiePlayer(nickname, phoneNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/verifycode")
    public ResponseEntity<String> verifyCode(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("code") int code) {
        boolean isVerified = newbiePlayerService.verifyCode(phoneNumber, code);

        if (!isVerified) return ResponseEntity.badRequest().body("Неверный код");

        return ResponseEntity.ok("Код верный");
    }

    @PostMapping(value = "/create")
    public ResponseEntity<String> createPlayerAfterVerifying(@RequestParam String nickname, @RequestParam String phoneNumber,
                                                             @RequestParam String password, @RequestParam int code) {
        boolean isRegistered = newbiePlayerService.registerNewPlayer(nickname, password, phoneNumber, code);
        if (!isRegistered) return ResponseEntity.badRequest().body("Что-то пошло не так. Попробуйте снова");

        return ResponseEntity.ok("Вы успешно зарегистрированы!");
    }


}
