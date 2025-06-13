package es.upsa.bbdd2.trabajo_1y2.application;

import es.upsa.bbdd2.trabajo_1y2.domain.entities.CantidadIngrediente;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.Menu;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.Plato;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.Tipo;
import es.upsa.bbdd2.trabajo_1y2.domain.exceptions.TrabajoException;

import java.time.LocalDate;
import java.util.List;

public interface Trabajo2UseCases
{
    Plato registrarPlato(String nombre, String descripcion, double precio, Tipo tipo, List<CantidadIngrediente> cantidadesIngredientes)throws TrabajoException;
    Menu registrarMenu(String nombre, LocalDate desde, LocalDate hasta, List<String> platos) throws TrabajoException;
    List<Menu> buscarMenu(LocalDate fecha) throws TrabajoException;
    List<Plato> buscarPlato(Tipo tipo, List<String> ingredientes) throws TrabajoException;
    void subirPrecioPlato(String nombre, double porcentaje) throws TrabajoException;
}
