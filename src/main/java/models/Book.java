package models;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class Book {

	private String userId;
	private List<String> collectionOfIsbns;
	
	public Book(List<String> collectionOfIsbns) {
		this.collectionOfIsbns = collectionOfIsbns;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getCollectionOfIsbns() {
		return collectionOfIsbns;
	}

	public void setCollectionOfIsbns(List<String> collectionOfIsbns) {
		this.collectionOfIsbns = collectionOfIsbns;
	}
		
	
    public String getJsonBook(){
    	
    	JsonArray jsonArray = new JsonArray();
    	JsonObject bookJsonRepresentation = new JsonObject();
    	
    	for (String isbn_id : collectionOfIsbns) {
    		JsonObject isbn = new JsonObject();
    		isbn.addProperty("isbn", isbn_id);
    		jsonArray.add(isbn);
		}
    	        
        bookJsonRepresentation.addProperty("userId", this.userId);
        bookJsonRepresentation.add("collectionOfIsbns", jsonArray);
        return bookJsonRepresentation.toString();
    }
    
    public String getJsonDeleteBook(String isbn_id){
    	JsonObject bookJsonRepresentation = new JsonObject();    	        
    	bookJsonRepresentation.addProperty("isbn", isbn_id);
    	bookJsonRepresentation.addProperty("userId", this.userId);
    	this.collectionOfIsbns.remove(isbn_id);
        return bookJsonRepresentation.toString();
    }
	
}
