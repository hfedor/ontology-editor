package com.pik.ontologyeditor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OntologyEditorApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(OntologyEditorApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
