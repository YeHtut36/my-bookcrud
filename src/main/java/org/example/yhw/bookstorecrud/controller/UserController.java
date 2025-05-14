package org.example.yhw.bookstorecrud.controller;

import lombok.var;
import org.example.yhw.bookstorecrud.dto.UserDTO;
import org.example.yhw.bookstorecrud.mapper.UserMapper;
import org.example.yhw.bookstorecrud.model.User;
import org.example.yhw.bookstorecrud.queryCriteria.UserCriteria;
import org.example.yhw.bookstorecrud.service.UserService;
import org.example.yhw.bookstorecrud.vo.DataTableInput;
import org.example.yhw.bookstorecrud.vo.DataTableOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showUserList() {
        return "user-list";
    }

    @PostMapping(path = "/api", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DataTableOutput<UserDTO>> listUsers(@RequestBody DataTableInput dataTableInput) {
        String searchValue = "";
        if (dataTableInput.getSearch() != null && dataTableInput.getSearch().getValue() != null) {
            searchValue = dataTableInput.getSearch().getValue().trim();
        }

        UserCriteria criteria = new UserCriteria();
        criteria.setUsername(searchValue);

        Pageable pageable = dataTableInput.getPageable();

        var userPage = userService.searchUsers(criteria, pageable);
        long totalRecords = userService.getAllUsers(Pageable.unpaged()).getTotalElements();

        DataTableOutput<UserDTO> output = DataTableOutput.of(userPage, totalRecords, dataTableInput);
        output.setDraw(dataTableInput.getDraw());

        return ResponseEntity.ok(output);
    }


    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElse(new User());
        model.addAttribute("user", user);
        return "user-form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String saveUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", user);
        return "user-profile";
    }
}
