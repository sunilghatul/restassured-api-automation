package jsonArrayAPI;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JsonArrayPoJo {
	
	private int page;
	private int per_page;
	private int total;
	private int total_pages;
	private List<UserData> UserData;
	private Support support;
	
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class UserData{
		
		private int id;
		private String email;
		private String firstname;
		private String lastname;
		private String avatar;
		
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class Support{
		private String url;
		private String text;
		
	}

}
