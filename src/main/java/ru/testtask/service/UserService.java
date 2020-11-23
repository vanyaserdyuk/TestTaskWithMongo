package ru.testtask.service;

import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.testtask.exception.NameAlreadyExistsException;
import ru.testtask.exception.WrongMethodUseException;
import ru.testtask.model.Role;
import ru.testtask.model.User;
import ru.testtask.repo.UserRepo;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserService{

    private final UserRepo userRepo;

    private final MongoTemplate mongoTemplate;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init(){
        mongoTemplate.indexOps("users").ensureIndex(new Index("username", Sort.Direction.ASC).unique());
        createDefaultUsers();
    }

    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public String getCurrentUserId() throws WrongMethodUseException {

        if (SecurityContextHolder.getContext().getAuthentication() == null)
            throw new WrongMethodUseException("Method can't be used from this thread.");

        else {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return getUserByUsername(user.getUsername()).getId();
        }
    }

    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

    public void updateUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
    }

    public void createDefaultUsers(){
        createDefaultUser("user", "user", Collections.singleton(Role.USER));
        createDefaultUser("admin", "admin", Collections.singleton(Role.ADMIN));
    }

    public void createDefaultUser(String username, String password, Set<Role> roles) {
            try {
                User user = User.builder().username(username).password(password).roles(roles).build();
                createUser(user);
            } catch (MongoWriteException e) {
                log.error("Impossible to write this user to a database");
            } catch (NameAlreadyExistsException e){
                log.info("Default users are created");
            }
    }

    public User createUser(User user){
        if (isUsernameAlreadyPicked(user.getUsername())) {
            throw new NameAlreadyExistsException("User with the same name already exists!");
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepo.insert(user);
        }
    }

    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    public void deleteUser(String id){
        userRepo.deleteById(id);
    }

    public boolean isUsernameAlreadyPicked(String username){
        return getUserByUsername(username) != null;
    }

    public User getAuthorizedUser(String username, String password) {
        User user = userRepo.findByUsername(username);
        if (user != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

}
