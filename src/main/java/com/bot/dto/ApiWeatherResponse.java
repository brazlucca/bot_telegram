package com.bot.dto;

import lombok.Data;

@Data
public class ApiWeatherResponse {
	
	private String by;
	private boolean valid_key;
	private Results results;
	private double execution_time;
	private boolean from_cache;
	
	
}
