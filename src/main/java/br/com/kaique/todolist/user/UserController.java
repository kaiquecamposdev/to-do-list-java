package br.com.kaique.todolist.user;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private IUserRepository userRepository;

  @PostMapping("/")
  public ResponseEntity<String> createUser(@RequestBody UserModel userModel) {
    UserModel user = this.userRepository.findByUsername(userModel.getUsername());

    if(user != null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Esse usuário já existe!");
    }

    String passwordEncrypted = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
    userModel.setPassword(passwordEncrypted);

    this.userRepository.save(userModel);
    return ResponseEntity.status(HttpStatus.CREATED).body("Usuário criado com sucesso!");
  }

  @GetMapping("/")
  public ResponseEntity<List<UserModel>> getUsers() {
    List<UserModel> users = this.userRepository.findAll();

    if(users.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    return ResponseEntity.status(HttpStatus.OK).body(users);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
    boolean userExist = this.userRepository.findById(id).isPresent(); 

    if(!userExist) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não existe!");
    }

    this.userRepository.deleteById(id);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body("Usuário removido com sucesso!");  
  }
}
