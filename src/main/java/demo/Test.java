package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class Test {

	
	
	public static void main(String[] args) {
		
       Photo p = new Photo("1", true);
       Photo p2 = new Photo("2", true);
       List<Photo> photos = new ArrayList<>();
       
       photos.add(p); photos.add(p2);
       
       
       Map<String, Object> in = new HashMap<>();
       in.put("photos", photos);
       in.put("id", 67);
       
       Gson gson = new Gson();
       String jsonStr = gson.toJson(in);
       
       System.out.println(jsonStr);
       
      // Map obj = gson.fromJson(jsonStr, Map<>.class);
       
       
		
		
		
	}
}
