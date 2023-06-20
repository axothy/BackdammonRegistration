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
    public ResponseEntity<String> verifyCode(@RequestParam("nickname") String nickname, @RequestParam("code") int code) {
        return ResponseEntity.ok("x");
    }

}
