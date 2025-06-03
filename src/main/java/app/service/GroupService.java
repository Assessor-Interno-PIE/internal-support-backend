package app.service;

import app.auth.service.TokenService;
import app.dto.CreateGroupDto;
import app.dto.GroupDto;
import app.exception.handler.KeycloakException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.domain.Pageable;

import java.util.*;

@Service
public class GroupService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final TokenService tokenService;
    private final LogService logService;

    @Value("${keycloak.admin.groups-url}")
    private String groupsUrl;

    public GroupService(TokenService tokenService, LogService logService) {
        this.tokenService = tokenService;
        this.logService = logService;
    }

    // Criar novo grupo
    public CreateGroupDto save(CreateGroupDto dto) {
        try {
            HttpHeaders headers = getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CreateGroupDto> entity = new HttpEntity<>(dto, headers);

            ResponseEntity<CreateGroupDto> response = restTemplate.exchange(
                    groupsUrl, HttpMethod.POST, entity, CreateGroupDto.class
            );

            logService.registrar("Salvar", "", "/api/groups", "Novo grupo criado");

            return response.getBody();
        } catch (Exception e) {
            throw new KeycloakException("Erro ao criar grupo", e);
        }
    }

    // Buscar grupo por ID
    public GroupDto findById(String id) {
        try {
            HttpHeaders headers = getHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<GroupDto> response = restTemplate.exchange(
                    groupsUrl + "/" + id,
                    HttpMethod.GET,
                    entity,
                    GroupDto.class
            );

            GroupDto group = response.getBody();
            if (group == null) {
                throw new NoSuchElementException("Grupo não encontrado com id: " + id);
            }

            logService.registrar("Busca", "", "/api/groups/" + id, "Grupo buscado por ID");

            return group;
        } catch (Exception e) {
            throw new KeycloakException("Erro ao buscar grupo por ID", e);
        }
    }

    // Buscar todos os grupos
    public List<GroupDto> findAll() {
        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            ResponseEntity<GroupDto[]> response = restTemplate.exchange(
                    groupsUrl, HttpMethod.GET, entity, GroupDto[].class
            );

            List<GroupDto> groups = Arrays.asList(Objects.requireNonNull(response.getBody()));
            if (groups.isEmpty()) {
                throw new RuntimeException("Não há grupos registrados!");
            }

            logService.registrar("Busca", "", "/api/groups", "Busca de todos os grupos");

            return groups;
        } catch (Exception e) {
            throw new KeycloakException("Erro ao listar grupos", e);
        }
    }

    // Paginação
    public Page<GroupDto> findAllPaginated(Pageable pageable) {
        List<GroupDto> allGroups = findAll();

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = page * size;
        int end = Math.min(start + size, allGroups.size());

        if (start >= allGroups.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, allGroups.size());
        }

        List<GroupDto> paginatedGroups = allGroups.subList(start, end);

        logService.registrar("Busca", "", "/api/groups/paginated", "Busca de grupos paginada");

        return new PageImpl<>(paginatedGroups, pageable, allGroups.size());
    }

    // Atualizar grupo por ID
    public void updateById(String id, CreateGroupDto updatedGroup) {
        try {
            findById(id); // dispara exceção se não existir

            HttpHeaders headers = getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CreateGroupDto> entity = new HttpEntity<>(updatedGroup, headers);

            restTemplate.exchange(
                    groupsUrl + "/" + id,
                    HttpMethod.PUT,
                    entity,
                    Void.class
            );

            logService.registrar("Atualização", "", "/api/groups/" + id, "Atualização de grupo");
        } catch (Exception e) {
            throw new KeycloakException("Erro ao atualizar grupo", e);
        }
    }

    // Deletar grupo por ID
    public void deleteById(String id) {
        try {
            findById(id); // garante que o grupo existe

            HttpEntity<String> entity = new HttpEntity<>(getHeaders());

            restTemplate.exchange(
                    groupsUrl + "/" + id,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );

            logService.registrar("Delete", "", "/api/groups/" + id, "Grupo deletado");
        } catch (Exception e) {
            throw new KeycloakException("Erro ao deletar grupo", e);
        }
    }

    // Gerar headers com token
    private HttpHeaders getHeaders() {
        String token = tokenService.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}
