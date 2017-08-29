# json-resources-spike
## objectives 
### demonstrate

- how a service description can reference json schemas which are provided via a jar
- how a schema can import definitions from a seperate schema file which has also been provided
via an external jar
- ensure that framework code generators are able to operate using service descriptions with
 externally provided schemas
- ensure that IDEs are able to process service descriptions which include externally imported 
schema without requiring absolute or /target references

### outcomes
- reduce development effort by removing the need to write event POJOs and schemas, we will generate
the POJOs from schemas instead
- enable data standards definitions to be maintained and reused across the programme, reducing code
overhead and improving the accuracy and consistency of data definitions

## solution
The common-schema jar provides the definitions.json schema file:

```json
{
  "$schema": "http://json-schema.org/draft-04/schema",
  "type": "object",
    "uuid": {
      "type": "string",
      "pattern": "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$"
    }
}
```
This is referenced relatively from the domain module which imports the schema definitions.json into
its /src/events/imported folder. The imported folder only contains schemas which have been imported
externally and maven-clean-plugin is configured to delete the contents of this folder when cleaning.

The domain module will contain a folder (/events) to contain all the events which are part of the domain
of a given microservice. These can be imported into service components such as listeners and event
processors.

The following excerpt demonstrates the maven configuration for the domain to import and clean the 
shared / common schema:

```xml
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <id>copy</id>
                        <phase>validate</phase>
                        <goals>
                            <goal>unpack</goal>
                        </goals>
                        <configuration>
                            <artifactItems>
                                <artifactItem>
                                    <groupId>uk.gov.justice.services</groupId>
                                    <artifactId>common-schema</artifactId>
                                    <version>${project.version}</version>
                                    <overWrite>true</overWrite>
                                    <outputDirectory>${basedir}/src/events/imported</outputDirectory>
                                    <includes>**/*.json</includes>
                                </artifactItem>
                            </artifactItems>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <artifactId>maven-clean-plugin</artifactId>
                <version>3.0.0</version>
                <configuration>
                    <filesets>
                        <fileset>
                            <directory>${project.basedir}/src/events/imported</directory>
                            <includes>
                                <include>**/*.json</include>
                            </includes>
                            <followSymlinks>false</followSymlinks>
                        </fileset>
                    </filesets>
                </configuration>
            </plugin>
```

The stagingtfl.events.charged-cases-csv-files-upload-accepted.json schema shows how the shared 
definitions.json can be reused:

```json
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "type": "object",
  "properties": {
    "example": {
      "$ref": "imported/definitions.json#/uuid"
    }
  },
  "required": [
    "example"
  ]
}
```

The maven-resources-plugin is also configured to package up the events from the domain module so they
can be used by the handler module:

```xml
            <plugin>
                <artifactId>maven-resources-plugin</artifactId>
                <version>3.0.2</version>
                <executions>
                    <execution>
                        <id>copy-events</id>
                        <phase>validate</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${basedir}/target/classes</outputDirectory>
                            <resources>
                                <resource>
                                    <directory>src/events</directory>
                                    <filtering>true</filtering>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```
The handler module configures the maven-dependency-plugin to import the domain schemas:

```xml
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <id>copy</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>unpack</goal>
                        </goals>
                        <configuration>
                            <artifactItems>
                                <artifactItem>
                                    <groupId>uk.gov.justice.services</groupId>
                                    <artifactId>domain</artifactId>
                                    <version>${project.version}</version>
                                    <overWrite>true</overWrite>
                                    <outputDirectory>${basedir}/src/raml/json/schema</outputDirectory>
                                    <includes>**/*.json</includes>
                                </artifactItem>
                            </artifactItems>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```