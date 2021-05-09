package com.pik.ontologyeditor;

import javax.annotation.Resource;

import com.pik.ontologyeditor.service.AddFileToDB;
import com.pik.ontologyeditor.service.FilesStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class OntologyEditorApplication implements CommandLineRunner {
	@Resource
	FilesStorageService storageService;
	AddFileToDB addFileToDB = new AddFileToDB("bolt://localhost:7687", "neo4j", "koperwas123");

	public static void main(String[] args) {
		SpringApplication.run(OntologyEditorApplication.class, args);
	}

	@Override
	public void run(String... arg) throws Exception {
		storageService.deleteAll();
		storageService.init();
		addFileToDB.AddFile("https://github.com/neo4j-labs/neosemantics/raw/3.5/docs/rdf/vw.owl");
	}
}
