package ru.testtask.service;

import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;

    private final MongoTemplate mongoTemplate;

    public UserService(UserRepo userRepo, MongoTemplate mongoTemplate) {
        this.userRepo = userRepo;
        this.mongoTemplate = mongoTemplate;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init(){
        mongoTemplate.indexOps("users").ensureIndex(new Index("username", Sort.Direction.ASC).unique());
        createDefaultUsers();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("User with username %s does not exists", username));
        }

        return user;
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
        userRepo.save(user);
    }

    public void createDefaultUsers(){
        createDefaultUser("user", "$2y$12$/wwVf0mDfYo4IRIT2jd0a.ks4wu/f7Np/NrGZJ6rxXUjG5UOs.Lb2", Collections.singleton(Role.USER));
        createDefaultUser("admin", "$2y$12$crhTzs9LTds5.3o1M.XaJO2wb6F4EnGa3GySy0odYcsdon8X.q3ye", Collections.singleton(Role.ADMIN));
    }

    public void createDefaultUser(String username, String password, Set<Role> roles) {
        if (getUserByUsername(username) == null) {
            try {
                User user = User.builder().username(username).password(password).roles(roles).build();
                userRepo.insert(user);
            } catch (MongoWriteException e) {
                log.error("Impossible to write this user to a database");
            }
        }
    }

    public User createUser(User user){
        if (getUserByUsername(user.getUsername()) != null) {
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

}
