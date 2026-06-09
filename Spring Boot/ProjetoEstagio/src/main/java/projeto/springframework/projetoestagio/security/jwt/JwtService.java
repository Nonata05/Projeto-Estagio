package projeto.springframework.projetoestagio.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import projeto.springframework.projetoestagio.domain.entity.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.audience}")
    private String audience;

    private SecretKey key;

    @PostConstruct
    public void inti(){
        this.key =  Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String gerarToken(User user) {
        Date agora = new Date();
        long tempoExpiracao = 86400000; // 1 dia
        Date dataExpiracao = new Date(agora.getTime() + tempoExpiracao);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(agora)
                .setExpiration(dataExpiracao)
                .claim("scope", user.getPapel().name())
                .claim("role", user.getPapel().name()) // Salva ex: "ADMIN" ou "USER"
                .signWith(key)
                .compact();
    }

    // === NOVO MÉTODO ADICIONADO AQUI ===
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object roles = claims.get("role");

        if (roles instanceof List) {
            return ((List<?>) roles).stream()
                    .map(Object::toString)
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .collect(Collectors.toList());
        } else if (roles instanceof String) {
            String roleStr = (String) roles;
            if (!roleStr.startsWith("ROLE_")) {
                roleStr = "ROLE_" + roleStr; // Garante que vira "ROLE_ADMIN"
            }
            return List.of(roleStr);
        }

        return Collections.emptyList();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(issuer)
                .requireAudience(audience)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            System.err.println("=== ERRO DETALHADO DA VALIDAÇÃO DO JWT ===");
            System.err.println("Causa do erro: " + e.getMessage());
            e.printStackTrace(); // Imprime a pilha de erros no console
            return false;
        }
    }

}
