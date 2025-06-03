package app.service;

import app.auth.service.TokenService;
import app.dto.CreateUserDto;
import app.dto.KeycloakUserDto;
import app.dto.UpdateUserDto;
import app.dto.UserDto;
import app.exception.handler.KeycloakException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class UserService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final TokenService tokenService;

    @Autowired
    private LogService logService;

    @Value("${keycloak.admin.users-url}")
    private String usersUrl;

    public UserService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public UserDto save(CreateUserDto dto) {
        try {
            HttpHeaders headers = getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            KeycloakUserDto keycloakUser = KeycloakUserDto.fromCreateUserDto(dto);
            HttpEntity<KeycloakUserDto> entity = new HttpEntity<>(keycloakUser, headers);

            ResponseEntity<UserDto> response = restTemplate.exchange(
                    usersUrl, HttpMethod.POST, entity, UserDto.class
            );

            logService.registrar("Usuário", dto.getUsername(), "/users", "Usuário criado com sucesso");
            return response.getBody();
        } catch (Exception e) {
            logService.registrar("Usuário", dto.getUsername(), "/users", "Erro ao criar usuário: " + e.getMessage());
            throw handleException(e, "criar usuário");
        }
    }

    public UserDto findById(String id) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            ResponseEntity<UserDto> response = restTemplate.exchange(
                    usersUrl + "/" + id, HttpMethod.GET, entity, UserDto.class
            );

            UserDto user = response.getBody();
            if (user == null) throw new EntityNotFoundException("Usuário não encontrado com id: " + id);

            logService.registrar("Usuário", id, "/users/" + id, "Usuário encontrado com sucesso");
            return user;
        } catch (Exception e) {
            logService.registrar("Usuário", id, "/users/" + id, "Erro ao buscar usuário: " + e.getMessage());
            throw handleException(e, "buscar usuário");
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

            logService.registrar("Usuário", null, "/users", "Lista de usuários obtida com sucesso");
            return users;
        } catch (Exception e) {
            logService.registrar("Usuário", null, "/users", "Erro ao listar usuários: " + e.getMessage());
            throw handleException(e, "listar usuários");
        }
    }

    public Page<UserDto> findAllPaginated(Pageable pageable) {
        List<UserDto> allUsers = findAll(); // Logs feitos dentro do método findAll()
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = page * size;
        int end = Math.min(start + size, allUsers.size());

        if (start >= allUsers.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, allUsers.size());
        }

        List<UserDto> paginatedUsers = allUsers.subList(start, end);
        return new PageImpl<>(paginatedUsers, pageable, allUsers.size());
    }

    public void deleteById(String id) {
        findById(id); // Log feito no método findById()

        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            restTemplate.exchange(usersUrl + "/" + id, HttpMethod.DELETE, entity, Void.class);

            logService.registrar("Usuário", id, "/users/" + id, "Usuário deletado com sucesso");
        } catch (Exception e) {
            logService.registrar("Usuário", id, "/users/" + id, "Erro ao deletar usuário: " + e.getMessage());
            throw handleException(e, "deletar usuário");
        }
    }

    public UserDto updateById(String id, UpdateUserDto updatedUser) {
        findById(id); // Log feito no método findById()

        try {
            HttpHeaders headers = getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            KeycloakUserDto keycloakUser = new KeycloakUserDto();
            keycloakUser.setEmail(updatedUser.getEmail());
            keycloakUser.setFirstName(updatedUser.getFirstName());
            keycloakUser.setLastName(updatedUser.getLastName());
            keycloakUser.setEnabled(updatedUser.isEnabled());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
                KeycloakUserDto.CredentialDto credential = new KeycloakUserDto.CredentialDto();
                credential.setValue(updatedUser.getPassword());
                keycloakUser.getCredentials().add(credential);
            }

            HttpEntity<KeycloakUserDto> entity = new HttpEntity<>(keycloakUser, headers);

            ResponseEntity<UserDto> response = restTemplate.exchange(
                    usersUrl + "/" + id, HttpMethod.PUT, entity, UserDto.class
            );

            logService.registrar("Usuário", id, "/users/" + id, "Usuário atualizado com sucesso");
            return response.getBody();
        } catch (Exception e) {
            logService.registrar("Usuário", id, "/users/" + id, "Erro ao atualizar usuário: " + e.getMessage());
            throw handleException(e, "atualizar usuário");
        }
    }

    private HttpHeaders getHeaders() {
        String token = tokenService.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    private RuntimeException handleException(Exception e, String acao) {
        if (e instanceof HttpClientErrorException err) {
            return new KeycloakException("Erro ao " + acao + " (Status: " + err.getStatusCode() + "): " + err.getResponseBodyAsString(), err);
        }
        if (e instanceof HttpServerErrorException err) {
            return new KeycloakException("Erro no servidor Keycloak ao " + acao + " (Status: " + err.getStatusCode() + "): " + err.getResponseBodyAsString(), err);
        }
        if (e instanceof RestClientException err) {
            return new KeycloakException("Erro de conexão ao " + acao + ": " + err.getMessage(), err);
        }
        return new KeycloakException("Erro inesperado ao " + acao + ": " + e.getMessage(), e);
    }
}
