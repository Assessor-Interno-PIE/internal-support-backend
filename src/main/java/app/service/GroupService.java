package app.service;

import app.auth.service.TokenService;
import app.dto.CreateGroupDto;
import app.dto.GroupDto;
import app.exception.handler.KeycloakException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class GroupService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final TokenService tokenService;

    @Value("${keycloak.admin.groups-url}")
    private String groupsUrl;

    public GroupService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public CreateGroupDto save(CreateGroupDto dto) {
        try {
            HttpHeaders headers = getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CreateGroupDto> entity = new HttpEntity<>(dto, headers);

            ResponseEntity<CreateGroupDto> response = restTemplate.exchange(
                    groupsUrl, HttpMethod.POST, entity, CreateGroupDto.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new KeycloakException("Erro ao criar grupo", e);
        }
    }

    public GroupDto findById(String id) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            ResponseEntity<GroupDto> response = restTemplate.exchange(
                    groupsUrl + "/" + id, HttpMethod.GET, entity, GroupDto.class
            );
            return Objects.requireNonNull(response.getBody(), "Grupo não encontrado com id: " + id);
        } catch (Exception e) {
            throw new KeycloakException("Erro ao buscar grupo com id: " + id, e);
        }
    }

    public List<GroupDto> findAll() {
        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            ResponseEntity<GroupDto[]> response = restTemplate.exchange(
                    groupsUrl, HttpMethod.GET, entity, GroupDto[].class
            );
            List<GroupDto> groups = Arrays.asList(Objects.requireNonNull(response.getBody()));
            if (groups.isEmpty()) throw new KeycloakException("Nenhum grupo encontrado");
            return groups;
        } catch (Exception e) {
            throw new KeycloakException("Erro ao listar grupos", e);
        }
    }

    public void deleteById(String id) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            restTemplate.exchange(groupsUrl + "/" + id, HttpMethod.DELETE, entity, Void.class);
        } catch (Exception e) {
            throw new KeycloakException("Erro ao deletar grupo com id: " + id, e);
        }
    }

    public void updateById(String id, CreateGroupDto updatedGroup) {
        String token = tokenService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateGroupDto> entity = new HttpEntity<>(updatedGroup, headers);

        restTemplate.exchange(
                groupsUrl + "/" + id,
                HttpMethod.PUT,
                entity,
                Void.class // Não espera corpo de resposta
        );
    }


    private HttpHeaders getHeaders() {
        String token = tokenService.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}
