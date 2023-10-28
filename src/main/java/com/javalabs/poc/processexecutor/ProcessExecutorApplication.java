package com.javalabs.poc.processexecutor;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

@SpringBootApplication
@RestController
public class ProcessExecutorApplication implements CommandLineRunner {
	@Autowired
	private WorkflowService workflowService;

	public static void main(String[] args) {
		SpringApplication.run(ProcessExecutorApplication.class, args);
	}

	@Override
	public void run(final String... args) throws Exception {
		this.workflowService.startWorkflow();
	}

	@Bean
	HandlerMapping webSocketRequestMapping() {
		WebSocketHandler logHandler = webSocketSession -> webSocketSession
				.send(this.workflowService.getProcessLogPublisher()
						.map(webSocketSession::textMessage));

		SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping(
				Map.<String, WebSocketHandler>of("/subscribe/logs", logHandler));
		return simpleUrlHandlerMapping;
	}

	@GetMapping("/workflow/start")
	public ResponseEntity<String> startWorkflow() {
		ResponseEntity<String> response;

		try {
			this.workflowService.startWorkflow();
			response = ResponseEntity.ok("Workflow Started!!!");
		} catch (final ExecutionException | InterruptedException exception) {
			response = ResponseEntity.internalServerError()
					.body(exception.getMessage());
		}

		return response;
	}
}
