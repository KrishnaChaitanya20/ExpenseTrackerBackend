package dev.dkc.expensetracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.dkc.expensetracker.exception.UserAlreadyExistsException;
import dev.dkc.expensetracker.exception.UserNotFoundException;
import dev.dkc.expensetracker.model.User;
import dev.dkc.expensetracker.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(User user) throws UserAlreadyExistsException {

        if (this.getUserByEmail(user.getEmail()) != null) {
            throw new UserAlreadyExistsException("User with email: " + user.getEmail() + " already exists");
        }
        // user.setUserId(UUID.randomUUID().toString());
        return userRepository.save(user);
    }
    
    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    
    public boolean userExists(String id) {
        return userRepository.existsById(id);
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user, String id) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            throw new UserNotFoundException("User with id " + id + " does not exist.");
        }
        if(user.getName()!=null)
            existingUser.setName(user.getName());
        if(user.getEmail()!=null)
            existingUser.setEmail(user.getEmail());
        if(user.getPassword()!=null)
            existingUser.setPassword(user.getPassword());
        if(user.getCategories()!=null)
            existingUser.setCategories(user.getCategories());
        if(user.getParentCategories()!=null)
            existingUser.setParentCategories(user.getParentCategories());
        userRepository.save(existingUser);

        return existingUser;
    }
}
