package net.thomas.portfolio.legal.configuration;

import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_MESSAGE_PREFIX;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_ROOT_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.WEB_SOCKET_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.MESSAGE_PREFIX;

import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Component
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint(LEGAL_ROOT_PATH + WEB_SOCKET_PATH).withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker(MESSAGE_PREFIX + LEGAL_MESSAGE_PREFIX);
		registry.setApplicationDestinationPrefixes(LEGAL_ROOT_PATH + WEB_SOCKET_PATH);
	}
}
