package es.upsa.bbdd2.trabajo_1y2.application.impl;

import es.upsa.bbdd2.trabajo_1y2.application.JsonUtils;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.MenuJson;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import lombok.Builder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JsonUtilsImpl implements JsonUtils
{

    @Override
    public MenuJson importMenu(File file) throws IOException
    {
        Jsonb jsonb = createJsonb();
        try(FileReader fr = new FileReader(file))
        {
            return jsonb.fromJson(fr, MenuJson.class);
        }
    }

    private Jsonb createJsonb()
    {
        JsonbConfig config = new JsonbConfig();
        config.setProperty( JsonbConfig.FORMATTING, true );
        return JsonbBuilder.newBuilder()
                           .withConfig(config)
                           .build();
    }
}
