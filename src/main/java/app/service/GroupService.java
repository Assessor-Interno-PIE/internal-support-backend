package app.service;

import app.auth.service.TokenService;
import app.dto.GroupDTO;
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

    public GroupDTO save(GroupDTO groupDTO) {
        String token = tokenService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GroupDTO> entity = new HttpEntity<>(groupDTO, headers);

        ResponseEntity<GroupDTO> response = restTemplate.exchange(
                groupsUrl,
                HttpMethod.POST,
                entity,
                GroupDTO.class
        );

        return response.getBody();
    }

    public GroupDTO findById(String id) {
        String token = tokenService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GroupDTO> response = restTemplate.exchange(
                groupsUrl + "/" + id,
                HttpMethod.GET,
                entity,
                GroupDTO.class
        );

        return Objects.requireNonNull(response.getBody(), "Group not found with id: " + id);
    }

    public List<GroupDTO> findAll() {
        String token = tokenService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GroupDTO[]> response = restTemplate.exchange(
                groupsUrl,
                HttpMethod.GET,
                entity,
                GroupDTO[].class
        );

        List<GroupDTO> groups = Arrays.asList(Objects.requireNonNull(response.getBody()));
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

    public GroupDTO updateById(String id, GroupDTO updatedGroup) {
        String token = tokenService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GroupDTO> entity = new HttpEntity<>(updatedGroup, headers);

        ResponseEntity<GroupDTO> response = restTemplate.exchange(
                groupsUrl + "/" + id,
                HttpMethod.PUT,
                entity,
                GroupDTO.class
        );

        return Objects.requireNonNull(response.getBody(), "Group not found with id: " + id);
    }

    public List<GroupDTO> findGroupsByNameContaining(String keyword) {
        String token = tokenService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GroupDTO[]> response = restTemplate.exchange(
                groupsUrl + "?search=" + keyword,
                HttpMethod.GET,
                entity,
                GroupDTO[].class
        );

        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }
}