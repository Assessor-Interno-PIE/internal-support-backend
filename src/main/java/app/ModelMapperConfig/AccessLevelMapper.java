package app.ModelMapperConfig;

import app.DTO.AccessLevelDTO;
import app.entity.AccessLevel;
import app.entity.User;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AccessLevelMapper {

    @Autowired
    private UserRepository userRepository;

    public AccessLevelDTO toAccessLevelDTO(AccessLevel accessLevel) {
        AccessLevelDTO accessLevelDTO = new AccessLevelDTO();
        accessLevelDTO.setId(accessLevel.getId());
        accessLevelDTO.setName(accessLevel.getName());

        // Mapping user IDs
        List<Long> userIds = Optional.ofNullable(accessLevel.getUsers())
                .orElse(Collections.emptyList()).stream()
                .map(User::getId)
                .collect(Collectors.toList());
        accessLevelDTO.setUserIds(userIds);

        return accessLevelDTO;
    }

    public AccessLevel toAccessLevel(AccessLevelDTO accessLevelDTO) {
        AccessLevel accessLevel = new AccessLevel();
        accessLevel.setId(accessLevelDTO.getId());
        accessLevel.setName(accessLevelDTO.getName());

        // Mapping users
        List<User> users = Optional.ofNullable(accessLevelDTO.getUserIds())
                .orElse(Collections.emptyList()).stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id)))
                .collect(Collectors.toList());
        accessLevel.setUsers(users);

        return accessLevel;
    }
}
