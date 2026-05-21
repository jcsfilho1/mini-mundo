package br.com.minimundo.service;

import br.com.minimundo.entity.User;
import br.com.minimundo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User salvar(User user) {
        return userRepository.save(user);
    }
}