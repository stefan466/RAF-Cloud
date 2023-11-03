package rs.raf.demo.responses;

import lombok.Data;
import rs.raf.demo.model.Role;

import java.util.List;

@Data
public class LoginResponse {
    private String jwt;
    private List<Role> roles;

    public LoginResponse(String jwt, List<Role> roles){
        this.roles = roles;
        this.jwt = jwt;
    }
}
