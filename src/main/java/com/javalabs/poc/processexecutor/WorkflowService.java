package com.javalabs.poc.processexecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.config.EnableWebFlux;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@EnableWebFlux
@Service
@Slf4j
public class WorkflowService {
	@Autowired
	private CommandsExecutor executor;

	private String[][] commands = {
			{ "sh", "-c", "echo \"*********STARTING TIMER*********\"" },
			{ "sh", "-c", "sleep 10" },
			{ "sh", "-c", "echo \"*********TIMER FINISHED*********\"" } };

	public Flux<String> getProcessLogPublisher() {
		return Flux.<String>from(this.executor.getProcessLogPublisher()
				.toFlowable(BackpressureStrategy.DROP));
	}

	public void startWorkflow() throws ExecutionException, InterruptedException {
		this.executor.getProcessLogPublisher()
				.subscribe(output -> log.info("OUTPUT : {}", output));

		CompletableFuture<Integer> future = CompletableFuture.<Integer>supplyAsync(() -> {
			try {
				return this.executor.execute(commands);
			} catch (final Exception exception) {
				throw new CompletionException(exception);
			}
		});

		int exitCode = future.get();
		log.info("PROCESS EXIT STATUS : {}", exitCode);
	}
}
