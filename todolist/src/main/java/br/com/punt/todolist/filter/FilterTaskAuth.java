package br.com.punt.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.punt.todolist.user.IUserRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter{

    @Autowired
    private IUserRepository userRepository;
    
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {

        // Pegar a autenticação
        var authorization = request.getHeader("Authorization");
        var decode = authorization.substring("Basic".length()).trim();
        byte[] authDecode = Base64.getDecoder().decode(decode);
        var authString = new String(decode);
        String[] credentials = authString.split(":");
        String username = credentials[0];
        String password = credentials[1];
        
        // Validar usuário
        var user = this.userRepository.findByUsername(username);
        if (user == null){
            response.sendError(401);
        }
        else {
            var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
            if (passwordVerify.verified){
                filterChain.doFilter(request, response);
            }
        }
        // Validar senha

        // Seguir
        filterChain.doFilter(request, response);  

    }

}
