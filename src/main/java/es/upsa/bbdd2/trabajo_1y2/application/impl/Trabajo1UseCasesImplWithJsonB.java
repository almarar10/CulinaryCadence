package es.upsa.bbdd2.trabajo_1y2.application.impl;

import es.upsa.bbdd2.trabajo_1y2.application.Dao;
import es.upsa.bbdd2.trabajo_1y2.application.JsonUtils;
import es.upsa.bbdd2.trabajo_1y2.application.Trabajo1UseCases;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.Menu;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.MenuJson;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.Plato;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.Tipo;
import es.upsa.bbdd2.trabajo_1y2.domain.exceptions.TrabajoException;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder(setterPrefix = "with")
@AllArgsConstructor
public class Trabajo1UseCasesImplWithJsonB implements Trabajo1UseCases
{
    private JsonUtils jsonUtils;
    private Dao dao;

    @Override
    public Menu importMenu(File file) throws TrabajoException
    {
        try
        {
            // 1. Leer y deserializar el archivo JSON en un objeto MenuJson
            MenuJson menuJson = jsonUtils.importMenu(file);

            // 2. Validar que los campos principales del JSON no sean nulos
            if (menuJson.getNombre() == null || menuJson.getDesde() == null || menuJson.getHasta() == null || menuJson.getPlatos() == null) {
                throw new TrabajoException("El archivo JSON contiene datos inválidos o incompletos.");
            }

            // 3. Convertir las fechas desde/hasta
            LocalDate desde = LocalDate.parse(menuJson.getDesde().toString());
            LocalDate hasta = LocalDate.parse(menuJson.getHasta().toString());

            // 4. Validar que la lista de nombres de platos no esté vacía
            if (menuJson.getPlatos().isEmpty())
            {
                throw new TrabajoException("El archivo JSON no contiene platos.");
            }

            // 5. Buscar y validar que todos los platos existen en la base de datos
            List<String> nombresPlatos = menuJson.getPlatos();
            List<Plato> platosEncontrados = new ArrayList<>();
            double sumaPrecios = 0.0;

            for (String nombrePlato : nombresPlatos)
            {
                Plato plato = dao.buscarPlatoByNombre(String.valueOf(nombrePlato));
                if (plato == null) {
                    throw new TrabajoException("El plato con nombre '" + nombrePlato + "' no existe en la base de datos.");
                }
                platosEncontrados.add(plato);
                sumaPrecios += plato.getPrecio();
            }

            // 6. Calcular el precio final del menú con el 15% de descuento
            double precioMenu = sumaPrecios * 0.85;

            // 7. Registrar el menú en la base de datos
            Menu menuInsertado = dao.insertarMenu(menuJson.getNombre(), precioMenu, desde, hasta);

            // 8. Relacionar el menú con los platos en la tabla intermedia (menu_plato)
            for (Plato plato : platosEncontrados)
            {
                dao.insertMenuPlato(menuInsertado.getId(), plato.getId());
            }

            // 9. Agrupar los platos por tipo y asignarlos al menú
            Map<Tipo, List<Plato>> mapPlatosPorTipo = new HashMap<>();
            for (Plato plato : platosEncontrados)
            {
                mapPlatosPorTipo
                        .computeIfAbsent(plato.getTipo(), k -> new ArrayList<>())
                        .add(plato);
            }
            menuInsertado.setPlatosByTipo(mapPlatosPorTipo);

            // 10. Retornar el menú completo
            return menuInsertado;

        }catch (IOException ioException)
        {
            throw new TrabajoException(ioException);
        }
    }
}
