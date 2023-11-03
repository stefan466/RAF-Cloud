package rs.raf.demo.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.Role;
import rs.raf.demo.model.User;
import rs.raf.demo.model.UserInfo;
import rs.raf.demo.repositories.RoleRepository;
import rs.raf.demo.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    public void saveRole(Role role) {
        roleRepository.save(role);
    }


    public void addRoleToUser(String mail, String roleName) {
        Role role = roleRepository.findByName(roleName);
        User user = userRepository.findByMail(mail);
        user.getRoles().add(role);
        userRepository.save(user);
    }


    public User updateUser(UserInfo user) {
        User newUser = userRepository.getById(user.getId());
        newUser.setName(user.getName());
        newUser.setLastName(user.getLastName());
        newUser.setMail(user.getMail());
        return userRepository.save(newUser);
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    public List<User> getUsers() {
        return userRepository.findAll();
    }


    public List<Role> rolesForUser(String mail) {
        User user = userRepository.findByMail(mail);
        return new ArrayList<>(user.getRoles());
    }


    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }


    public User getUserByMail(String mail) {
        return userRepository.findByMail(mail);
    }

    public List<Role> getRoles(){
        return roleRepository.findAll();
    }
}
