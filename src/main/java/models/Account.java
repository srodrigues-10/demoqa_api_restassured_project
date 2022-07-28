package models;

import com.google.gson.JsonObject;

public class Account {

	private String userName;
	private String password;
	private String userID;
	private String authorizationToken;
	
	public Account(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}
	
    public void setPassword(String password) {
		this.password = password;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	public String getAuthorizationToken() {
		return authorizationToken;
	}

	public void setAuthorizationToken(String authorizationToken) {
		this.authorizationToken = authorizationToken;
	}

	public String getJsonAccount(){
        JsonObject userJsonRepresentation = new JsonObject();
        userJsonRepresentation.addProperty("userName", this.userName);
        userJsonRepresentation.addProperty("password", this.password);
        return userJsonRepresentation.toString();
    }
    
}
