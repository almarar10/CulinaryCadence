package es.upsa.bbdd2.trabajo_1y2.application;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

public interface JsonAdapter<T>
{
    T fromJson(JsonStructure json);

    default List<T> fromJson(JsonArray json)
    {
        List<T> list = new ArrayList<T>();
        for (JsonValue itemJV : json)
        {
            JsonStructure itemJS = (JsonStructure) itemJV;
            T item = fromJson(itemJS);
            list.add(item);
        }
        return list;
    }
}
