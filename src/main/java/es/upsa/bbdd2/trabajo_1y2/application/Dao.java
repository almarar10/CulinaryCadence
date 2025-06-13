package es.upsa.bbdd2.trabajo_1y2.application;

import es.upsa.bbdd2.trabajo_1y2.domain.entities.*;
import es.upsa.bbdd2.trabajo_1y2.domain.exceptions.TrabajoException;

import java.time.LocalDate;
import java.util.List;

public interface Dao extends AutoCloseable
{
    Plato insertarPlato(String nombre, String descripcion, Double precio, Tipo tipo) throws TrabajoException;
    Plato buscarPlatoByNombre(String nombre) throws TrabajoException;
    Menu insertarMenu(String nombre, double precio, LocalDate desde, LocalDate hasta) throws TrabajoException;
    Ingrediente buscarIngredienteByNombre(String nombre) throws TrabajoException;
    Ingrediente insertarIngrediente(String nombre) throws TrabajoException;
    void insertarCompuesto(String idPlato, String idIngrediente, int cantidad, UnidadMedida unidad) throws TrabajoException;
    List<Menu> findMenusByDate(LocalDate fecha) throws TrabajoException;
    List<Plato> findPlatosByMenuId(String idMenu) throws TrabajoException;
    Plato findPlatoById(String idPlato) throws TrabajoException;
    void updatePlatoIdMenu(Plato plato) throws TrabajoException;
    void insertMenuPlato(String idMenu, String idPlato) throws TrabajoException;
    List<String> findPlatoIdsByMenuId(String idMenu) throws TrabajoException;
    List<Plato> buscarPlato(Tipo tipo, List<String> ingredientes) throws TrabajoException;
    void actualizarPrecioPlato(String nombre, double nuevoPrecio) throws TrabajoException;
    void actualizarPreciosMenusAfectados(String idPlato) throws TrabajoException;
}
