package org.example.yhw.bookstorecrud.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.example.yhw.bookstorecrud.dto.LoginRequestDTO;
import org.example.yhw.bookstorecrud.dto.LoginResponseDTO;
import org.example.yhw.bookstorecrud.dto.UserDTO;
import org.example.yhw.bookstorecrud.dto.UserRegistrationDTO;
import org.example.yhw.bookstorecrud.mapper.UserMapper;
import org.example.yhw.bookstorecrud.model.User;
import org.example.yhw.bookstorecrud.queryCriteria.UserCriteria;
import org.example.yhw.bookstorecrud.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserRestController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO,
                                          BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            userService.registerNewUser(registrationDTO);
            return ResponseEntity.status(201).body("User Registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO,
                                       BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            String token = userService.authenticateAndGenerateToken(loginRequestDTO.getUsername(),
                    loginRequestDTO.getPassword());
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body("Invalid  username or Password");
        }
    }

    @GetMapping
    public Page<UserDTO> getUsers(@RequestParam(required = false) String username, Pageable pageable){
        UserCriteria criteria = new UserCriteria();
        criteria.setUsername(username);
        return userService.searchUsers(criteria, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable @RequestParam Long id){
        return userService.getUserById(id)
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> createOrUpdateUser(@PathVariable Long id,
                                                @RequestBody UserDTO userDTO){

        User user = userMapper.toEntity(userDTO);

        if (id == 0) {
            User saved = userService.saveUser(user);
            return ResponseEntity.status(201).body(userMapper.toDto(saved));
        } else {
            return userService.getUserById(id)
                    .map(existingUser -> {
                        user.setId(id);
                        User updated = userService.saveUser(user);
                        return ResponseEntity.ok(userMapper.toDto(updated));
                    })
                    .orElse(ResponseEntity.notFound().build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }
}
