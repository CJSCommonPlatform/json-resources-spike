package json.resource.spike.test;

import java.io.IOException;
import java.io.InputStream;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

public class TestJsonResourcesEverret {


    //EveretIT need to configure (if possible) to use relative paths in schemas
    public void shouldValidateSchema() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/events/stagingtfl.events.charged-cases-csv-files-upload-accepted.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            Schema schema = SchemaLoader.load(rawSchema);
            try (InputStream inputStream2 = getClass().getResourceAsStream("/json/schema/example-json.json")) {
                JSONObject rawJson = new JSONObject(new JSONTokener(inputStream2));
                schema.validate(rawJson); // throws a ValidationException if this object is invalid
            }
        }
    }
}
