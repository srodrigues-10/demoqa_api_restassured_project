package requests;

import static io.restassured.RestAssured.given;

import helpers.Utilities;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Account;

public class AccountRequests {
	
	private AccountRequests() {	
	}
	
    public static Response postCreateAccountRequest(RequestSpecification spec, Account account){

        Response postAccountResponse =
                given().
                        spec(spec).
                        header("Content-Type", "application/json").
                and().
                        body(account.getJsonAccount()).
                        log().all().
                when().
                        post("/Account/v1/User");

        account.setUserID(Utilities.getValueFromResponse(postAccountResponse, "userID"));
        return postAccountResponse;
    }
    
	public static Response postGenerateTokenAccountRequest(RequestSpecification spec, Account account) {
		
		Response postAccountTokenResponse =
                given()
                        .spec(spec)
                        .header("Content-Type", "application/json")
                .and()
                        .body(account.getJsonAccount())
                        .log().all()
                .when()
                        .post("/Account/v1/GenerateToken")
            	;
        		
				account.setAuthorizationToken(Utilities.getValueFromResponse(postAccountTokenResponse, "token"));
        		
        		return postAccountTokenResponse;
	}    

	public static Response deleteAccountRequest(RequestSpecification spec, Account account) {
		
		return 
                given()
                        .spec(spec)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + account.getAuthorizationToken())
                        .pathParams("UUID", account.getUserID())
                        .log().all()
                .when()
                        .delete("/Account/v1/User/{UUID}")
            	;
		
	}
	
}
