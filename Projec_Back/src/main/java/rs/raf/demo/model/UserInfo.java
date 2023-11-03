package rs.raf.demo.model;

import lombok.Data;

@Data
public class UserInfo {
    private Long id;
    private String name;
    private String lastName;
    private String mail;
    private Role[] roles;
}
