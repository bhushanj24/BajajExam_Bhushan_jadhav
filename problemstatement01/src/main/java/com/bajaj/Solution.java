
package com.bajaj;

import org.json.JSONObject;
import org.json.JSONArray;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        // PRN number with whitespace removed and converted to lowercase
        String prnNumber = "240344220068".toLowerCase().replaceAll("\\s+", "");

        // Path to the JSON file
        String jsonFilePath = "C:\\Users\\DELL\\Desktop\\Bajaj\\data.json";

        try {
            // Check if the file exists, and create it if it doesn't
            File jsonFile = new File(jsonFilePath);
            if (!jsonFile.exists()) {
                System.out.println("File not found: " + jsonFilePath + ". Creating a new file.");
                createDefaultJsonFile(jsonFilePath);
            }

            // Read and parse the JSON file for the "destination" key
            String destinationValue = parseJsonForDestination(jsonFilePath);
            if (destinationValue == null) {
                System.out.println("Destination key not found in the JSON file.");
                return;
            }

            // Generate a random 8-character alphanumeric string
            String randomString = generateRandomString(8);

            // Concatenate PRN, destination value, and random string, then generate an MD5 hash
            String concatenatedString = prnNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(concatenatedString);

            // Output the result as hash;randomString
            System.out.println(md5Hash + ";" + randomString);

        } catch (IOException e) {
            System.err.println("Error reading or writing the file: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error generating MD5 hash: " + e.getMessage());
        }
    }

    // Method to create a default JSON file with some basic content
    private static void createDefaultJsonFile(String filePath) throws IOException {
        JSONObject defaultJson = new JSONObject();
        defaultJson.put("destination", "defaultValue");

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(defaultJson.toString(4)); // Indent with 4 spaces for readability
        }
    }

    // Method to parse the JSON file and find the first instance of the key "destination"
    private static String parseJsonForDestination(String filePath) throws IOException {
        StringBuilder jsonContent = new StringBuilder();

        // Read the JSON file content
        try (Scanner scanner = new Scanner(new FileReader(filePath))) {
            while (scanner.hasNextLine()) {
                jsonContent.append(scanner.nextLine());
            }
        }

        // Parse the content into a JSONObject
        JSONObject jsonObject = new JSONObject(jsonContent.toString());
        // Find and return the value of the "destination" key
        return findDestinationKey(jsonObject);
    }

    // Recursively find the "destination" key in the JSON object
    private static String findDestinationKey(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            }
            if (value instanceof JSONObject) {
                String result = findDestinationKey((JSONObject) value);
                if (result != null) {
                    return result;
                }
            } else if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                for (int i = 0; i < array.length(); i++) {
                    Object arrayItem = array.get(i);
                    if (arrayItem instanceof JSONObject) {
                        String result = findDestinationKey((JSONObject) arrayItem);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }

    // Method to generate a random alphanumeric string of specified length
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    // Method to generate an MD5 hash of a given input string
    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
