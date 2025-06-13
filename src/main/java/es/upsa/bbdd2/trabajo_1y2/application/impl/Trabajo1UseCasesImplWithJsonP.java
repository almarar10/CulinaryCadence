package es.upsa.bbdd2.trabajo_1y2.application.impl;

import es.upsa.bbdd2.trabajo_1y2.application.JsonAdapter;
import es.upsa.bbdd2.trabajo_1y2.application.Trabajo1UseCases;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.Menu;
import es.upsa.bbdd2.trabajo_1y2.domain.exceptions.TrabajoException;
import jakarta.json.Json;
import jakarta.json.JsonReader;
import lombok.Builder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Builder(setterPrefix = "with")
public class Trabajo1UseCasesImplWithJsonP implements Trabajo1UseCases
{
    private JsonAdapter<Menu> menuJsonAdapter;

    @Override
    public Menu importMenu(File file) throws TrabajoException
    {
        try(FileReader fr = new FileReader(file);
            JsonReader jr = Json.createReader(fr))
        {
            return  menuJsonAdapter.fromJson(jr.readObject());
        }catch (IOException e)
        {
            throw new TrabajoException(e);
        }
    }
}
