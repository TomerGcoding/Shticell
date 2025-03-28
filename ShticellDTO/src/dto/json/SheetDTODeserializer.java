package dto.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dto.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SheetDTODeserializer implements JsonDeserializer<SheetDTO> {

    @Override
    public SheetDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Map<CoordinateDTO, CellDTO> activeCells = new HashMap<>();
        JsonObject activeCellsJson = jsonObject.getAsJsonObject("activeCells");
        for (Map.Entry<String, JsonElement> entry : activeCellsJson.entrySet()) {
            String key = entry.getKey();
            CoordinateDTO coordinate = parseCoordinate(key);
            CellDTO cellDTO = context.deserialize(entry.getValue(), CellDTO.class);
            activeCells.put(coordinate, cellDTO);
        }

        Map<String, RangeDTO> activeRanges = context.deserialize(jsonObject.get("activeRanges"), new TypeToken<Map<String, RangeDTO>>() {}.getType());
        int currVersion = jsonObject.get("currVersion").getAsInt();
        String sheetName = jsonObject.get("sheetName").getAsString();
        SheetPropertiesDTO properties = context.deserialize(jsonObject.get("properties"), SheetPropertiesDTO.class);

        SheetUsersAccessDTO sheetUsersAccess = context.deserialize(jsonObject.get("sheetUsersAccess"), SheetUsersAccessDTO.class);

        return new SheetDTO(activeCells, activeRanges, currVersion, sheetName, properties, sheetUsersAccess);
    }

    private CoordinateDTO parseCoordinate(String key) {
        String[] parts = key.split(",");
        int row = Integer.parseInt(parts[0]);
        int column = Integer.parseInt(parts[1]);
        return new CoordinateDTO(row, column);
    }
}
