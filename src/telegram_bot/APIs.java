package telegram_bot;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import telegram_bot.apis.dto.ApiAdviceResponse;
import telegram_bot.apis.dto.ApiTranslateResponse;
import telegram_bot.apis.dto.ApiJokeChuckNorrisResponse;
import telegram_bot.apis.dto.ApiWeatherResponse;

public class APIs {

	protected static ResponseEntity<ApiTranslateResponse> requestTradutorAPI(String textoParaTraduzir) {
		String urlTemplate = UriComponentsBuilder
				.fromHttpUrl("https://google-unlimited-multi-translate.p.rapidapi.com/api_translate_unlimited.php")
				.toUriString();
		RestTemplate restTemplate = new RestTemplate();
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_FORM_URLENCODED));
		restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);

		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("from", "en");
		requestBody.add("to", "pt");
		requestBody.add("text", textoParaTraduzir);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("X-RapidAPI-Host", "google-unlimited-multi-translate.p.rapidapi.com");
		headers.set("X-RapidAPI-Key", "c3b0f077edmshb45007e09904bb4p1bab87jsnee7d204a5dd4");

		HttpEntity<?> requestEntity = new HttpEntity<>(requestBody, headers);

		return restTemplate.exchange(urlTemplate, HttpMethod.POST, requestEntity, ApiTranslateResponse.class,
				headers);

	}

	protected static ResponseEntity<ApiAdviceResponse> requestConselhoAPI() {
		String urlTemplate = UriComponentsBuilder.fromHttpUrl("https://api.adviceslip.com/advice").toUriString();
		RestTemplate restTemplate = new RestTemplate();
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(
				Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_HTML));
		restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);

		return restTemplate.exchange(urlTemplate, HttpMethod.GET, null, ApiAdviceResponse.class);
	}

	protected static ResponseEntity<ApiWeatherResponse> requestTempoAPI() {
		String urlTemplate = UriComponentsBuilder.fromHttpUrl("https://api.hgbrasil.com/weather")
				.queryParam("key", "ba402e03").queryParam("city_name", "São Paulo").toUriString();
		RestTemplate restTemplate = new RestTemplate();

		return restTemplate.exchange(urlTemplate, HttpMethod.GET, null, ApiWeatherResponse.class);

	}
	
	protected static ResponseEntity<ApiJokeChuckNorrisResponse> requestJokeChuckNorrisAPI() {
		String urlTemplate = UriComponentsBuilder.fromHttpUrl("https://api.chucknorris.io/jokes/random")
				.toUriString();
		RestTemplate restTemplate = new RestTemplate();

		return restTemplate.exchange(urlTemplate, HttpMethod.GET, null, ApiJokeChuckNorrisResponse.class);

	}
}
