package com.app.ecom_application.services;

import com.app.ecom_application.dto.AddressDTO;
import com.app.ecom_application.dto.UserRequest;
import com.app.ecom_application.dto.UserResponse;
import com.app.ecom_application.models.Address;
import com.app.ecom_application.models.User;
import com.app.ecom_application.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponse> fetchAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Optional<UserResponse> fetchUser(Long id) {
        return userRepository.findById(id).map(this::convertToResponse);
    }

    public void addUser(UserRequest userRequest) {
        User user = new User();
        updateUserFromRequest(userRequest, user);
        userRepository.save(user);
    }

    public Optional<UserResponse> updateUser(UserRequest userRequest, Long id) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    updateUserFromRequest(userRequest, existingUser);
                    User savedUser = userRepository.save(existingUser);
                    return convertToResponse(savedUser);
                });
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setUserRole(user.getUserRole());
        
        if (user.getAddress() != null) {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setStreet(user.getAddress().getStreet());
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setState(user.getAddress().getState());
            addressDTO.setCountry(user.getAddress().getCountry());
            addressDTO.setZipcode(user.getAddress().getZipcode());
            response.setAddress(addressDTO);
        }
        
        return response;
    }

    private void updateUserFromRequest(UserRequest userRequest, User user) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        
        if (userRequest.getAddress() != null) {
            Address address = user.getAddress();
            if (address == null) {
                address = new Address();
            }
            address.setStreet(userRequest.getAddress().getStreet());
            address.setCity(userRequest.getAddress().getCity());
            address.setState(userRequest.getAddress().getState());
            address.setCountry(userRequest.getAddress().getCountry());
            address.setZipcode(userRequest.getAddress().getZipcode());
            user.setAddress(address);
        } else {
            user.setAddress(null);
        }
    }
}
