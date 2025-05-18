package app.service;

import app.auth.service.KeycloakTokenService;
import app.dto.CreateUserDto;
import app.dto.KeycloakUserRepresentation;
import app.dto.UserRepresentation;
import app.dto.UserUpdateRequest;
import app.exception.handler.KeycloakException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final KeycloakTokenService keycloakTokenService;

    @Value("${keycloak.admin.users-url}")
    private String usersUrl;

    public UserService(KeycloakTokenService keycloakTokenService) {
        this.keycloakTokenService = keycloakTokenService;
    }

    public UserRepresentation createUser(CreateUserDto userDto) {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            KeycloakUserRepresentation keycloakUser = KeycloakUserRepresentation.buildFromCreateUser(userDto);
            HttpEntity<KeycloakUserRepresentation> entity = new HttpEntity<>(keycloakUser, headers);

            ResponseEntity<UserRepresentation> response = restTemplate.exchange(
                    usersUrl, HttpMethod.POST, entity, UserRepresentation.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            String errorMessage = String.format("Erro ao criar usuário (Status: %s): %s",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakException(errorMessage);
        } catch (HttpServerErrorException e) {
            String errorMessage = String.format("Erro no servidor Keycloak (Status: %s): %s",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakException(errorMessage);
        } catch (RestClientException e) {
            String errorMessage = String.format("Erro de conexão com Keycloak: %s", e.getMessage());
            throw new KeycloakException(errorMessage);
        } catch (Exception e) {
            String errorMessage = String.format("Erro inesperado ao criar usuário: %s", e.getMessage());
            throw new KeycloakException(errorMessage);
        }
    }

    public UserRepresentation getUserById(String userId) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<UserRepresentation> response = restTemplate.exchange(
                    usersUrl + "/" + userId,
                    HttpMethod.GET,
                    entity,
                    UserRepresentation.class
            );

            UserRepresentation user = response.getBody();
            if (user == null) {
                throw new EntityNotFoundException("Usuário não encontrado com ID: " + userId);
            }

            return user;
        } catch (HttpClientErrorException.NotFound e) {
            throw new EntityNotFoundException("Usuário não encontrado com ID: " + userId);
        } catch (HttpClientErrorException e) {
            String errorMessage = String.format("Erro ao buscar usuário (Status: %s): %s",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakException(errorMessage);
        } catch (Exception e) {
            String errorMessage = String.format("Erro ao buscar usuário: %s", e.getMessage());
            throw new KeycloakException(errorMessage);
        }
    }

    public List<UserRepresentation> listAllUsers() {
        try {
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<UserRepresentation[]> response = restTemplate.exchange(
                    usersUrl, HttpMethod.GET, entity, UserRepresentation[].class
            );
            List<UserRepresentation> users = Arrays.asList(Objects.requireNonNull(response.getBody()));
            if (users.isEmpty()) {
                throw new KeycloakException("Nenhum usuário encontrado");
            }
            return users;
        } catch (HttpClientErrorException e) {
            String errorMessage = String.format("Erro ao listar usuários (Status: %s): %s",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakException(errorMessage);
        } catch (Exception e) {
            String errorMessage = String.format("Erro ao listar usuários: %s", e.getMessage());
            throw new KeycloakException(errorMessage);
        }
    }

    public Page<UserRepresentation> listUsersPaginated(Pageable pageable) {
        List<UserRepresentation> allUsers = listAllUsers();

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = page * size;
        int end = Math.min(start + size, allUsers.size());

        if (start >= allUsers.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, allUsers.size());
        }

        List<UserRepresentation> paginatedUsers = allUsers.subList(start, end);
        return new PageImpl<>(paginatedUsers, pageable, allUsers.size());
    }

    public void deleteUserById(String userId) {
        getUserById(userId);

        try {
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            restTemplate.exchange(
                    usersUrl + "/" + userId,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            String errorMessage = String.format("Erro ao deletar usuário (Status: %s): %s",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakException(errorMessage);
        } catch (Exception e) {
            String errorMessage = String.format("Erro ao deletar usuário: %s", e.getMessage());
            throw new KeycloakException(errorMessage);
        }
    }

    public UserRepresentation updateUserById(String userId, UserUpdateRequest updatedUser) {
        getUserById(userId);

        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            KeycloakUserRepresentation keycloakUser = KeycloakUserRepresentation.buildForUpdate(updatedUser);

            HttpEntity<KeycloakUserRepresentation> entity = new HttpEntity<>(keycloakUser, headers);

            ResponseEntity<UserRepresentation> response = restTemplate.exchange(
                    usersUrl + "/" + userId,
                    HttpMethod.PUT,
                    entity,
                    UserRepresentation.class
            );

            UserRepresentation updated = response.getBody();
            if (updated == null) {
                throw new KeycloakException("Usuário atualizado, mas resposta do Keycloak está vazia");
            }

            return updated;
        } catch (HttpClientErrorException e) {
            String errorMessage = String.format("Erro ao atualizar usuário (Status: %s): %s",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakException(errorMessage);
        } catch (HttpServerErrorException e) {
            String errorMessage = String.format("Erro no servidor Keycloak (Status: %s): %s",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakException(errorMessage);
        } catch (RestClientException e) {
            String errorMessage = String.format("Erro de conexão com Keycloak: %s", e.getMessage());
            throw new KeycloakException(errorMessage);
        } catch (Exception e) {
            String errorMessage = String.format("Erro inesperado ao atualizar usuário: %s", e.getMessage());
            throw new KeycloakException(errorMessage);
        }
    }

    private HttpHeaders createHeaders() {
        String token = keycloakTokenService.retrieveAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}