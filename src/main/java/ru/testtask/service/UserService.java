package ru.testtask.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.testtask.exception.WrongMethodUseException;
import ru.testtask.model.User;
import ru.testtask.repo.UserRepo;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
            User user = userRepo.findByUsername(username);

            if (user == null) {
                throw new UsernameNotFoundException(String.format("User with username %s does not exists", username));
            }

            return user;
    }

    public User getUserByUsername(String username){
        return userRepo.findByUsername(username);
    }

    public String getCurrentUserId() throws WrongMethodUseException{

        if (SecurityContextHolder.getContext().getAuthentication() == null)
            throw new WrongMethodUseException("Method can't be used from this thread.");

        else {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return getUserByUsername(user.getUsername()).getId();
        }
    }

    public Optional<User> getUserById(String id){
        return userRepo.findById(id);
    }

    public void updateUser(User user){
        userRepo.save(user);
    }
}
