package app.service;

import app.auth.service.TokenService;
import app.dto.CreateUserDto;
import app.dto.UserDto;
import app.exception.handler.KeycloakException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final TokenService tokenService;

    @Value("${keycloak.admin.users-url}")
    private String usersUrl;

    public UserService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public UserDto save(CreateUserDto dto) {
        try {
            HttpHeaders headers = getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CreateUserDto> entity = new HttpEntity<>(dto, headers);

            ResponseEntity<UserDto> response = restTemplate.exchange(
                    usersUrl, HttpMethod.POST, entity, UserDto.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new KeycloakException("Erro ao criar usuário", e);
        }
    }

    public UserDto findById(String id) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            ResponseEntity<UserDto> response = restTemplate.exchange(
                    usersUrl + "/" + id,
                    HttpMethod.GET,
                    entity,
                    UserDto.class
            );

            UserDto user = response.getBody();
            if (user == null) {
                throw new EntityNotFoundException("Usuário não encontrado com id: " + id);
            }

            return user;
        } catch (Exception e) {
            throw new KeycloakException("Erro ao buscar usuário", e);
        }
    }

    public List<UserDto> findAll() {
        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            ResponseEntity<UserDto[]> response = restTemplate.exchange(
                    usersUrl, HttpMethod.GET, entity, UserDto[].class
            );
            List<UserDto> users = Arrays.asList(Objects.requireNonNull(response.getBody()));
            if (users.isEmpty()) throw new KeycloakException("Nenhum usuário encontrado");
            return users;
        } catch (Exception e) {
            throw new KeycloakException("Erro ao listar usuários", e);
        }
    }

    public void deleteById(String id) {
        findById(id); // Verifica se o usuário existe

        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            restTemplate.exchange(
                    usersUrl + "/" + id,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );
        } catch (Exception e) {
            throw new KeycloakException("Erro ao deletar usuário", e);
        }
    }

    public UserDto updateById(String id, CreateUserDto updatedUser) {
        findById(id); // Verifica se o usuário existe

        try {
            HttpHeaders headers = getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CreateUserDto> entity = new HttpEntity<>(updatedUser, headers);

            ResponseEntity<UserDto> response = restTemplate.exchange(
                    usersUrl + "/" + id,
                    HttpMethod.PUT,
                    entity,
                    UserDto.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new KeycloakException("Erro ao atualizar usuário", e);
        }
    }

    private HttpHeaders getHeaders() {
        String token = tokenService.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
} 