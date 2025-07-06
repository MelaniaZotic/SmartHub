package com.example.smarthub;



import com.example.smarthub.enums.Role;
import com.example.smarthub.models.User;
import com.example.smarthub.models.dtos.RegisterRequest;
import com.example.smarthub.repositories.UserRepository;
import com.example.smarthub.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test registerUser(RegisterRequest) - succes
    @Test
    void registerUser_shouldSaveStudent() {
        // given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@s.univ.ro");
        request.setPassword("rawpw");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpw");

        // when
        boolean result = userService.registerUser(request);

        // then
        assertThat(result).isTrue();
        verify(userRepository, times(1)).save(argThat(user ->
                user.getEmail().equals("test@s.univ.ro")
                        && user.getRoles().contains(Role.STUDENT)
                        && user.getPassword().equals("encodedpw")
        ));
    }

    // Test registerUser(RegisterRequest) - email existent
    @Test
    void registerUser_shouldFailIfEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@prof.univ.ro");
        request.setPassword("pw");

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        boolean result = userService.registerUser(request);

        assertThat(result).isFalse();
        verify(userRepository, never()).save(any());
    }

    // Test registerUser(RegisterRequest) - domeniu invalid
    @Test
    void registerUser_shouldFailIfInvalidDomain() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@gmail.com");
        request.setPassword("pw");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        boolean result = userService.registerUser(request);

        assertThat(result).isFalse();
        verify(userRepository, never()).save(any());
    }

    // Test approveUser
    @Test
    void approveUser_shouldSetEnabledTrue() {
        User user = new User();
        user.setEnabled(false);
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.approveUser(1L);

        assertThat(user.isEnabled()).isTrue();
        verify(userRepository).save(user);
    }

    // Test getPendingUsers
    @Test
    void getPendingUsers_shouldReturnList() {
        List<User> pending = List.of(new User(), new User());
        when(userRepository.findByEnabledFalse()).thenReturn(pending);

        List<User> result = userService.getPendingUsers();

        assertThat(result).hasSize(2);
        verify(userRepository, times(1)).findByEnabledFalse();
    }

    // Test varianta veche registerUser(username, rawPw)
    @Test
    void registerUser_stringParams_shouldSaveStudentWithEncodedPassword() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpw");

        userService.registerUser("myusername", "myrawpw");

        verify(userRepository, times(1)).save(argThat(user ->
                user.getRoles().contains(Role.STUDENT)
                        && user.getPassword().equals("encodedpw")
                        && !user.isEnabled() // trebuie sa fie false
        ));
    }
}
