package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.Client;

import java.io.IOException;
import java.sql.SQLException;

public class CheckLoginRequest extends ClientEvent{

    private String username;
    private String password;
    private Client client;

    public CheckLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void dispatchOn(RequestVisitor requestVisitor) throws IOException, SQLException {
        requestVisitor.visit(this);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}
