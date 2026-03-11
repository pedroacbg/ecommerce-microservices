package com.ecommerce.user.service;

import com.ecommerce.user.dto.AddressDTO;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.Address;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> fetchAllUsers(){
        return userRepository.findAll().stream().map(this::mapToUserResponse).collect(Collectors.toList());
    }

    public Optional<UserResponse> getUserById(Long id){
        return userRepository.findById(id).map(this::mapToUserResponse);
    }

    public String createUser(UserRequest request){
        User newUser = new User();
        updateUserFromRequest(newUser, request);
        userRepository.save(newUser);
        return "User created successfully";
    }

    public boolean updateUser(Long id, UserRequest request){
        return userRepository.findById(id).map(existingUser -> {
            updateUserFromRequest(existingUser, request);
            userRepository.save(existingUser);
            return true;
        }).orElse(false);
    }

    private UserResponse mapToUserResponse(User user){
        UserResponse response = new UserResponse();
        response.setId(user.getId().toString());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhoneNumber());
        response.setRole(user.getRole());

        if(user.getAddress() != null){
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setState(user.getAddress().getState());
            addressDTO.setStreet(user.getAddress().getStreet());
            addressDTO.setZipCode(user.getAddress().getZipCode());
            addressDTO.setCountry(user.getAddress().getCountry());
            response.setAddress(addressDTO);
        }
        return response;
    }

    private void updateUserFromRequest(User user, UserRequest request){
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        if(request.getAddress() != null){
            Address address = new Address();
            address.setCity(request.getAddress().getCity());
            address.setStreet(request.getAddress().getStreet());
            address.setState(request.getAddress().getState());
            address.setCountry(request.getAddress().getCountry());
            address.setZipCode(request.getAddress().getZipCode());
            user.setAddress(address);
        }
    }

}
