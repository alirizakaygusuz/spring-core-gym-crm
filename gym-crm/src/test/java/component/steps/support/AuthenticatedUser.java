package component.steps.support;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticatedUser {
    private String username;
    private String firstName;
    private String lastName;
    private String token;
}
