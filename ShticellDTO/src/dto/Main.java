package dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CoordinateDTO;
import dto.json.CoordinateDTOSerializer;

public class Main {
    public static void main(String[] args) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Register the custom deserializer for CoordinateDTO
        gsonBuilder.registerTypeAdapter(CoordinateDTO.class, new CoordinateDTOSerializer());

        Gson gson = gsonBuilder.create();

        // Example JSON string for CoordinateDTO
        String json = "\"2,3\"";  // Example of row 2, column 3

        // Deserialize the JSON string to a CoordinateDTO object
        CoordinateDTO coordinate = gson.fromJson(json, CoordinateDTO.class);

        // Output
        System.out.println("Row: " + coordinate.getRow() + ", Column: " + coordinate.getColumn());
    }
}

