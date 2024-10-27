package dto.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dto.SheetDTO;
import dto.json.SheetDTODeserializer;

import java.lang.reflect.Type;
import java.util.*;

public class MapOfSheetsDeserializer implements JsonDeserializer<Map<String, List<SheetDTO>>> {

    @Override
    public Map<String, List<SheetDTO>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, List<SheetDTO>> result = new HashMap<>();

        JsonObject jsonObject = json.getAsJsonObject();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
                .create();

        // Iterate over each entry in the JSON object
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String userName = entry.getKey();
            JsonArray sheetArray = entry.getValue().getAsJsonArray();

            // Deserialize the list of SheetDTOs
            List<SheetDTO> sheetList = gson.fromJson(sheetArray, new TypeToken<List<SheetDTO>>(){}.getType());
            result.put(userName, sheetList);
        }

        return result;
    }
}
