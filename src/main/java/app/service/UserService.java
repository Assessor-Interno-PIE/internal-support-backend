package app.service;

import app.auth.service.TokenService;
import app.dto.CreateUserDto;
import app.dto.KeycloakUserDto;
import app.dto.UpdateUserDto;
import app.dto.UserDto;
import app.exception.handler.KeycloakException;
import jakarta.persistence.EntityNotFoundException;
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

import java.util.Arrays;
import java.util.Collections;
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

            KeycloakUserDto keycloakUser = KeycloakUserDto.fromCreateUserDto(dto);
            HttpEntity<KeycloakUserDto> entity = new HttpEntity<>(keycloakUser, headers);

            ResponseEntity<UserDto> response = restTemplate.exchange(
                    usersUrl, HttpMethod.POST, entity, UserDto.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            String errorMessage = String.format("Erro ao criar usuário (Status: %s): %s",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakException(errorMessage, e);
        } catch (HttpServerErrorException e) {
            String errorMessage = String.format("Erro no servidor Keycloak (Status: %s): %s",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakException(errorMessage, e);
        } catch (RestClientException e) {
            String errorMessage = String.format("Erro de conexão com Keycloak: %s", e.getMessage());
            throw new KeycloakException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format("Erro inesperado ao criar usuário: %s", e.getMessage());
            throw new KeycloakException(errorMessage, e);
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
        } catch (HttpClientErrorException.NotFound e) {
            throw new EntityNotFoundException("Usuário não encontrado com id: " + id);
        } catch (HttpClientErrorException e) {
            String errorMessage = String.format("Erro ao buscar usuário (Status: %s): %s",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format("Erro ao buscar usuário: %s", e.getMessage());
            throw new KeycloakException(errorMessage, e);
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
        } catch (HttpClientErrorException e) {
            String errorMessage = String.format("Erro ao listar usuários (Status: %s): %s",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format("Erro ao listar usuários: %s", e.getMessage());
            throw new KeycloakException(errorMessage, e);
        }
    }

    public Page<UserDto> findAllPaginated(Pageable pageable) {
        List<UserDto> allUsers = findAll(); // Fetch all users from Keycloak

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
        findById(id); // Verifica se o usuário existe

        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            restTemplate.exchange(
                    usersUrl + "/" + id,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            String errorMessage = String.format("Erro ao deletar usuário (Status: %s): %s",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format("Erro ao deletar usuário: %s", e.getMessage());
            throw new KeycloakException(errorMessage, e);
        }
    }

    public UserDto updateById(String id, UpdateUserDto updatedUser) {
        findById(id); // Verifica se o usuário existe

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
                    usersUrl + "/" + id,
                    HttpMethod.PUT,
                    entity,
                    UserDto.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            String errorMessage = String.format("Erro ao atualizar usuário (Status: %s): %s",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakException(errorMessage, e);
        } catch (HttpServerErrorException e) {
            String errorMessage = String.format("Erro no servidor Keycloak (Status: %s): %s",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakException(errorMessage, e);
        } catch (RestClientException e) {
            String errorMessage = String.format("Erro de conexão com Keycloak: %s", e.getMessage());
            throw new KeycloakException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format("Erro inesperado ao atualizar usuário: %s", e.getMessage());
            throw new KeycloakException(errorMessage, e);
        }
    }

    private HttpHeaders getHeaders() {
        String token = tokenService.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}