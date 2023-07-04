package com.skab.javers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skab.javers.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;
    private final Javers javers;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public UserController(UserService userService, Javers javers, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.userService = userService;
        this.javers = javers;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@RequestParam String username, @RequestParam String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = createToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    private String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + 86400);

        String secret = "MjdiOGFiOGVjYjhlNmFlYTNmMWEzNjVlNzMzOTg4YTE5YTQ5ZTE3Mzg3MDA3YzhmNzA4ZGJjZmYzMjE4ZThhMWRmYjMyNDA5MmMyNzMyMGU5NDIwNGVhYzBmNGE3ZGFiNDA2OGUxYTBhNGY0MzY2ZmEzNGJmNzkwMGYwNjE4Mjk";
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts
                .builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/register")
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User updateUser) {
        User user = userService.findById(id);
        user.setUsername(updateUser.getUsername());
//        user.setPassword(updateUser.getPassword());
        user.setEmail(updateUser.getEmail());
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
//        userService.deleteById(id);
    }

    @GetMapping("/audit/snapshots")
    public String getSnapshots() {
        QueryBuilder jqlQuery = QueryBuilder.byClass(User.class);
        List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build());
        return javers.getJsonConverter().toJson(snapshots);
    }

    @GetMapping("/audit/{id}/changes")
    public String getChanges(@PathVariable Long id) {
        User user = userService.findById(id);
        QueryBuilder jqlQuery = QueryBuilder.byInstance(user);
        Changes changes = javers.findChanges(jqlQuery.build());
        return javers.getJsonConverter().toJson(changes);
    }

    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
