package com.app.ecom_application;

import com.app.ecom_application.dto.AddressDTO;
import com.app.ecom_application.dto.UserRequest;
import com.app.ecom_application.models.Address;
import com.app.ecom_application.models.User;
import com.app.ecom_application.repositories.UserRepository;
import com.app.ecom_application.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserAddressTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUserAddressPersistence() {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet("123 Main St");
        addressDTO.setCity("Springfield");
        addressDTO.setState("IL");
        addressDTO.setCountry("USA");

        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setEmail("john.doe@example.com");
        userRequest.setAddress(addressDTO);

        userService.addUser(userRequest);

        User savedUser = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals("john.doe@example.com"))
                .findFirst()
                .orElseThrow();

        assertThat(savedUser.getAddress()).isNotNull();
        assertThat(savedUser.getAddress().getStreet()).isEqualTo("123 Main St");
    }

    @Test
    void testUpdateUserAddress() {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setEmail("jane.smith@example.com");
        
        userRepository.save(user);
        Long id = user.getId();

        AddressDTO newAddressDTO = new AddressDTO();
        newAddressDTO.setStreet("456 Elm St");
        newAddressDTO.setCity("Shelbyville");
        newAddressDTO.setState("IL");
        newAddressDTO.setCountry("USA");

        UserRequest updateDetails = new UserRequest();
        updateDetails.setFirstName("Jane");
        updateDetails.setLastName("Doe");
        updateDetails.setEmail("jane.doe@example.com");
        updateDetails.setAddress(newAddressDTO);

        userService.updateUser(updateDetails, id);

        User updatedUser = userRepository.findById(id).orElseThrow();
        assertThat(updatedUser.getAddress()).isNotNull();
        assertThat(updatedUser.getAddress().getStreet()).isEqualTo("456 Elm St");
    }
}
