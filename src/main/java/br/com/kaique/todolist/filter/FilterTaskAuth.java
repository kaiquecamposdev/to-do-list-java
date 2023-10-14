package br.com.kaique.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.kaique.todolist.user.IUserRepository;
import br.com.kaique.todolist.user.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

    String servletPath = req.getServletPath();

    if(servletPath.startsWith("/tasks/")) {

      String authEncoded = req.getHeader("Authorization").substring("Basic".length()).trim();
      byte[] authDecode = Base64.getDecoder().decode(authEncoded);
      String authDecodeInString = new String(authDecode);

      String[] credentials = authDecodeInString.split(":");
      String username = credentials[0];
      String password = credentials[1];

      UserModel user = this.userRepository.findByUsername(username);
      if(user == null) {
        res.sendError(401, "Usuário não autorizado!");
      } else {

        boolean passwordVerified = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()).verified;

        if(passwordVerified) {
          req.setAttribute("userId", user.getId());
          filterChain.doFilter(req, res);
        } else {
          res.sendError(401, "Usuário não autorizado!");
        }
      } 
    }
    else {
      filterChain.doFilter(req, res);
    }
  } 
}