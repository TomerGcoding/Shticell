package dto.json;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;
import dto.CoordinateDTO;

import java.lang.reflect.Type;

public class CoordinateDTOSerializer implements JsonDeserializer<CoordinateDTO> {

    @Override
    public CoordinateDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String coordinateString = json.getAsString();  // Assuming it's in the format "row,column"

        // Split the string to get row and column values
        String[] parts = coordinateString.split(",");
        if (parts.length != 2) {
            throw new JsonParseException("Invalid coordinate format");
        }

        try {
            int row = Integer.parseInt(parts[0].trim());
            int column = Integer.parseInt(parts[1].trim());

            // Create and return the CoordinateDTO
            return new CoordinateDTO(row, column);
        } catch (NumberFormatException e) {
            throw new JsonParseException("Invalid coordinate values", e);
        }
    }
}
