package be.helha.applicine.common.models.request;

import be.helha.applicine.server.ClientHandler;

import java.io.IOException;
import java.sql.SQLException;

public class CheckLoginRequest extends ClientEvent{

    private String username;
    private String password;

    public CheckLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void dispatchOn(ClientHandler clientHandler) throws IOException, SQLException {
        clientHandler.handleCheckLogin(this.username, this.password);
    }
}
