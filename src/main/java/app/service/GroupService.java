package app.service;

import app.auth.service.TokenService;
import app.dto.CreateGroupDto;
import app.dto.GroupDto;
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

    public CreateGroupDto save(CreateGroupDto createGroupDto) {
        String token = tokenService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateGroupDto> entity = new HttpEntity<>(createGroupDto, headers);

        ResponseEntity<CreateGroupDto> response = restTemplate.exchange(
                groupsUrl,
                HttpMethod.POST,
                entity,
                CreateGroupDto.class
        );

        return response.getBody();
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

        return Objects.requireNonNull(response.getBody(), "Group not found with id: " + id);
    }

    public List<GroupDto> findAll() {
        String token = tokenService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GroupDto[]> response = restTemplate.exchange(
                groupsUrl,
                HttpMethod.GET,
                entity,
                GroupDto[].class
        );

        List<GroupDto> groups = Arrays.asList(Objects.requireNonNull(response.getBody()));
        if (groups.isEmpty()) {
            throw new RuntimeException("No groups found!");
        }
        return groups;
    }

    public String deleteById(String id) {
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

        return "Group deleted successfully.";
    }

    public GroupDto updateById(String id, GroupDto updatedGroup) {
        String token = tokenService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GroupDto> entity = new HttpEntity<>(updatedGroup, headers);

        ResponseEntity<GroupDto> response = restTemplate.exchange(
                groupsUrl + "/" + id,
                HttpMethod.PUT,
                entity,
                GroupDto.class
        );

        return Objects.requireNonNull(response.getBody(), "Group not found with id: " + id);
    }
}