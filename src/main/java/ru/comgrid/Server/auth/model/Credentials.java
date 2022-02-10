package ru.comgrid.Server.auth.model;

public class Credentials {

    public enum CredentialType {
        ID_TOKEN, SESSION
    }

    private CredentialType type;
    private String idToken;
    private String session;

    public CredentialType getType() {
        return type;
    }

    public void setType(CredentialType type) {
        this.type = type;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Credentials(CredentialType type, String idToken, String session) {
        this.type = type;
        this.idToken = idToken;
        this.session = session;
    }
}