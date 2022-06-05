package telegram_bot.apis.dto;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Results {
	private int temp;
	private String date;
	private String time;
	private String condition_code;
	private String description;
	private String currently;
	private String cid;
	private String city;
	private String img_id;
	private int humidity;
	private String wind_speedy;
	private String sunrise;
	private String sunset;
	private String condition_slug;
	private String city_name;
	private ArrayList<Forecast> forecast;
}
