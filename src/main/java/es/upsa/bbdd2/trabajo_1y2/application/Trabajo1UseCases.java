package es.upsa.bbdd2.trabajo_1y2.application;

import es.upsa.bbdd2.trabajo_1y2.domain.entities.Menu;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.MenuJson;
import es.upsa.bbdd2.trabajo_1y2.domain.exceptions.TrabajoException;

import java.io.File;
import java.io.IOException;

public interface Trabajo1UseCases
{
    Menu importMenu(File file) throws TrabajoException;
}
