package ru.comgrid.server.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.security.annotation.CurrentUser;
import ru.comgrid.server.security.user.info.UserPrincipal;
import ru.comgrid.server.service.user.UserHelp;
import ru.comgrid.server.service.user.UserService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/user", produces = "application/json; charset=utf-8")
@SecurityRequirement(name = "bearerAuth")
public class UserController{
    private final UserService userService;

    /**
     * @hidden
     */
    public UserController(@Autowired UserService userService){
        this.userService = userService;
    }

    @ApiResponse(responseCode = "404", description = "user.not_found. User does not exist.")

    @Operation(summary = "Get user info")
    @GetMapping("/info")
    public Person getUserInfo(
        @CurrentUser UserPrincipal user,
        @RequestParam(required = false) String userId,
        @RequestParam(required = false, defaultValue = "false") boolean includeChats
    ){
        BigDecimal id;
        if(userId == null){
            id = UserHelp.extractId(user);
        }else{
            id = UserHelp.extractId(userId);
        }
        return userService.getPersonById(userId, includeChats, id);
    }

    @Operation(summary = "Check if user is logged in")
    @ApiResponse(responseCode = "200", description = "User is logged in")
    @ApiResponse(responseCode = "401", description = "User is not logged in")
    @GetMapping("/login")
    public ResponseEntity<String> checkIfUserLoggedIn(
        @CurrentUser UserPrincipal user
    ){
        return user != null ? ResponseEntity.ok().build() : ResponseEntity.status(401).build();
    }

    @ApiResponse(responseCode = "403", description = "user.not_admin. User is not an admin, but tries to make one without adminKey.")
    @ApiResponse(responseCode = "403", description = "admin_key.wrong. User is not an admin, but tries to make one with incorrect adminKey.")
    @ApiResponse(responseCode = "404", description = "user.not_found. Provided user is not found.")
    @PostMapping("/make_admin")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void makePersonAnAdmin(
        @CurrentUser UserPrincipal user,
        @RequestParam(required = false) String adminKey,
        @RequestParam(required = false) String personId
    ){
        userService.makePersonAnAdmin(user, adminKey, personId);
    }


    @PostMapping("/username")
    @Transactional
    public void changeUsername(
        @CurrentUser UserPrincipal user,
        @RequestParam String username
    ){
        var userId = user.getId();
        userService.changeUsername(username, userId);
    }

    @GetMapping("/by_username")
    public List<Person> getUsersByUsername(
        @RequestParam String username
    ){
        return userService.getUsersByUsername(username);
    }

}
