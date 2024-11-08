package app.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccessLevelServiceTest {

    @InjectMocks
    private AccessLevelService accessLevelService;

    @Mock
    private AccessLevelRepository accessLevelRepository;

    @Test
    void testSaveAccessLevel() {
        AccessLevel accessLevel = new AccessLevel();
        String result = accessLevelService.save(accessLevel);

        verify(accessLevelRepository, times(1)).save(accessLevel);
        // verify return
        assertEquals("Nivel de acesso salvo com sucesso", result);
    }

    @Test
    void testFindByIdFound() {
        Long id = 1L;
        AccessLevel accessLevel = new AccessLevel();
        accessLevel.setId(id);

        when(accessLevelRepository.findById(id)).thenReturn(java.util.Optional.of(accessLevel));

        AccessLevel result = accessLevelService.findById(id);

        verify(accessLevelRepository, times(1)).findById(id);
        assertEquals(accessLevel, result);
    }

    @Test
    void testFindByIdNotFound() {
        Long id = 1L;

        when(accessLevelRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accessLevelService.findById(id));
        verify(accessLevelRepository, times(1)).findById(id);
    }

    @Test
    void testFindAllWithAccessLevels() {
        AccessLevel accessLevel1 = new AccessLevel();
        AccessLevel accessLevel2 = new AccessLevel();
        List<AccessLevel> accessLevelsList = List.of(accessLevel1, accessLevel2);

        // simulator return of repository
        when(accessLevelRepository.findAll()).thenReturn(accessLevelsList);

        List<AccessLevel> result = accessLevelService.findAll();

        // verify list return
        assertEquals(accessLevelsList, result);
        verify(accessLevelRepository, times(1)).findAll();
    }

    @Test
    void findAllAccessLevelEmpty() {
        when(accessLevelRepository.findAll()).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accessLevelService.findAll();
        });

        assertEquals("Não há niveis de acesso registrados!", exception.getMessage());
        verify(accessLevelRepository, times(1)).findAll();
    }

    @Test
    void testDeleteByIdSuccess() {
        long id = 1L;
        AccessLevel accessLevel = new AccessLevel();
        accessLevel.setId(id); // Assumindo que você tem um método setId

        // Simula o retorno do repositório
        when(accessLevelRepository.findById(id)).thenReturn(java.util.Optional.of(accessLevel));

        String result = accessLevelService.deleteById(id);

        verify(accessLevelRepository, times(1)).deleteById(id);
        assertEquals("Nível de acesso deletado com sucesso", result);
    }

    /*@Test
    void testDeleteByIdNotFound() {
        Long id = 1L;
        AccessLevel accessLevel = new AccessLevel();
        accessLevel.setId(id);

        // Simula que o repositório não encontrou o nível de acesso
        when(accessLevelRepository.findById(id)).thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accessLevelService.deleteById(id));
        assertEquals("Nível de acesso não encontrado com id: " + id, exception.getMessage());

        // Verifica que deleteById não foi chamado, pois o nível não foi encontrado
        verify(accessLevelRepository).deleteById(id);
    }*/


    @Test
    void testUpdateByIdSuccess() {
        Long id = 1L;
        AccessLevel existingAccessLevel = new AccessLevel();
        existingAccessLevel.setId(id);
        existingAccessLevel.setName("Old Name");

        AccessLevel updatedAccessLevel = new AccessLevel();
        updatedAccessLevel.setName("New Name");

        // Simula o retorno do repositório
        when(accessLevelRepository.findById(id)).thenReturn(java.util.Optional.of(existingAccessLevel));
        when(accessLevelRepository.save(existingAccessLevel)).thenReturn(existingAccessLevel);

        AccessLevel result = accessLevelService.updateById(id, updatedAccessLevel);

        verify(accessLevelRepository, times(1)).findById(id);
        verify(accessLevelRepository, times(1)).save(existingAccessLevel);
        assertEquals("New Name", result.getName());
    }

    @Test
    void testUpdateByIdNotFound() {
        Long id = 1L;
        AccessLevel updatedAccessLevel = new AccessLevel();
        updatedAccessLevel.setName("New Name");

        // Simula que o repositório não encontrou o nível de acesso
        when(accessLevelRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> accessLevelService.updateById(id, updatedAccessLevel));
        verify(accessLevelRepository, times(1)).findById(id);
    }
}
