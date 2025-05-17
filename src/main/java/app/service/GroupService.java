package app.service;

import app.auth.service.TokenService;
import app.dto.CreateGroupDto;
import app.dto.GroupDto;
import app.exception.handler.KeycloakException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.data.domain.Pageable;
import java.util.Arrays;
import java.util.Collections;
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

    public Page<GroupDto> findAllPaginated(Pageable pageable) {
        List<GroupDto> allGroups = findAll(); // chama o método que faz o request ao Keycloak

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        int start = page * size;
        int end = Math.min(start + size, allGroups.size());

        if (start >= allGroups.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, allGroups.size());
        }

        List<GroupDto> paginatedGroups = allGroups.subList(start, end);
        return new PageImpl<>(paginatedGroups, pageable, allGroups.size());
    }


    public GroupDto findById(String id) {
        String token = tokenService.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GroupDto> response = restTemplate.exchange(
                groupsUrl + "/" + id,
                HttpMethod.GET,
                entity,
                GroupDto.class
        );

        GroupDto group = response.getBody();
        if (group == null) {
            throw new EntityNotFoundException("Grupo não encontrado com id: " + id);
        }

        return group;
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
        findById(id);

        String token = tokenService.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        restTemplate.exchange(
                groupsUrl + "/" + id,
                HttpMethod.DELETE,
                entity,
                Void.class
        );
    }


    public void updateById(String id, CreateGroupDto updatedGroup) {
        findById(id);

        String token = tokenService.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateGroupDto> entity = new HttpEntity<>(updatedGroup, headers);

        restTemplate.exchange(
                groupsUrl + "/" + id,
                HttpMethod.PUT,
                entity,
                Void.class
        );
    }



    private HttpHeaders getHeaders() {
        String token = tokenService.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}
