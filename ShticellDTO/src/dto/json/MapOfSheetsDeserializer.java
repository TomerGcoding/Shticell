package dto.json;

import com.google.gson.*;
import dto.SheetDTO;
import dto.json.SheetDTODeserializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MapOfSheetsDeserializer implements JsonDeserializer<Map<String, SheetDTO>> {

    @Override
    public Map<String, SheetDTO> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, SheetDTO> result = new HashMap<>();

        JsonObject jsonObject = json.getAsJsonObject();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
                .create();

        // Iterate over each entry in the JSON object
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            // Deserialize each SheetDTO
            SheetDTO sheetDTO = gson.fromJson(value, SheetDTO.class);
            result.put(key, sheetDTO);
        }

        return result;
    }
}
