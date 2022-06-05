package com.bot.dto;

import lombok.Data;

@Data
public class Forecast {
	
	public String date;
	public String weekday;
	public int max;
	public int min;
	public String description;
	public String condition;
	
}
