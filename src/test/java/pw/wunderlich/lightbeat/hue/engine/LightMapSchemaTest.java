package pw.wunderlich.lightbeat.hue.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LightMapSchemaTest {

    private static JsonSchema schema;
    private static ObjectMapper mapper;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void setUp() throws IOException {
        mapper = new ObjectMapper();
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        
        // Load schema from file
        Path schemaPath = Paths.get("schemas/lightmap.schema.json");
        try (InputStream schemaStream = Files.newInputStream(schemaPath)) {
            schema = factory.getSchema(schemaStream);
        }
    }

    @Test
    void testSchemaExists() {
        assertNotNull(schema);
    }

    @Test
    void testValidLightMapWithBridges() throws IOException {
        String json = """
                {
                  "bridges": [
                    {"id": "bridge-001", "ip": "192.168.1.100"}
                  ],
                  "lights": [
                    {"id": "light-001", "x": 0.0, "y": 0.0, "bridgeId": "bridge-001", "controlType": "FAST_UDP"}
                  ]
                }
                """;
        JsonNode node = mapper.readTree(json);
        Set<ValidationMessage> errors = schema.validate(node);
        
        assertTrue(errors.isEmpty(), "Valid light map should not have errors: " + errors);
    }

    @Test
    void testValidLightMapWithoutBridges() throws IOException {
        String json = """
                {
                  "lights": [
                    {"id": "light-001", "controlType": "FAST_UDP"},
                    {"id": "light-002", "controlType": "LOW_HTTP"}
                  ]
                }
                """;
        JsonNode node = mapper.readTree(json);
        Set<ValidationMessage> errors = schema.validate(node);
        
        assertTrue(errors.isEmpty(), "Valid light map should not have errors: " + errors);
    }

    @Test
    void testValidLightMapWithAllFields() throws IOException {
        String json = """
                {
                  "bridges": [
                    {"id": "bridge-001", "ip": "192.168.1.100"},
                    {"id": "bridge-002", "ip": "10.0.0.50"}
                  ],
                  "lights": [
                    {"id": "light-001", "x": 0.0, "y": 0.0, "bridgeId": "bridge-001", "controlType": "FAST_UDP"},
                    {"id": "light-002", "x": 1.0, "y": 0.0, "bridgeId": "bridge-001", "controlType": "LOW_HTTP"},
                    {"id": "light-003", "x": 0.5, "y": 1.0, "bridgeId": "bridge-002", "controlType": "FAST_UDP"}
                  ]
                }
                """;
        JsonNode node = mapper.readTree(json);
        Set<ValidationMessage> errors = schema.validate(node);
        
        assertTrue(errors.isEmpty(), "Valid light map should not have errors: " + errors);
    }

    @Test
    void testInvalidLightMapMissingLights() throws IOException {
        String json = """
                {
                  "bridges": [
                    {"id": "bridge-001", "ip": "192.168.1.100"}
                  ]
                }
                """;
        JsonNode node = mapper.readTree(json);
        Set<ValidationMessage> errors = schema.validate(node);
        
        assertFalse(errors.isEmpty(), "Should have validation errors for missing lights");
    }

    @Test
    void testInvalidBridgeMissingId() throws IOException {
        String json = """
                {
                  "bridges": [
                    {"ip": "192.168.1.100"}
                  ],
                  "lights": [
                    {"id": "light-001", "controlType": "FAST_UDP"}
                  ]
                }
                """;
        JsonNode node = mapper.readTree(json);
        Set<ValidationMessage> errors = schema.validate(node);
        
        assertFalse(errors.isEmpty(), "Should have validation errors for bridge missing id");
    }

    @Test
    void testInvalidBridgeMissingIp() throws IOException {
        String json = """
                {
                  "bridges": [
                    {"id": "bridge-001"}
                  ],
                  "lights": [
                    {"id": "light-001", "controlType": "FAST_UDP"}
                  ]
                }
                """;
        JsonNode node = mapper.readTree(json);
        Set<ValidationMessage> errors = schema.validate(node);
        
        assertFalse(errors.isEmpty(), "Should have validation errors for bridge missing ip");
    }

    @Test
    void testInvalidBridgeInvalidIpFormat() throws IOException {
        String json = """
                {
                  "bridges": [
                    {"id": "bridge-001", "ip": "not-an-ip"}
                  ],
                  "lights": [
                    {"id": "light-001", "controlType": "FAST_UDP"}
                  ]
                }
                """;
        JsonNode node = mapper.readTree(json);
        Set<ValidationMessage> errors = schema.validate(node);
        
        assertFalse(errors.isEmpty(), "Should have validation errors for invalid IP format");
    }

    @Test
    void testInvalidLightMissingId() throws IOException {
        String json = """
                {
                  "lights": [
                    {"controlType": "FAST_UDP"}
                  ]
                }
                """;
        JsonNode node = mapper.readTree(json);
        Set<ValidationMessage> errors = schema.validate(node);
        
        assertFalse(errors.isEmpty(), "Should have validation errors for light missing id");
    }

    @Test
    void testInvalidLightMissingControlType() throws IOException {
        String json = """
                {
                  "lights": [
                    {"id": "light-001"}
                  ]
                }
                """;
        JsonNode node = mapper.readTree(json);
        Set<ValidationMessage> errors = schema.validate(node);
        
        assertFalse(errors.isEmpty(), "Should have validation errors for light missing controlType");
    }

    @Test
    void testInvalidLightInvalidControlType() throws IOException {
        String json = """
                {
                  "lights": [
                    {"id": "light-001", "controlType": "INVALID_TYPE"}
                  ]
                }
                """;
        JsonNode node = mapper.readTree(json);
        Set<ValidationMessage> errors = schema.validate(node);
        
        assertFalse(errors.isEmpty(), "Should have validation errors for invalid controlType");
    }

    @Test
    void testLoadAndSaveValidLightMap() throws IOException {
        String originalJson = """
                {
                  "bridges": [
                    {"id": "bridge-001", "ip": "192.168.1.100"}
                  ],
                  "lights": [
                    {"id": "light-001", "x": 0.0, "y": 0.0, "bridgeId": "bridge-001", "controlType": "FAST_UDP"},
                    {"id": "light-002", "x": 1.0, "y": 0.0, "bridgeId": "bridge-001", "controlType": "LOW_HTTP"}
                  ]
                }
                """;
        
        // Parse and validate original
        JsonNode originalNode = mapper.readTree(originalJson);
        Set<ValidationMessage> errors = schema.validate(originalNode);
        assertTrue(errors.isEmpty(), "Original should be valid");

        // Save to file
        Path testFile = tempDir.resolve("lightmap.json");
        Files.writeString(testFile, originalJson);

        // Load from file
        String loadedJson = Files.readString(testFile);
        JsonNode loadedNode = mapper.readTree(loadedJson);

        // Validate loaded
        errors = schema.validate(loadedNode);
        assertTrue(errors.isEmpty(), "Loaded lightmap should be valid");
        
        // Verify content
        assertEquals(originalNode, loadedNode);
    }

    @Test
    void testExampleLightmapJsonIsValid() throws IOException {
        // Test that the example lightmap.json in resources is valid
        Path examplePath = Paths.get("src/main/resources/lightmap.json");
        if (!Files.exists(examplePath)) {
            // If example doesn't exist, skip this test
            return;
        }

        String json = Files.readString(examplePath);
        JsonNode node = mapper.readTree(json);
        Set<ValidationMessage> errors = schema.validate(node);
        
        assertTrue(errors.isEmpty(), "Example lightmap.json should be valid: " + errors);
    }
}
