package com.pik.ontologyeditor;

import javax.annotation.Resource;

import com.pik.ontologyeditor.service.AddFileToDB;
import com.pik.ontologyeditor.service.FilesStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pik.ontologyeditor.neo4jMapping.Mapping;

@SpringBootApplication
public class OntologyEditorApplication implements CommandLineRunner {
	@Resource
	FilesStorageService storageService;

	public static void main(String[] args) {
		SpringApplication.run(OntologyEditorApplication.class, args);
	}

	@Override
	public void run(String... arg) throws Exception {
		storageService.deleteAll();
		storageService.init();


		Mapping mapping = new Mapping("bolt://localhost:7687", "neo4j", "koperwas123");
		mapping.GetNodeByID(206);
		mapping.GetRoots();
		mapping.GetChildrenByParentsID(206);
		mapping.UpdatePropertyName(206,"uri", "url");
	}
}
