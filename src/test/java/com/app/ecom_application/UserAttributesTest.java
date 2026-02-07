package com.app.ecom_application;

import com.app.ecom_application.dto.UserRequest;
import com.app.ecom_application.dto.UserResponse;
import com.app.ecom_application.models.User;
import com.app.ecom_application.models.UserRole;
import com.app.ecom_application.repositories.UserRepository;
import com.app.ecom_application.services.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserAttributesTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUserAttributesPersistence() {
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setEmail("john.doe@example.com");
        userRequest.setPhone("1234567890");

        userService.addUser(userRequest);

        User savedUser = userRepository.findAll().get(0);
        assertThat(savedUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(savedUser.getPhone()).isEqualTo("1234567890");
        // Default role is CUSTOMER in Entity
        assertThat(savedUser.getUserRole()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    void testUpdateUserAttributes() {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setEmail("jane.smith@example.com");
        user.setPhone("0987654321");
        user.setUserRole(UserRole.CUSTOMER);

        userRepository.save(user);
        Long id = user.getId();

        UserRequest updateDetails = new UserRequest();
        updateDetails.setFirstName("Jane");
        updateDetails.setLastName("Doe");
        updateDetails.setEmail("jane.doe@example.com");
        updateDetails.setPhone("5555555555");

        userService.updateUser(updateDetails, id);

        User updatedUser = userRepository.findById(id).orElseThrow();
        assertThat(updatedUser.getLastName()).isEqualTo("Doe");
        assertThat(updatedUser.getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(updatedUser.getPhone()).isEqualTo("5555555555");
    }
}
