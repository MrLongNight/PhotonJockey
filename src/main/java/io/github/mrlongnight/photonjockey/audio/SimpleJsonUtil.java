package io.github.mrlongnight.photonjockey.audio;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Simple JSON utility for reading and writing audio profiles.
 * This is a minimal implementation to avoid adding external dependencies.
 */
class SimpleJsonUtil {

    private SimpleJsonUtil() {
        // Utility class
    }

    /**
     * Writes audio profiles to a JSON file.
     *
     * @param profiles the profiles to write
     * @param file     the file to write to
     * @throws IOException if an I/O error occurs
     */
    static void writeProfilesToJson(Map<String, AudioProfile> profiles, File file) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write("{\n");
            writer.write("  \"profiles\": [\n");

            boolean first = true;
            for (AudioProfile profile : profiles.values()) {
                if (!first) {
                    writer.write(",\n");
                }
                first = false;
                writeProfile(writer, profile, "    ");
            }

            writer.write("\n  ]\n");
            writer.write("}\n");
        }
    }

    private static void writeProfile(Writer writer, AudioProfile profile, String indent)
      throws IOException {
        writer.write(indent);
        writer.write("{\n");
        writer.write(indent);
        writer.write("  \"id\": ");
        writeString(writer, profile.getId());
        writer.write(",\n");
        writer.write(indent);
        writer.write("  \"name\": ");
        writeString(writer, profile.getName());
        writer.write(",\n");
        writer.write(indent);
        writer.write("  \"parameters\": {\n");

        Map<String, Object> params = profile.getParameters();
        boolean firstParam = true;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (!firstParam) {
                writer.write(",\n");
            }
            firstParam = false;
            writer.write(indent);
            writer.write("    ");
            writeString(writer, entry.getKey());
            writer.write(": ");
            writeValue(writer, entry.getValue());
        }

        writer.write("\n");
        writer.write(indent);
        writer.write("  }\n");
        writer.write(indent);
        writer.write("}");
    }

    private static void writeString(Writer writer, String value) throws IOException {
        writer.write("\"");
        writer.write(escapeString(value));
        writer.write("\"");
    }

    private static void writeValue(Writer writer, Object value) throws IOException {
        if (value == null) {
            writer.write("null");
        } else if (value instanceof String) {
            writeString(writer, (String) value);
        } else if (value instanceof Number || value instanceof Boolean) {
            writer.write(value.toString());
        } else {
            writeString(writer, value.toString());
        }
    }

    private static String escapeString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Reads audio profiles from a JSON file.
     *
     * @param file the file to read from
     * @return a map of profile id to AudioProfile
     * @throws IOException if an I/O error occurs
     */
    static Map<String, AudioProfile> readProfilesFromJson(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append('\n');
            }

            return parseProfiles(content.toString());
        }
    }

    private static Map<String, AudioProfile> parseProfiles(String json) {
        Map<String, AudioProfile> profiles = new HashMap<>();
        
        // Simple parsing: find the profiles array
        int profilesStart = json.indexOf("\"profiles\"");
        if (profilesStart == -1) {
            return profiles;
        }

        int arrayStart = json.indexOf('[', profilesStart);
        int arrayEnd = json.lastIndexOf(']');
        if (arrayStart == -1 || arrayEnd == -1) {
            return profiles;
        }

        String profilesJson = json.substring(arrayStart + 1, arrayEnd);
        List<String> profileObjects = splitProfileObjects(profilesJson);

        for (String profileJson : profileObjects) {
            AudioProfile profile = parseProfile(profileJson);
            if (profile != null) {
                profiles.put(profile.getId(), profile);
            }
        }

        return profiles;
    }

    private static List<String> splitProfileObjects(String json) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = 0;
        boolean inString = false;
        char prev = 0;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"' && prev != '\\') {
                inString = !inString;
            } else if (!inString) {
                if (c == '{') {
                    if (depth == 0) {
                        start = i;
                    }
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0) {
                        objects.add(json.substring(start, i + 1));
                    }
                }
            }
            prev = c;
        }

        return objects;
    }

    private static AudioProfile parseProfile(String json) {
        String id = extractStringValue(json, "id");
        String name = extractStringValue(json, "name");

        if (id == null || name == null) {
            return null;
        }

        AudioProfile profile = new AudioProfile(id, name);

        // Extract parameters object
        int paramsStart = json.indexOf("\"parameters\"");
        if (paramsStart != -1) {
            int objStart = json.indexOf('{', paramsStart);
            int objEnd = findMatchingBrace(json, objStart);
            if (objStart != -1 && objEnd != -1) {
                String paramsJson = json.substring(objStart + 1, objEnd);
                Map<String, Object> params = parseParameters(paramsJson);
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    profile.setParameter(entry.getKey(), entry.getValue());
                }
            }
        }

        return profile;
    }

    private static Map<String, Object> parseParameters(String json) {
        Map<String, Object> params = new HashMap<>();
        
        // Split by comma, but not within strings
        List<String> pairs = splitKeyValuePairs(json);
        
        for (String pair : pairs) {
            int colonIndex = pair.indexOf(':');
            if (colonIndex == -1) {
                continue;
            }

            String key = extractStringFromQuotes(pair.substring(0, colonIndex).trim());
            String valueStr = pair.substring(colonIndex + 1).trim();
            
            if (key != null && !valueStr.isEmpty()) {
                Object value = parseValue(valueStr);
                if (value != null) {
                    params.put(key, value);
                }
            }
        }

        return params;
    }

    private static List<String> splitKeyValuePairs(String json) {
        List<String> pairs = new ArrayList<>();
        int start = 0;
        boolean inString = false;
        char prev = 0;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"' && prev != '\\') {
                inString = !inString;
            } else if (!inString && c == ',') {
                pairs.add(json.substring(start, i));
                start = i + 1;
            }
            prev = c;
        }

        if (start < json.length()) {
            pairs.add(json.substring(start));
        }

        return pairs;
    }

    private static Object parseValue(String valueStr) {
        valueStr = valueStr.trim();
        
        if (valueStr.equals("null")) {
            return null;
        } else if (valueStr.equals("true")) {
            return true;
        } else if (valueStr.equals("false")) {
            return false;
        } else if (valueStr.startsWith("\"")) {
            return extractStringFromQuotes(valueStr);
        } else {
            // Try to parse as number
            try {
                if (valueStr.contains(".")) {
                    return Double.parseDouble(valueStr);
                } else {
                    return Integer.parseInt(valueStr);
                }
            } catch (NumberFormatException e) {
                return valueStr;
            }
        }
    }

    private static String extractStringValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyStart = json.indexOf(searchKey);
        if (keyStart == -1) {
            return null;
        }

        int colonIndex = json.indexOf(':', keyStart);
        if (colonIndex == -1) {
            return null;
        }

        int valueStart = json.indexOf('"', colonIndex);
        if (valueStart == -1) {
            return null;
        }

        int valueEnd = json.indexOf('"', valueStart + 1);
        if (valueEnd == -1) {
            return null;
        }

        return unescapeString(json.substring(valueStart + 1, valueEnd));
    }

    private static String extractStringFromQuotes(String str) {
        str = str.trim();
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return unescapeString(str.substring(1, str.length() - 1));
        }
        return null;
    }

    private static String unescapeString(String str) {
        return str.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    private static int findMatchingBrace(String json, int start) {
        int depth = 0;
        boolean inString = false;
        char prev = 0;

        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"' && prev != '\\') {
                inString = !inString;
            } else if (!inString) {
                if (c == '{') {
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0) {
                        return i;
                    }
                }
            }
            prev = c;
        }

        return -1;
    }
}
