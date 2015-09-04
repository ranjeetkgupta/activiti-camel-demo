package demo;

import java.util.ArrayList;
import java.util.List;

public class MessageEnvelop {

	public List<Photo> getPhotos() {
		return photos;
	}
	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}
	public String getExecutionId() {
		return executionId;
	}
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}
	List<Photo> photos  = new ArrayList<>();
	String executionId ;
	
	
	
	
}
