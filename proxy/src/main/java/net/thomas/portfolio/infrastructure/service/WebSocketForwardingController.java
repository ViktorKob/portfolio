package net.thomas.portfolio.infrastructure.service;

import static java.util.Collections.singletonList;
import static net.thomas.portfolio.globals.LegalServiceGlobals.HISTORY_UPDATED;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_MESSAGE_PREFIX;
import static net.thomas.portfolio.services.ServiceGlobals.LEGAL_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.MESSAGE_PREFIX;
import static net.thomas.portfolio.services.ServiceGlobals.STOMP_PATH;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@Controller
public class WebSocketForwardingController {
	// @Autowired
	// private EurekaClient discoveryClient;

	@Autowired
	private SimpMessagingTemplate webSocket;
	private final Map<String, StompSession> liveSessions;

	public WebSocketForwardingController() {
		liveSessions = new HashMap<>();
	}

	@PostConstruct
	public void initializeService() {
		webSocket.setMessageConverter(new MappingJackson2MessageConverter());
		getSession("ws://localhost:8350" + LEGAL_SERVICE_PATH + STOMP_PATH);
	}

	private StompSession getSession(String url) {
		if (!liveSessions.containsKey(url) || !liveSessions.get(url).isConnected()) {
			liveSessions.put(url, buildSession(url));
		}
		return liveSessions.get(url);
	}

	private StompSession buildSession(String url) {
		final SockJsClient sockJsClient = new SockJsClient(singletonList(new WebSocketTransport(new StandardWebSocketClient())));
		final WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		// final List<InstanceInfo> legalServiceInstances =
		// discoveryClient.getInstancesById(LEGAL_SERVICE_NAME);
		// final InstanceInfo instance = legalServiceInstances.get(0);
		final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
		headers.add("Authorization", "Basic c2VydmljZS11c2VyOnBhc3N3b3Jk");
		try {
			final ListenableFuture<StompSession> session = stompClient.connect(url, headers, new LegalServiceWebSocketForwarder(webSocket));
			return session.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException("Unable to build STOMP connection", e);
		}
	}

	private static class LegalServiceWebSocketForwarder extends StompSessionHandlerAdapter {
		private final SimpMessagingTemplate webSocket;

		public LegalServiceWebSocketForwarder(SimpMessagingTemplate webSocket) {
			this.webSocket = webSocket;
		}

		@Override
		public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
			session.subscribe(MESSAGE_PREFIX + LEGAL_MESSAGE_PREFIX + HISTORY_UPDATED, this);
		}

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			webSocket.convertAndSend(headers.getDestination(), payload);
		}
	}
}