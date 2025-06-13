package es.upsa.bbdd2.trabajo_1y2.application;

import es.upsa.bbdd2.trabajo_1y2.domain.entities.MenuJson;

import java.io.File;
import java.io.IOException;

public interface JsonUtils
{
    MenuJson importMenu(File file) throws IOException;
}
