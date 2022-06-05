package com.bot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;

import com.bot.dto.ApiAdviceResponse;
import com.bot.dto.ApiJokeChuckNorrisResponse;
import com.bot.dto.ApiTranslateResponse;
import com.bot.dto.ApiWeatherResponse;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

public class Main {
	
	private static Map<String, String> env = System.getenv();

	private static TelegramBot bot = new TelegramBot(env.get("token_telegram"));
	
	private static String token_rapid_key = env.get("token_rapid_key");
	
	private static String token_weather = env.get("token_weather"); 

	// Objeto responsavel por receber as mensagens.
	private static GetUpdatesResponse updatesResponse;

	// Objeto responsavel por gerenciar o envio de respostas.
	private static SendResponse sendResponse;

	// Objeto responsavel por gerenciar o envio de acoes do chat.
	private static BaseResponse baseResponse;

	private static String tab = "     ";
	
	
	private static String funcionalidades = new StringBuilder()
								.append(tab).append("/tempo \n")
								.append(tab).append("/conselho \n")
								.append(tab).append("/piada_Chuck_Norris \n")
								.append(tab).append("/ordenar \"Passe os números que você deseja ordenar \" \n")
								.toString();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Criacao do objeto bot com as informacoes de acesso.

		// Controle de off-set, isto e, a partir deste ID sera lido as mensagens
		// pendentes na fila.
		int m = 0;

		// Loop infinito pode ser alterado por algum timer de intervalo curto.
		while (true) {

			// Executa comando no Telegram para obter as mensagens pendentes a partir de um
			// off-set (limite inicial).
			updatesResponse = bot.execute(new GetUpdates().limit(100).offset(m));

			// Lista de mensagens.
			List<Update> updates = updatesResponse.updates();

			// Analise de cada acao da mensagem.
			for (Update update : updates) {

				// Atualizacao do off-set.
				m = update.updateId() + 1;
				
				if(update.message() != null ) {
					System.out.println("Recebendo mensagem: " + update.message().text());

					// Envio de "Escrevendo" antes de enviar a resposta.
					baseResponse = bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing));

					// Verificacao de acao de chat foi enviada com sucesso.
					System.out.println("Resposta de Chat Action Enviada? " + baseResponse.isOk());

					if (update.message().text().toLowerCase().equals("/start")) {
						telegramRespostaPadrao(update);
						
					} else if (update.message().text().toLowerCase().equals("/tempo")) {
						ResponseEntity<ApiWeatherResponse> requestTempoAPI = APIs.requestTempoAPI(token_weather);
						telegramRespostaTempo(update, requestTempoAPI);
					
					} else if (update.message().text().toLowerCase().equals("/conselho")) {
						ResponseEntity<ApiAdviceResponse> requestConselhoAPI = APIs.requestConselhoAPI();
						String telegramRespostaConselho = telegramRespostaConselho(update, requestConselhoAPI);
						ResponseEntity<ApiTranslateResponse> requestTradutorAPI = APIs.requestTradutorAPI(telegramRespostaConselho, token_rapid_key);
						telegramRespostaConselhoTradutor(update, requestTradutorAPI);
						
					} else if (update.message().text().toLowerCase().equals("/piada_Chuck_Norris".toLowerCase())) {
						ResponseEntity<ApiJokeChuckNorrisResponse> requestJokeChuckNorrisAPI = APIs.requestJokeChuckNorrisAPI();
						String telegramRespostaPiadaChuckNorris = telegramRespostaPiadaChuckNorris(update, requestJokeChuckNorrisAPI);
						ResponseEntity<ApiTranslateResponse> requestTradutorAPI = APIs.requestTradutorAPI(telegramRespostaPiadaChuckNorris, token_rapid_key);
						telegramRespostaPiadaChuckNorrisTradutor(update, requestTradutorAPI);

					} else if (update.message().text().toLowerCase().startsWith("/ordenar")) {
						try {
							telegramRespostaOrdenar(update);
						} catch (Exception e) {
							System.out.println(e);
							sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Desculpe talvez minha funcionalidade de ordenação n�o esteja funcionando, certifique-se de que você tenha passado na frente do comando /ordenar apenas números com um espaço em branco entre cada número. Depois tente novamente se quiser. "));
						}
						
						
					} else {
						sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Desculpe eu não entendi, pode dizer novamente? "));
						
					}
					
					// Verificacao de mensagem enviada com sucesso.
//					System.out.println("Mensagem Enviada? " + sendResponse.isOk());
				} else {
					
				}
				

			}
		}

	}


	private static void telegramRespostaOrdenar(Update update) {
		
		String usuarioString = update.message().text().toLowerCase();
		usuarioString = usuarioString.replace("/ordenar", " ");
		usuarioString = usuarioString.trim();	
		List<String> listaStringParaOrdenar = Arrays.asList(usuarioString.split(" "));
		List<Integer> listaIntegerParaOrdenar = listaStringParaOrdenar.stream().map(Integer::parseInt).collect(Collectors.toList());
		Collections.sort(listaIntegerParaOrdenar);
		sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Sua lista ordenada é " + listaIntegerParaOrdenar));
		sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Se quiser continuar iteragindo comigo, selecione uma das opções: \n" + funcionalidades));
		
	}


	// RESPOSTA TELEGRRAM

	private static void telegramRespostaPadrao(Update update) {
		sendResponse = bot
				.execute(new SendMessage(update.message().chat().id(), "Bem vindo usuário " + nomeDoUsuario(update) + " ao BOT"));
		sendResponse = bot
				.execute(new SendMessage(update.message().chat().id(), "Escolha uma das opções: \n" + funcionalidades));
	}

	private static void telegramRespostaTempo(Update update, ResponseEntity<ApiWeatherResponse> requestTempoAPI) {
		ApiWeatherResponse body = requestTempoAPI.getBody();

		String resultado = new StringBuilder().append("Previsão do tempo em: " + body.getResults().getCity())
				.toString();

		String resultadoHoje = new StringBuilder().append("Hoje \n")
				.append("Data: " + body.getResults().getForecast().get(0).getDate() + "\n")
				.append("Descrição: " + body.getResults().getDescription() + "\n")
				.append("Temperatura: " + body.getResults().getTemp() + " graus" + "\n")
				.append("Velocidade do vento: " + body.getResults().getWind_speedy() + "\n")
				.append("Por do sol: " + body.getResults().getSunset() + "\n")
				.append("Máximo: " + body.getResults().getForecast().get(0).getMax() + " graus" + "\n")
				.append("Mínimo: " + body.getResults().getForecast().get(0).getMin() + " graus").toString();

		String resultadoAmanha = new StringBuilder().append("Amanhã \n")
				.append("Data: " + body.getResults().getForecast().get(1).getDate() + "\n")
				.append("Descrição: " + body.getResults().getForecast().get(1).getDescription() + "\n")
				.append("Máximo: " + body.getResults().getForecast().get(1).getMax() + " graus" + "\n")
				.append("Mínimo: " + body.getResults().getForecast().get(1).getMin() + " graus").toString();

		sendResponse = bot.execute(new SendMessage(update.message().chat().id(), resultado));
		sendResponse = bot.execute(new SendMessage(update.message().chat().id(), resultadoHoje));
		sendResponse = bot.execute(new SendMessage(update.message().chat().id(), resultadoAmanha));
		sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Se quiser continuar iteragindo comigo, selecione uma das opções: \n" + funcionalidades));

	}

	private static String telegramRespostaConselho(Update update, ResponseEntity<ApiAdviceResponse> requestConselhoAPI) {
		ApiAdviceResponse body = requestConselhoAPI.getBody();

		String resultado = new StringBuilder().append("Conselho do dia em inglês: " + body.getSlip().getAdvice())
				.toString();

		sendResponse = bot.execute(new SendMessage(update.message().chat().id(), resultado));

		return resultado;

	}
	
	private static void telegramRespostaConselhoTradutor(Update update, ResponseEntity<ApiTranslateResponse> requestConselhoTradutorAPI) {
		ApiTranslateResponse body = requestConselhoTradutorAPI.getBody();

		String resultado = new StringBuilder().append(body.getResult().getPt())
				.toString();

		sendResponse = bot.execute(new SendMessage(update.message().chat().id(), resultado));
		sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Se quiser continuar iteragindo comigo, selecione uma das opções: \n" + funcionalidades));

	}
	
	private static String telegramRespostaPiadaChuckNorris(Update update, ResponseEntity<ApiJokeChuckNorrisResponse> requestJokeChuckNorrisAPI) {
		ApiJokeChuckNorrisResponse body = requestJokeChuckNorrisAPI.getBody();

		String resultado = new StringBuilder().append("Piada do Chuck Norris em inglês: " + body.getValue())
				.toString();

		sendResponse = bot.execute(new SendMessage(update.message().chat().id(), resultado));

		return resultado;

	}
	
	private static void telegramRespostaPiadaChuckNorrisTradutor(Update update, ResponseEntity<ApiTranslateResponse> requestTradutorAPI) {
		
		ApiTranslateResponse body = requestTradutorAPI.getBody();

		String resultado = new StringBuilder().append(body.getResult().getPt())
				.toString();

		sendResponse = bot.execute(new SendMessage(update.message().chat().id(), resultado));
		sendResponse = bot.execute(new SendMessage(update.message().chat().id(),
				"Se quiser continuar iteragindo comigo, selecione uma das opções: \n" + funcionalidades));
		
	}

	// METODOS AUXILIARES
	private static String concatenaNomeDoUsuario(String idUser, String... names) {

		String user = "";

		for (String name : names) {
			if (name != null && !name.isEmpty()) {
				user += name + " ";
			}
		}

		if (user.isEmpty()) {
			user = idUser;
		}

		return user;
	}

	private static String nomeDoUsuario(Update update) {
		return concatenaNomeDoUsuario(update.message().from().id().toString(), update.message().from().firstName(),
				update.message().from().lastName());
	}
}
