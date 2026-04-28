package component.steps.support;

import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Getter
@Setter
public class SharedState {

    private ResponseEntity<String> response;

    private AuthenticatedUser authenticatedUser;




    public String getUsername() {
        return authenticatedUser.getUsername();
    }

    public String getToken() {
        return authenticatedUser.getToken();
    }

   public String getFirstName() {
        return authenticatedUser.getFirstName();
    }

    public String getLastName() {
        return authenticatedUser.getLastName();
    }


    public void initializeAuthenticatedUser(String username,String token, String firstName, String lastName) {
        AuthenticatedUser user = new AuthenticatedUser();
        user.setUsername(username);
        user.setToken(token);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        this.authenticatedUser = user;
    }

    public void clearAuthentication() {
        this.authenticatedUser = null;
    }

    public HttpHeaders authHeaders() {
        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        return headers;
    }

    public HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public HttpHeaders authHeaders(String token) {
        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(token);
        return headers;
    }


}