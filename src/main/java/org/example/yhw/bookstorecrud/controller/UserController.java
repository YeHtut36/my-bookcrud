package org.example.yhw.bookstorecrud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.yhw.bookstorecrud.dto.UserDTO;
import org.example.yhw.bookstorecrud.mapper.UserMapper;
import org.example.yhw.bookstorecrud.model.User;
import org.example.yhw.bookstorecrud.queryCriteria.UserCriteria;
import org.example.yhw.bookstorecrud.service.UserService;
import org.example.yhw.bookstorecrud.vo.DataTableInput;
import org.example.yhw.bookstorecrud.vo.DataTableOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService, UserMapper userMapper, ObjectMapper objectMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showUserList() {
        return "user-list";
    }

    @PostMapping(path = "/api", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DataTableOutput<UserDTO>> listUsers(@RequestBody DataTableInput dataTableInput) {
        try {
            Pageable pageable = dataTableInput.getPageable();
            UserCriteria criteria = new UserCriteria();

            if (dataTableInput.getQueryCriteria() != null && !dataTableInput.getQueryCriteria().isNull()) {
                criteria = objectMapper.treeToValue(dataTableInput.getQueryCriteria(), UserCriteria.class);
            }
            if (dataTableInput.getSearch() != null && dataTableInput.getSearch().getValue() != null) {
                String globalSearch = dataTableInput.getSearch().getValue().trim();
                if (!globalSearch.isEmpty() && (criteria.getUsername() == null || criteria.getUsername().isEmpty())) {
                    criteria.setUsername(globalSearch);
                }
            }

            Page<UserDTO> authorPage = userService.searchUsers(criteria, pageable);
            long totalRecords = userService.countUsers();

            DataTableOutput<UserDTO> output = DataTableOutput.of(authorPage, totalRecords, dataTableInput);
            output.setDraw(dataTableInput.getDraw());

            return ResponseEntity.ok(output);
        } catch (Exception e) {
            log.error("Error processing DataTables request", e);
            return ResponseEntity.internalServerError().body(new DataTableOutput<>());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String editUserForm(@PathVariable Long id, Model model) {
        UserDTO userDto = userService.getUserById(id)
                        .map(userMapper::toDto)
                                .orElse(new UserDTO());
        model.addAttribute("user", userDto);
        return "user-form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String saveUser(@ModelAttribute UserDTO userDto) {
        User user = userMapper.toEntity(userDto);
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
        UserDTO userDto = userService.getUserByUsername(authentication.getName())
                .map(userMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", userDto);
        return "user-profile";
    }
}
