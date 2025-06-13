package es.upsa.bbdd2.trabajo_1y2.application.impl;

import es.upsa.bbdd2.trabajo_1y2.application.JsonAdapter;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.Menu;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.Plato;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import lombok.Builder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder(setterPrefix = "with")
public class MenuJsonAdapter implements JsonAdapter<Menu>
{

    @Override
    public Menu fromJson(JsonStructure json)
    {
        JsonObject jo = json.asJsonObject();

        // Leer propiedades del menú
        String nombre = jo.getString("nombre");
        LocalDate desde = LocalDate.parse(jo.getString("desde"));
        LocalDate hasta = LocalDate.parse(jo.getString("hasta"));

        // Leer la lista de nombres de platos
        JsonArray platosArray = jo.getJsonArray("platos");
        List<String> platos = new ArrayList<>();
        for (int i = 0; i < platosArray.size(); i++)
        {
            platos.add(platosArray.getString(i));
        }

        return Menu.builder()
                   .withNombre(jo.getString("nombre"))
                   .withDesde(LocalDate.parse(jo.getString("desde")))
                   .withHasta(LocalDate.parse(jo.getString("hasta")))
                   .withPlatos(platos)
                   .build();
    }
}
