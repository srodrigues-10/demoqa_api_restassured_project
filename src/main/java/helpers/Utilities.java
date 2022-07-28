package helpers;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Utilities {

    public static String getValueFromResponse(Response response, String key){
        JsonPath jsonPathEvaluator = response.jsonPath();
        return jsonPathEvaluator.get(key);
    }
	
}
