package es.upsa.bbdd2.trabajo_1y2.application.impl;

import es.upsa.bbdd2.trabajo_1y2.application.Dao;
import es.upsa.bbdd2.trabajo_1y2.application.Trabajo2UseCases;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.*;
import es.upsa.bbdd2.trabajo_1y2.domain.exceptions.PlatoNotFoundException;
import es.upsa.bbdd2.trabajo_1y2.domain.exceptions.TrabajoException;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder(setterPrefix = "with")
@AllArgsConstructor
public class Trabajo2UseCasesImpl implements Trabajo2UseCases
{
    private Dao dao;

    @Override
    public Plato registrarPlato(String nombre, String descripcion, double precio, Tipo tipo, List<CantidadIngrediente> cantidadesIngredientes) throws TrabajoException
    {
        // 1) Insertar el plato en la BD para obtener el 'id'
        Plato platoInsertado = dao.insertarPlato(nombre, descripcion, precio, tipo);
        // En este punto, platoInsertado tiene un id (por ejemplo, "P000")

        // 2) Preparamos una lista en memoria para los 'Compuesto'
        List<Compuesto> compuestos = new ArrayList<>();

        // 3) Por cada CantidadIngrediente, buscar/crear el Ingrediente y relacionarlo con el plato
        for (CantidadIngrediente ci : cantidadesIngredientes)
        {
            // 3.1) Buscar el ingrediente por nombre
            Ingrediente ingrediente = dao.buscarIngredienteByNombre(ci.getNombre());
            if (ingrediente == null)
            {
                // Si no existe, lo creamos
                ingrediente = dao.insertarIngrediente(ci.getNombre());
                // Este método insertará en la tabla INGREDIENTES
                // y devolverá un objeto Ingrediente con el id ("I000"), nombre, etc.
            }

            // 3.2) Insertar la relación en la tabla 'compuestos' (idPlato, idIngrediente, cantidad, unidad)
            dao.insertarCompuesto(platoInsertado.getId(),
                                 ingrediente.getId(),
                                 ci.getCantidad(),
                                 ci.getUnidadMedida()
                                 );

            // 3.3) Crear un objeto Compuesto en memoria para añadirlo al Plato
            Compuesto c = Compuesto.builder()
                                   .withIngrediente(ingrediente)
                                   .withCantidad(ci.getCantidad())
                                   .withUnidadMedida(ci.getUnidadMedida())
                                   .build();
            compuestos.add(c);
        }

        // 4) Asignar la lista de compuestos al objeto Plato que devolvemos
        platoInsertado.setCompuestos(compuestos);

        // 5) Retornar el plato con sus compuestos
        return platoInsertado;
    }

    @Override
    public Menu registrarMenu(String nombre, LocalDate desde, LocalDate hasta, List<String> platos) throws TrabajoException
    {

        // 2) Recorrer la lista de nombres de platos para:
        //    - comprobar que existan (buscarPlatoByNombre)
        //    - ir sumando sus precios
        double sumaPrecios = 0.0;
        List<Plato> platosEncontrados = new ArrayList<>();

        for (String nombrePlato : platos)
        {
            Plato p = dao.buscarPlatoByNombre(nombrePlato);
            if (p == null)
            {
                // Si no existe, lanzar excepción y no crear el menú
                throw new TrabajoException("No existe el plato con nombre: " + nombrePlato);
            }
            sumaPrecios += p.getPrecio();
            platosEncontrados.add(p);
        }

        // 3) Calcular el precio final aplicando el 15% de descuento
        double precioMenu = sumaPrecios * 0.85;

        // 4) Insertar el menú en la base de datos (similar a insertarPlato)
        //    Este método en Dao generará el 'id' (ej. "M001") a partir de la secuencia
        Menu menuInsertado = dao.insertarMenu(nombre, precioMenu, desde, hasta);

        // 5) Insertar la asociación en la tabla intermedia "menu_plato"
        for (Plato plato : platosEncontrados)
        {
            // Al ser muchos a muchos, basta con relacionarlos en la tabla intermedia
            // NO actualizamos 'idMenu' en 'platos', pues esa columna no existe en Caso B.
            dao.insertMenuPlato(menuInsertado.getId(), plato.getId());
        }

        // 6) Construir un map que agrupe los platos por Tipo (ENTRANTE, PRINCIPAL, POSTRE, etc.)
        Map<Tipo, List<Plato>> mapPlatosPorTipo = new HashMap<>();
        for (Plato plato : platosEncontrados)
        {
            mapPlatosPorTipo
                    .computeIfAbsent(plato.getTipo(), k -> new ArrayList<>())
                    .add(plato);
        }

        // 7) Asignar el map resultante al objeto menuInsertado
        menuInsertado.setPlatosByTipo(mapPlatosPorTipo);

        // 8) Retornar el menú que ya tiene:
        //    - id asignado
        //    - precio calculado con descuento
        //    - map de platos por tipo
        return menuInsertado;
    }

    @Override
    public List<Menu> buscarMenu(LocalDate fecha) throws TrabajoException {
        // 1) Buscar los menús vigentes para la fecha dada
        //    (select * from menus where desde <= fecha <= hasta)
        List<Menu> menusDisponibles = dao.findMenusByDate(fecha);

        // 2) Por cada menú, localizar sus platos en la tabla intermedia 'menu_plato'
        //    y agruparlos por tipo
        for (Menu menu : menusDisponibles)
        {
            // 2a) Recoger todos los idPlato vinculados a este menú en la tabla 'menu_plato'
            List<String> idsPlatos = dao.findPlatoIdsByMenuId(menu.getId());
            // (método en el DAO que hace: select idPlato from menu_plato where idMenu=?)

            // 2b) Cargar cada Plato y meterlo en una lista
            List<Plato> listaPlatos = new ArrayList<>();
            for (String platoId : idsPlatos)
            {
                Plato p = dao.findPlatoById(platoId);
                if (p != null)
                {
                    listaPlatos.add(p);
                }
            }

            // 2c) Agruparlos por tipo
            Map<Tipo, List<Plato>> mapPorTipo = new HashMap<>();
            for (Plato p : listaPlatos)
            {
                mapPorTipo
                        .computeIfAbsent(p.getTipo(), k -> new ArrayList<>())
                        .add(p);
            }

            // 2d) Asignar el map al menú
            menu.setPlatosByTipo(mapPorTipo);
        }

        // 3) Retornar la lista de menús completamente armada
        return menusDisponibles;
    }

    @Override
    public List<Plato> buscarPlato(Tipo tipo, List<String> ingredientes) throws TrabajoException
    {
        return dao.buscarPlato(tipo, ingredientes);
    }

    @Override
    public void subirPrecioPlato(String nombre, double porcentaje) throws TrabajoException
    {
        // 1. Buscar el plato por nombre
        Plato plato = dao.buscarPlatoByNombre(nombre);
        if (plato == null)
        {
            throw new PlatoNotFoundException();
        }

        // 2. Calcular el nuevo precio
        double nuevoPrecio = plato.getPrecio() * (1 + porcentaje);

        // 3. Actualizar el precio del plato en la base de datos
        dao.actualizarPrecioPlato(nombre, nuevoPrecio);

        // 4. Recalcular los precios de los menús afectados
        dao.actualizarPreciosMenusAfectados(plato.getId());
    }
}
