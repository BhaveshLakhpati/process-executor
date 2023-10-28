package com.javalabs.poc.processexecutor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

import io.reactivex.rxjava3.subjects.PublishSubject;

@Service
public final class CommandsExecutor {
	private final PublishSubject<String> processPublisher;
	private final ProcessBuilder builder;

	private CommandsExecutor() {
		this.processPublisher = PublishSubject.create();
		this.builder = new ProcessBuilder();
	}

	public int execute(final String[][] script) throws Exception {
		int exitCode = 1;
		Process process;
		for (String[] command : script) {
			process = this.builder.command(command)
					.start();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					this.processPublisher.onNext(line);
				}
			}
			process.waitFor();
			exitCode = process.exitValue();
		}

		return exitCode;
	}

	public PublishSubject<String> getProcessLogPublisher() {
		return this.processPublisher;
	}
}
