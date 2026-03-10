package com.neuralFlux.Saas_OOH_demo.controllers;

import com.neuralFlux.Saas_OOH_demo.dtos.UserRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.UserResponseDTO;
import com.neuralFlux.Saas_OOH_demo.models.User;
import com.neuralFlux.Saas_OOH_demo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> save(@RequestBody UserRequestDTO dto){
        User savedUser = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(savedUser));
    }
}
