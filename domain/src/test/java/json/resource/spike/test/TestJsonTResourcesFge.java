package json.resource.spike.test;

import static com.github.fge.jackson.JsonLoader.fromFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.uri.URITranslatorConfiguration;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.junit.Test;

public class TestJsonTResourcesFge {

    private static final String NAMESPACE
            = "file:/Users/justin/code/json-resources-spike/domain/src/main/resources/json/schema/";

    @Test
    public void shouldValidateLinkedSchema() throws IOException, ProcessingException, URISyntaxException {


        final URL schemaUrl = getClass().getResource("/stagingtfl.events.charged-cases-csv-files-upload-accepted.json");
        System.out.println(schemaUrl.toString());
        final URL jsonUrl = getClass().getResource("/example-json.json");
        final JsonNode jsonSchemaNode = fromFile(new File(schemaUrl.getFile()));
        final JsonNode jsonExample = fromFile(new File(jsonUrl.getFile()));

        final URITranslatorConfiguration translatorCfg
                = URITranslatorConfiguration.newBuilder()
                .setNamespace(nameSpaceFrom(schemaUrl)).freeze();
        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
                .setURITranslatorConfiguration(translatorCfg).freeze();

        final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
                .setLoadingConfiguration(cfg).freeze();

        final JsonSchema schema = factory.getJsonSchema(jsonSchemaNode);

        ProcessingReport report;

        report = schema.validate(jsonExample);
        System.out.println(report);

    }

    private String nameSpaceFrom(URL schemaInputStream) throws URISyntaxException {
        return String.format("file:%s/", Paths.get(schemaInputStream.toURI()).getParent().toString());
    }


}
