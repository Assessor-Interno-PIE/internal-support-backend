package app.service;

import app.entity.User;
import app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailService userDetailService;

    private User user;

    @BeforeEach
    void setUp() {
        // Configurando um usuário de exemplo
        user = new User();
        user.setUsername("john_doe");
        user.setPassword("password123");
        user.setIsAdmin(1); // Simulando um admin
    }

    @Test
    void testLoadUserByUsername_Success() {

        when(userRepository.findByUsername("john_doe")).thenReturn(user);

        UserDetails userDetails = userDetailService.loadUserByUsername("john_doe");

        assertNotNull(userDetails);
        assertEquals("john_doe", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")));

        verify(userRepository).findByUsername("john_doe");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("non_existing_user")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userDetailService.loadUserByUsername("non_existing_user"));
    }
}
