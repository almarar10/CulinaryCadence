package es.upsa.bbdd2.trabajo_1y2;

import es.upsa.bbdd2.trabajo_1y2.application.*;
import es.upsa.bbdd2.trabajo_1y2.application.impl.*;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.*;
import jakarta.json.JsonObject;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception
    {
        try (Dao dao = new DaoImpl("jdbc:postgresql://localhost:5432/upsa", "system", "manager"))
        {
            Trabajo2UseCases trabajo2UseCases = Trabajo2UseCasesImpl.builder()
                                                                    .withDao(dao)
                                                                    .build();

            Trabajo1UseCases trabajo1UseCases = Trabajo1UseCasesImplWithJsonB.builder()
                                                                    .withDao(dao)
                                                                    .withJsonUtils(new JsonUtilsImpl())
                                                                    .build();

            JsonAdapter<Menu> menuJsonAdapter = MenuJsonAdapter.builder().build();

            Trabajo1UseCases useCasesJsonP = Trabajo1UseCasesImplWithJsonP.builder()
                                                                          .withMenuJsonAdapter(menuJsonAdapter)
                                                                          .build();

            //Prueba para los métodos buscarPlato y subirPrecioPlato
            // 1. Probar buscarPlato
            System.out.println("Buscando platos PRINCIPAL que no contengan 'Sal' y 'Ajo':");
            List<Plato> platosSinIngredientes = trabajo2UseCases.buscarPlato(
                                                                             Tipo.PRINCIPAL,
                                                                             Arrays.asList("Sal", "Ajo")
                                                                            );
            platosSinIngredientes.forEach(plato -> System.out.println("   - " + plato.getNombre()));

            // 2. Probar subirPrecioPlato
            System.out.println("\nSubiendo precio del plato 'Lubina al Horno' un 20%:");
            try
            {
                trabajo2UseCases.subirPrecioPlato("Lubina al Horno", 0.2);
                System.out.println("   - Precio del plato 'Lubina al Horno' actualizado correctamente.");
            } catch (Exception e)
            {
                System.err.println("   - Error al actualizar el precio: " + e.getClass().getSimpleName());
            }

            // 3. Verificar que el precio del plato y los menús asociados se actualizaron
            System.out.println("\nVerificando precios actualizados:");
            Plato platoActualizado = dao.buscarPlatoByNombre("Lubina al Horno");
            System.out.println("   - Precio actualizado de 'Lubina al Horno': " + (platoActualizado != null ? platoActualizado.getPrecio() : "No encontrado"));




/*
            //Prueba JsonP
            //Archivo Json
            File file = new File("menu.json");
            //Importar el menú
            Menu menu = useCasesJsonP.importMenu(file);
            // Mostrar el menú
            System.out.println("Menú importado:");
            System.out.println("Nombre: " + menu.getNombre());
            System.out.println("Desde: " + menu.getDesde());
            System.out.println("Hasta: " + menu.getHasta());
            System.out.println("Platos: " + menu.getPlatos());

 */



/*         //Prueba JsonB
            // 2. Cargar el archivo JSON
            File file = new File("menu.json");

            // 3. Llamar a importMenu(...) para registrar el menú
            Menu menuImportado = trabajo1UseCases.importMenu(file);

            // 4. Imprimir el menú registrado
            System.out.println("\n--- MENÚ IMPORTADO ---");
            System.out.println("ID: " + menuImportado.getId());
            System.out.println("Nombre: " + menuImportado.getNombre());
            System.out.println("Precio (con 15% desc): " + menuImportado.getPrecio());
            System.out.println("Válido desde: " + menuImportado.getDesde());
            System.out.println("Válido hasta: " + menuImportado.getHasta());

            System.out.println("\nPlatos en el menú agrupados por tipo:");
            if (menuImportado.getPlatosByTipo() != null)
            {
                menuImportado.getPlatosByTipo().forEach((tipo, listaPlatos) ->
                {
                    System.out.println("Tipo: " + tipo);
                    listaPlatos.forEach(plato ->
                            System.out.println("   - " + plato.getNombre() + " (precio: " + plato.getPrecio() + ")")
                    );
                });
            }

*/

/*
            // ---------------------------------------------------------
            // 1) Registrar 2 ENTRANTES
            // ---------------------------------------------------------
            List<CantidadIngrediente> ingredientesEnsalada = List.of(
                    new CantidadIngrediente("Lechuga", 50, UnidadMedida.GRAMOS),
                    new CantidadIngrediente("Tomate", 50, UnidadMedida.GRAMOS),
                    new CantidadIngrediente("Aceite", 5, UnidadMedida.CENTILITROS),
                    new CantidadIngrediente("Sal", 2, UnidadMedida.GRAMOS)
            );
            Plato ensalada = trabajo2UseCases.registrarPlato(
                    "Ensalada Mixta",
                    "Ensalada fresca con lechuga, tomate y aliño de aceite y sal",
                    5.0,
                    Tipo.ENTRANTE,
                    ingredientesEnsalada
            );
            imprimirPlatoConCompuestos(ensalada);

            List<CantidadIngrediente> ingredientesSopa = List.of(
                    new CantidadIngrediente("Caldo", 200, UnidadMedida.CENTILITROS),
                    new CantidadIngrediente("Verduras", 100, UnidadMedida.GRAMOS),
                    new CantidadIngrediente("Sal", 2, UnidadMedida.GRAMOS)
            );
            Plato sopa = trabajo2UseCases.registrarPlato(
                    "Sopa de Verduras",
                    "Sopa casera con verduras variadas",
                    4.0,
                    Tipo.ENTRANTE,
                    ingredientesSopa
            );
            imprimirPlatoConCompuestos(sopa);

            // ---------------------------------------------------------
            // 2) Registrar 2 PRINCIPALES
            // ---------------------------------------------------------
            List<CantidadIngrediente> ingredientesLubina = List.of(
                    new CantidadIngrediente("Lubina", 1, UnidadMedida.UNIDADES),
                    new CantidadIngrediente("Aceite", 10, UnidadMedida.CENTILITROS),
                    new CantidadIngrediente("Ajo", 2, UnidadMedida.UNIDADES),
                    new CantidadIngrediente("Sal", 5, UnidadMedida.GRAMOS)
            );
            Plato lubina = trabajo2UseCases.registrarPlato(
                    "Lubina al Horno",
                    "Lubina fresca cocinada al horno con ajo y aceite",
                    18.0,
                    Tipo.PRINCIPAL,
                    ingredientesLubina
            );
            imprimirPlatoConCompuestos(lubina);

            List<CantidadIngrediente> ingredientesPaella = List.of(
                    new CantidadIngrediente("Arroz", 200, UnidadMedida.GRAMOS),
                    new CantidadIngrediente("Pollo", 1, UnidadMedida.UNIDADES),
                    new CantidadIngrediente("Gambas", 100, UnidadMedida.GRAMOS),
                    new CantidadIngrediente("Sal", 3, UnidadMedida.GRAMOS)
            );
            Plato paella = trabajo2UseCases.registrarPlato(
                    "Paella",
                    "Paella tradicional con pollo y gambas",
                    15.0,
                    Tipo.PRINCIPAL,
                    ingredientesPaella
            );
            imprimirPlatoConCompuestos(paella);

            // ---------------------------------------------------------
            // 3) Registrar 1 POSTRE
            // ---------------------------------------------------------
            List<CantidadIngrediente> ingredientesBrownie = List.of(
                    new CantidadIngrediente("Chocolate", 100, UnidadMedida.GRAMOS),
                    new CantidadIngrediente("Harina", 50, UnidadMedida.GRAMOS),
                    new CantidadIngrediente("Azucar", 30, UnidadMedida.GRAMOS),
                    new CantidadIngrediente("Huevos", 2, UnidadMedida.UNIDADES)
            );
            Plato brownie = trabajo2UseCases.registrarPlato(
                    "Brownie",
                    "Brownie de chocolate con azúcar y huevos",
                    6.0,
                    Tipo.POSTRE,
                    ingredientesBrownie
            );
            imprimirPlatoConCompuestos(brownie);

            // ---------------------------------------------------------
            // 4) Registrar 1 INFANTIL
            // ---------------------------------------------------------
            List<CantidadIngrediente> ingredientesHamburguesa = List.of(
                    new CantidadIngrediente("Pan", 1, UnidadMedida.UNIDADES),
                    new CantidadIngrediente("Carne", 100, UnidadMedida.GRAMOS),
                    new CantidadIngrediente("Queso", 20, UnidadMedida.GRAMOS),
                    new CantidadIngrediente("Ketchup", 10, UnidadMedida.GRAMOS)
            );
            Plato hamburguesaInfantil = trabajo2UseCases.registrarPlato(
                    "Hamburguesa Infantil",
                    "Hamburguesa pequeña con queso y ketchup para niños",
                    5.5,
                    Tipo.INFANTIL,
                    ingredientesHamburguesa
            );
            imprimirPlatoConCompuestos(hamburguesaInfantil);

            // ---------------------------------------------------------
            // 5) Crear 2 MENÚS con distintos platos
            // ---------------------------------------------------------
            // Menú 1: "Menú Degustación"
            //  - ENTRANTE: Ensalada Mixta, Sopa de Verduras
            //  - PRINCIPAL: Lubina al Horno, Paella
            //  - POSTRE: Brownie
            //  - INFANTIL: Hamburguesa Infantil
            List<String> nombresPlatosMenu1 = List.of(
                    "Ensalada Mixta",
                    "Sopa de Verduras",
                    "Lubina al Horno",
                    "Paella",
                    "Brownie",
                    "Hamburguesa Infantil"
            );

            Menu menuDegustacion = trabajo2UseCases.registrarMenu(
                    "Menú Degustación",
                    LocalDate.of(2025, 10, 10),
                    LocalDate.of(2025, 10, 15),
                    nombresPlatosMenu1
            );
            imprimirMenu(menuDegustacion);

            // Menú 2: "Menú Familiar"
            //  - ENTRANTE: Sopa de Verduras
            //  - PRINCIPAL: Paella
            //  - POSTRE: Brownie
            //  - INFANTIL: Hamburguesa Infantil
            //  (solo 4 platos para variar)
            List<String> nombresPlatosMenu2 = List.of(
                    "Sopa de Verduras",
                    "Paella",
                    "Brownie",
                    "Hamburguesa Infantil"
            );

            Menu menuFamiliar = trabajo2UseCases.registrarMenu(
                    "Menú Familiar",
                    LocalDate.of(2025, 11, 1),
                    LocalDate.of(2025, 11, 7),
                    nombresPlatosMenu2
            );
            imprimirMenu(menuFamiliar);




            LocalDate fechaABuscar = LocalDate.of(2025,10,12);
            List<Menu> menus = trabajo2UseCases.buscarMenu(fechaABuscar);

            mostrarMenus(menus);


*/


        }
    }

    /**
     * Método auxiliar para imprimir un Plato y sus compuestos.
     */
    private static void imprimirPlatoConCompuestos(Plato plato)
    {
        System.out.println("\n--- PLATO REGISTRADO: " + plato.getNombre() + " ---");
        System.out.println(plato);

        if (plato.getCompuestos() != null && !plato.getCompuestos().isEmpty())
        {
            System.out.println("Ingredientes del plato " + plato.getNombre() + ":");
            for (Compuesto c : plato.getCompuestos())
            {
                Ingrediente ing = c.getIngrediente();
                System.out.println("  - " + ing.getNombre() + " (id=" + ing.getId() + "), "
                        + "cantidad=" + c.getCantidad() + " "
                        + c.getUnidadMedida());
            }
        } else {
            System.out.println("El plato no tiene compuestos cargados.");
        }
    }

    /**
     * Método auxiliar para imprimir un Menu y sus platos agrupados por tipo.
     */
    private static void imprimirMenu(Menu menu)
    {
        System.out.println("\n--- MENÚ CREADO ---");
        System.out.println("ID: " + menu.getId());
        System.out.println("Nombre: " + menu.getNombre());
        System.out.println("Precio (con 15% desc): " + menu.getPrecio());
        System.out.println("Válido desde: " + menu.getDesde());
        System.out.println("Válido hasta: " + menu.getHasta());

        System.out.println("\nPlatos en el menú agrupados por tipo:");
        if (menu.getPlatosByTipo() != null)
        {
            menu.getPlatosByTipo().forEach((tipo, listaPlatos) ->
            {
                System.out.println("Tipo: " + tipo);
                listaPlatos.forEach(plato ->
                        System.out.println("   - " + plato.getNombre() + " (precio: " + plato.getPrecio() + ")")
                );
            });
        } else {
            System.out.println("No hay platos asociados en este menú.");
        }
    }

    public static void mostrarMenus(List<Menu> menus)
    {
        for (Menu m : menus)
        {
            System.out.println("Menú: " + m.getNombre());
            System.out.println(" Precio: " + m.getPrecio());
            System.out.println(" Platos:");

            // Recordemos que m.getPlatosByTipo() es un Map<Tipo, List<Plato>>
            // Simplemente, iteramos en un orden: ENTRANTE, PRINCIPAL, POSTRE, INFANTIL
            // para mostrar la plantilla exacta
            for (Tipo tipo : List.of(Tipo.ENTRANTE, Tipo.PRINCIPAL, Tipo.POSTRE, Tipo.INFANTIL))
            {
                System.out.println("  " + tipo + ":");
                List<Plato> listaPlatos = m.getPlatosByTipo().get(tipo);
                if (listaPlatos != null)
                {
                    for (Plato p : listaPlatos)
                    {
                        System.out.println("    " + p.getNombre());
                    }
                }
            }
            // Línea en blanco tras cada menú
            System.out.println();
        }
    }

}
