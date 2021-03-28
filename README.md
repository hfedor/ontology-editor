# ontology-editor

## Nexus
### How to upload an artifact to Nexus using Maven?
1. Modify repository location in `pom.xml`.
2. Modify permissions in `settings.xml`.
3. `mvn deploy`

### How to download an artifact to Nexus using Maven?
1. Modify library you want to download in `pom.xml`.
2. Modify mirrors in `settings.xml`.
3. `mvn deploy|compile|test|install`

