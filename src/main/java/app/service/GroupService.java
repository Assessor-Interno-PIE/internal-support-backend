package app.service;

import app.auth.service.KeycloakTokenService;
import app.dto.GroupCreationRequest;
import app.dto.GroupRepresentation;
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
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class GroupService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final KeycloakTokenService KeycloakTokenService;

    @Value("${keycloak.admin.groups-url}")
    private String groupsUrl;

    public GroupService(KeycloakTokenService keycloakTokenService) {
        this.KeycloakTokenService = keycloakTokenService;
    }

    public GroupCreationRequest createGroup(GroupCreationRequest groupDto) {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GroupCreationRequest> entity = new HttpEntity<>(groupDto, headers);

            ResponseEntity<GroupCreationRequest> response = restTemplate.exchange(
                    groupsUrl, HttpMethod.POST, entity, GroupCreationRequest.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new KeycloakException("Erro ao criar grupo: " + e.getMessage());
        }
    }

    public Page<GroupRepresentation> listGroupsPaginated(Pageable pageable) {
        List<GroupRepresentation> allGroups = listAllGroups();

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = page * size;
        int end = Math.min(start + size, allGroups.size());

        if (start >= allGroups.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, allGroups.size());
        }

        List<GroupRepresentation> paginatedGroups = allGroups.subList(start, end);
        return new PageImpl<>(paginatedGroups, pageable, allGroups.size());
    }

    public GroupRepresentation getGroupById(String groupId) {
        String token = KeycloakTokenService.retrieveAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GroupRepresentation> response = restTemplate.exchange(
                groupsUrl + "/" + groupId,
                HttpMethod.GET,
                entity,
                GroupRepresentation.class
        );

        GroupRepresentation group = response.getBody();
        if (group == null) {
            throw new EntityNotFoundException("Grupo não encontrado com ID: " + groupId);
        }

        return group;
    }

    public List<GroupRepresentation> listAllGroups() {
        try {
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<GroupRepresentation[]> response = restTemplate.exchange(
                    groupsUrl, HttpMethod.GET, entity, GroupRepresentation[].class
            );
            List<GroupRepresentation> groups = Arrays.asList(Objects.requireNonNull(response.getBody()));
            if (groups.isEmpty()) {
                throw new KeycloakException("Nenhum grupo encontrado");
            }
            return groups;
        } catch (Exception e) {
            throw new KeycloakException("Erro ao listar grupos: " + e.getMessage());
        }
    }

    public void deleteGroupById(String groupId) {
        getGroupById(groupId);

        String token = KeycloakTokenService.retrieveAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        restTemplate.exchange(
                groupsUrl + "/" + groupId,
                HttpMethod.DELETE,
                entity,
                Void.class
        );
    }

    public void updateGroupById(String groupId, GroupCreationRequest updatedGroup) {
        getGroupById(groupId);

        String token = KeycloakTokenService.retrieveAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GroupCreationRequest> entity = new HttpEntity<>(updatedGroup, headers);

        restTemplate.exchange(
                groupsUrl + "/" + groupId,
                HttpMethod.PUT,
                entity,
                Void.class
        );
    }

    private HttpHeaders createHeaders() {
        String token = KeycloakTokenService.retrieveAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}