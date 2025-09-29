package br.com.erudio.controllers;

import br.com.erudio.data.dto.security.AccountCredentialsDTO;
import br.com.erudio.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Endpoint!")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService service;

    @Operation(summary = "Authenticates an user and returns a token")
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody AccountCredentialsDTO credentials) {

        if (credentialsIsInvalid(credentials))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client Request");

        var token = service.signIn(credentials);

        if (token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client Request");

        return ResponseEntity.ok().body(token);
    }

    @Operation(summary = "Refresh token for authenticated user and returns a token")
    @PutMapping("/refresh/{username}")
    public ResponseEntity<?> refreshToken(@PathVariable(value = "username") String username,
                                          @RequestHeader("Authorization") String refreshToken) {

        if (parametersAreInvalid(username, refreshToken))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client Request");
        var token = service.refreshToken(username, refreshToken);
        if (token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client Request");
        return ResponseEntity.ok().body(token);
    }


    @PostMapping(
            value = "/createUser",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE},
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    public AccountCredentialsDTO create(@RequestBody AccountCredentialsDTO credentials) {
        return service.create(credentials);
    }


    private boolean parametersAreInvalid(String username, String refreshToken) {
        return StringUtils.isBlank(username) || StringUtils.isBlank(refreshToken);
    }


    private static boolean credentialsIsInvalid(AccountCredentialsDTO credentials) {
        return credentials == null || StringUtils.isBlank(credentials.getPassword()) || StringUtils.isBlank(credentials.getUsername());
    }

}
