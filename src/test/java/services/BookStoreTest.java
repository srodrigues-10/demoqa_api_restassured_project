package services;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import enums.Isbn;
import helpers.Utilities;
import io.restassured.response.Response;
import models.Account;
import models.Book;
import requests.AccountRequests;

@TestMethodOrder(OrderAnnotation.class)
public class BookStoreTest extends TestBase{

	private static Account validAccount1;
	private static Account invalidAccount1;
	private static Account invalidAccount2;
	private static Book booksValidAccount1;
	
	
	@BeforeAll
    public static void generateTestData(){
    	//Users
        validAccount1 = new Account("tcc_pujucan_eta_2021_1", "@123456ABCabc");
        invalidAccount1 = new Account("tcc_vitoria_eta_2021_1", "@123456ABCabc");
        invalidAccount2 = new Account("tcc_pujucan_eta_2021_1", "@123456ABCabc");
        
        //Creating Accounts Before
        AccountRequests.postCreateAccountRequest(SPEC, invalidAccount1).then().statusCode(201);
        AccountRequests.postGenerateTokenAccountRequest(SPEC, invalidAccount1).then().statusCode(200);
        
        //Change for invalid password
        invalidAccount1.setPassword("invalidPass123");
        
        //Isbn List
        List<String> listIsbn1 = new ArrayList<>();
        listIsbn1.add(Isbn.Designing_Evolvable_Web_APIs_with_ASP_NET);
        listIsbn1.add(Isbn.Git_Pocket_Guide);
        listIsbn1.add(Isbn.Speaking_JavaScript);
        
        //Books
        booksValidAccount1 = new Book(listIsbn1);
    }
    
    
    @Test
    @Order(1)
    @DisplayName("CY001 - Create account successfully")
    public void createAccountSuccessfully() {

        		Response postAccountResponse =
                given()
                        .spec(SPEC)
                        .header("Content-Type", "application/json")
                .and()
                        .body(validAccount1.getJsonAccount())
                        .log().all()
                .when()
                        .post("/Account/v1/User")
            	;

        		postAccountResponse
        		.then()
        			.log().all()
        			.statusCode(201)
        		;
        		
        		validAccount1.setUserID(Utilities.getValueFromResponse(postAccountResponse, "userID"));
        		
    }
    
    @Test
    @Order(2)
    @DisplayName("CY002 - Error creating account for existing account")
    public void createAccountError() {

        		Response postAccountResponse =
                given()
                        .spec(SPEC)
                        .header("Content-Type", "application/json")
                .and()
                        .body(invalidAccount2.getJsonAccount())
                        .log().all()
                .when()
                        .post("/Account/v1/User")
            	;

        		postAccountResponse
        		.then()
        			.log().all()
        			.statusCode(406)
        			.body("code", Matchers.is("1204"))
        			.body("message", Matchers.is("User exists!"))
        		;        		
        		
    }
    
    @Test
    @Order(3)
    @DisplayName("CY003 - Generate account token successfully")
    public void accountTokenGenerateSuccessfully() {

        		Response postAccountTokenResponse =
                given()
                        .spec(SPEC)
                        .header("Content-Type", "application/json")
                .and()
                        .body(validAccount1.getJsonAccount())
                        .log().all()
                .when()
                        .post("/Account/v1/GenerateToken")
            	;

        		postAccountTokenResponse
        		.then()
        			.log().all()
        			.statusCode(200)
        			.body("status", Matchers.is("Success"))
        			.body("result", Matchers.is("User authorized successfully."))
        		;
        		
        		validAccount1.setAuthorizationToken(Utilities.getValueFromResponse(postAccountTokenResponse, "token"));
        		
    }
    
    @Test
    @Order(4)
    @DisplayName("CY004 - Account authorized successfully")
    public void accountAuthorizedSuccessfully() {

        		Response postAccountResponse =
                given()
                        .spec(SPEC)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + validAccount1.getAuthorizationToken())
                .and()
                        .body(validAccount1.getJsonAccount())
                        .log().all()
                .when()
                        .post("/Account/v1/Authorized")
            	;

        		postAccountResponse
        		.then()
        			.log().all()
        			.statusCode(200)
        		;
        		    	
    }
    
    @Test
    @Order(5)
    @DisplayName("CY005 - Account not authorized for reason 'Not Found'")
    public void accountNotAuthorized() {
    	
        		Response postAccountResponse =
                given()
                        .spec(SPEC)
                        .header("Content-Type", "application/json")
                .and()
                        .body(invalidAccount1.getJsonAccount())
                        .log().all()
                .when()
                        .post("/Account/v1/Authorized")
            	;

        		postAccountResponse
        		.then()
        			.log().all()
        			.statusCode(404)
        			.body("code", Matchers.is("1207"))
        			.body("message", Matchers.is("User not found!"))
        		;
        		    	
    }
    
    @Test
    @Order(6)
    @DisplayName("CY006 - Consult existing account in the database")
    public void consultAccountSuccessfully() {
    	
        		Response getAccountResponse =
                given()
                        .spec(SPEC)
                        .header("Authorization", "Bearer " + validAccount1.getAuthorizationToken())
                        .pathParams("UUID", validAccount1.getUserID())
                .and()
                        .log().all()
                .when()
                        .get("/Account/v1/User/{UUID}")
            	;

        		getAccountResponse
        		.then()
        			.log().all()
        			.statusCode(200)
        			.body("userId", Matchers.is(validAccount1.getUserID()))
        			.body("username", Matchers.is(validAccount1.getUserName()))
        		;
        		    	
    }
    
    @Test
    @Order(7)
    @DisplayName("CY007 - Consult all books in the database")
    public void consultAllBooksSuccessfully() {
    	
        		Response getBooksResponse =
                given()
                        .spec(SPEC)
                .and()
                        .log().all()
                .when()
                        .get("/BookStore/v1/Books")
            	;

        		getBooksResponse
        		.then()
        			.log().all()
        			.statusCode(200)
        			.body("books", Matchers.hasSize(8))
        		;
        		    	
    }
    
    @Test
    @Order(8)
    @DisplayName("CY008 - Relate book to account successfully")
    public void relateBookToAccountSuccessfully() {

    			booksValidAccount1.setUserId(validAccount1.getUserID());
    	
        		Response postBookAccountResponse =
                given()
                        .spec(SPEC)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + validAccount1.getAuthorizationToken())
                .and()
                        .body(booksValidAccount1.getJsonBook())
                        .log().all()
                .when()
                        .post("/BookStore/v1/Books")
            	;

        		postBookAccountResponse
        		.then()
        			.log().all()
        			.statusCode(201)
        			.body("books", Matchers.hasSize(3))
        		;
        		    	
    }
    
    @Test
    @Order(9)
    @DisplayName("CY009 - Consult book by isbn")
    public void consultBookByIsbnSuccessfully() {
    	
        		Response getBookResponse =
                given()
                        .spec(SPEC)
                        .queryParam("ISBN", Isbn.Git_Pocket_Guide)
                .and()
                        .log().all()
                .when()
                        .get("/BookStore/v1/Book")
            	;

        		getBookResponse
        		.then()
        			.log().all()
        			.statusCode(200)
        			.body("isbn", Matchers.is(Isbn.Git_Pocket_Guide))
        		;
        		    	
    }
    
    @Test
    @Order(10)
    @DisplayName("CY010 - Delete account book by isbn successfully")
    public void deleteAccountBookByIsbnSuccessfully() {
    	
        		Response delBookAccountResponse =
                given()
                        .spec(SPEC)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + validAccount1.getAuthorizationToken())
                .and()
                        .body(booksValidAccount1.getJsonDeleteBook(Isbn.Speaking_JavaScript))
                        .log().all()
                .when()
                        .delete("/BookStore/v1/Book")
            	;

        		delBookAccountResponse
        		.then()
        			.log().all()
        			.statusCode(204)
        		;
        		    	
    }
    
    @Test
    @Order(11)
    @DisplayName("CY011 - Delete all account books successfully")
    public void deleteAllAccountBooksSuccessfully() {
    	
        		Response delAllBookAccountResponse =
                given()
                        .spec(SPEC)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + validAccount1.getAuthorizationToken())
                        .queryParam("UserId", validAccount1.getUserID())
                        .log().all()
                .when()
                        .delete("/BookStore/v1/Books")
            	;

        		delAllBookAccountResponse
        		.then()
        			.log().all()
        			.statusCode(204)
        		;
        		    	
    }
    
    @Test
    @Order(12)
    @DisplayName("CY012 - Delete account successfully")
    public void deleteAccountSuccessfully() {
    	
        		Response delAccountResponse =
                given()
                        .spec(SPEC)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + validAccount1.getAuthorizationToken())
                        .pathParams("UUID", validAccount1.getUserID())
                        .log().all()
                .when()
                        .delete("/Account/v1/User/{UUID}")
            	;

        		delAccountResponse
        		.then()
        			.log().all()
        			.statusCode(204)
        		;
        		    	
    }
    
	@AfterAll
    public static void deleteTestData(){
        //Removing Accounts After
        AccountRequests.deleteAccountRequest(SPEC, invalidAccount1).then().statusCode(204);
	}
	
}
