package es.upsa.bbdd2.trabajo_1y2.application.impl;

import es.upsa.bbdd2.trabajo_1y2.application.Dao;
import es.upsa.bbdd2.trabajo_1y2.domain.entities.*;
import es.upsa.bbdd2.trabajo_1y2.domain.exceptions.*;
import org.postgresql.Driver;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DaoImpl implements Dao
{

    private Connection connection;

    public DaoImpl(String url, String user, String password) throws TrabajoException
    {
        try
        {
            DriverManager.registerDriver( new Driver());
            connection = DriverManager.getConnection(url, user, password);

        }catch (SQLException sqlException)
        {
            throw manageException(sqlException);
        }
    }

    @Override
    public Plato insertarPlato(String nombre, String descripcion, Double precio, Tipo tipo) throws TrabajoException
    {
       final String SEQ_SQL = "SELECT nextval('seq_platos') as seq_val";
       long seqVal;

       try (
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(SEQ_SQL)
            )
       {
           if(rs.next())
           {
               seqVal = rs.getLong("seq_val");
           }else {
               throw new TrabajoException("No se pudo obtener el valor de la secuencia seq_platos.");
           }

       }catch (SQLException sqlException)
       {
           throw manageException(sqlException);
       }

       String formattedId = String.format("P%03d", seqVal);

       final String SQL = """
                            INSERT INTO platos(id, nombre, descripcion, precio, tipo)
                            VALUES (?, ?, ?, ?, ?)
                            """;

       try(PreparedStatement prStmnt = connection.prepareStatement(SQL))
       {
           prStmnt.setString(1, formattedId);
           prStmnt.setString(2, nombre);
           prStmnt.setString(3, descripcion);
           prStmnt.setDouble(4, precio);
           prStmnt.setString(5, tipo.toString());

           prStmnt.executeUpdate();

                   return Plato.builder()
                               .withId(formattedId)
                               .withNombre(nombre)
                               .withDescripcion(descripcion)
                               .withPrecio(precio)
                               .withTipo(tipo)
                               .build();

       }catch (SQLException sqlException)
       {
           throw manageException(sqlException);
       }
    }

    @Override
    public Plato buscarPlatoByNombre(String nombre) throws TrabajoException
    {
        final String SQL = """
                            SELECT id, nombre, descripcion, precio, tipo
                              FROM platos
                              WHERE nombre = ?
                            """;
        try(PreparedStatement ps = connection.prepareStatement(SQL))
        {
            ps.setString(1, nombre);

            try(ResultSet rs = ps.executeQuery())
            {
                if(rs.next())
                {
                    String id = rs.getString("id");
                    String nom = rs.getString("nombre");
                    String desc = rs.getString("descripcion");
                    double prec = rs.getDouble("precio");
                    String tipoStr = rs.getString("tipo");

                    return Plato.builder()
                                .withId(id)
                                .withNombre(nom)
                                .withDescripcion(desc)
                                .withPrecio(prec)
                                .withTipo(Tipo.valueOf(tipoStr))
                                .build();
                }else{
                    return null;
                }
            }

        }catch (SQLException sqlException)
        {
            throw manageException(sqlException);
        }
    }

    @Override
    public Menu insertarMenu(String nombre, double precio, LocalDate desde, LocalDate hasta) throws TrabajoException
    {
        // 1) Obtener el valor de la secuencia seq_menus
        final String SEQ_SQL = "SELECT nextval('seq_menus') as seq_val";
        long seqVal;

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(SEQ_SQL))
        {

            if (rs.next())
            {
                seqVal = rs.getLong("seq_val");
            } else {
                throw new TrabajoException("No se pudo obtener valor de la secuencia seq_menus.");
            }

        } catch (SQLException e) {
            throw manageException(e);
        }

        // 2) Formatear el ID, p. ej. "M001"
        String generatedId = String.format("M%03d", seqVal);

        // 3) Insertar el menú en la tabla MENUS
        final String SQL = """
                           INSERT INTO menus (id, nombre, precio, desde, hasta)
                           VALUES (?, ?, ?, ?, ?)
                            """;

        try (PreparedStatement ps = connection.prepareStatement(SQL))
        {
            ps.setString(1, generatedId);
            ps.setString(2, nombre);
            ps.setDouble(3, precio);
            ps.setObject(4, desde);
            ps.setObject(5, hasta);

            ps.executeUpdate();

            // Retornamos un objeto Menu con el ID y demás atributos
            return Menu.builder()
                       .withId(generatedId)
                       .withNombre(nombre)
                       .withPrecio(precio)
                       .withDesde(desde)
                       .withHasta(hasta)
                       .build();

        } catch (SQLException e) {
            throw manageException(e);
        }
    }

    @Override
    public Ingrediente buscarIngredienteByNombre(String nombre) throws TrabajoException
    {
        final String SQL = """
                            SELECT id, nombre
                              FROM ingredientes
                            WHERE nombre = ?
                            """;
        try(PreparedStatement ps = connection.prepareStatement(SQL))
        {
            ps.setString(1, nombre);

            try(ResultSet rs = ps.executeQuery())
            {
                if(rs.next()){
                    return Ingrediente.builder()
                                      .withId(rs.getString("id"))
                                      .withNombre(rs.getString("nombre"))
                                      .build();
                }else{
                    return null;
                }
            }
        }catch (SQLException sqlException)
        {
            throw manageException(sqlException);
        }
    }

    @Override
    public Ingrediente insertarIngrediente(String nombre) throws TrabajoException
    {
       final String SEQ_SQL = """
                                SELECT nextval('seq_ingredientes') as seq_val
                                """;
       long seqVal;

       try(
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(SEQ_SQL)
          )
       {

           if(rs.next())
           {
               seqVal = rs.getLong("seq_val");
           }else{
               throw new TrabajoException("No se pudo obtener el valor de la secuencia seq_ingredientes");
           }

       }catch (SQLException sqlException)
       {
           throw manageException(sqlException);
       }

       String generatedId = String.format("I%03d", seqVal);

       final String SQL = """
                            INSERT INTO ingredientes (id, nombre)
                            VALUES (?, ?)
                            """;

       try(PreparedStatement ps = connection.prepareStatement(SQL))
       {
           ps.setString(1, generatedId);
           ps.setString(2, nombre);

           ps.executeUpdate();

           return Ingrediente.builder()
                             .withId(generatedId)
                             .withNombre(nombre)
                             .build();
       }catch (SQLException sqlException)
       {
           throw manageException(sqlException);
       }
    }

    @Override
    public void insertarCompuesto(String idPlato, String idIngrediente, int cantidad, UnidadMedida unidad) throws TrabajoException
    {
        final String SQL = """
                            INSERT INTO compuestos(idPlato, idIngrediente, cantidad, unidad)
                            VALUES(?, ?, ?, ?)
                            """;

        try(PreparedStatement ps = connection.prepareStatement(SQL))
        {
            ps.setString(1, idPlato);
            ps.setString(2, idIngrediente);
            ps.setInt(3, cantidad);
            ps.setString(4, unidad.name());

            ps.executeUpdate();

        }catch (SQLException sqlException)
        {
            throw manageException(sqlException);
        }
    }

    @Override
    public List<Menu> findMenusByDate(LocalDate fecha) throws TrabajoException
    {
        final String SQL = """
                            SELECT id, nombre, precio, desde, hasta
                             FROM menus
                             WHERE desde <= ?
                                AND hasta >= ?
                            """;
        List<Menu> menus = new ArrayList<>();

        try(PreparedStatement ps = connection.prepareStatement(SQL))
        {
            ps.setObject(1, fecha);
            ps.setObject(2, fecha);

            try(ResultSet rs = ps.executeQuery())
            {
                while(rs.next())
                {
                    Menu menu = Menu.builder()
                                    .withId(rs.getString("id"))
                                    .withNombre(rs.getString("nombre"))
                                    .withPrecio(rs.getDouble("precio"))
                                    .withDesde(rs.getObject("desde", LocalDate.class))
                                    .withHasta(rs.getObject("hasta", LocalDate.class))
                                    .build();
                    menus.add(menu);
                }
            }
        }catch (SQLException sqlException)
        {
            throw manageException(sqlException);
        }
        return menus;
    }

    @Override
    public List<Plato> findPlatosByMenuId(String idMenu) throws TrabajoException
    {
        // 1) Buscamos todos los idPlato en la tabla intermedia
        final String SQL = "SELECT idPlato FROM menu_plato WHERE idMenu = ?";
        List<Plato> lista = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setString(1, idMenu);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String idPlato = rs.getString("idPlato");
                    // 2) Buscamos el plato en la tabla 'platos'
                    //    (o puedes hacer JOIN en una sola consulta, a tu gusto)
                    Plato plato = findPlatoById(idPlato);
                    if (plato != null) {
                        lista.add(plato);
                    }
                }
            }
        } catch (SQLException e) {
            throw manageException(e);
        }
        return lista;
    }

    @Override
    public Plato findPlatoById(String idPlato) throws TrabajoException
    {
        final String SQL = """
                            SELECT id, nombre, descripcion, precio, tipo
                            FROM platos
                            WHERE id = ?
                            """;
        try(PreparedStatement ps = connection.prepareStatement(SQL))
        {
         ps.setString(1, idPlato);

         try(ResultSet rs = ps.executeQuery())
         {
             if(rs.next())
             {
                 return Plato.builder()
                             .withId(rs.getString("id"))
                             .withNombre(rs.getString("nombre"))
                             .withDescripcion(rs.getString("descripcion"))
                             .withPrecio(rs.getDouble("precio"))
                             .withTipo(Tipo.valueOf(rs.getString("tipo")))
                             .build();
             }else {
                 return null;
             }
         }
        }catch (SQLException sqlException)
        {
            throw manageException(sqlException);
        }
    }

    @Override
    public void updatePlatoIdMenu(Plato plato) throws TrabajoException
    {
        final String SQL = """
                            UPDATE platos 
                             SET idMenu = ?
                            WHERE id = ?
                            """;
        try(PreparedStatement ps = connection.prepareStatement(SQL))
        {
            ps.setString(1, plato.getIdMenu());
            ps.setString(2, plato.getId());
            ps.executeUpdate();
        }catch (SQLException sqlException)
        {
            throw manageException(sqlException);
        }
    }

    @Override
    public void insertMenuPlato(String idMenu, String idPlato) throws TrabajoException
    {
        final String SQL = """
                            INSERT INTO menu_plato(idMenu, idPlato)
                            VALUES(?, ?)
                            """;
        try(PreparedStatement ps = connection.prepareStatement(SQL))
        {
            ps.setString(1, idMenu);
            ps.setString(2, idPlato);
            ps.executeUpdate();
        }catch (SQLException sqlException)
        {
            throw manageException(sqlException);
        }
    }

    @Override
    public List<String> findPlatoIdsByMenuId(String idMenu) throws TrabajoException
    {
        final String SQL = """
                            SELECT idPlato
                             FROM menu_plato
                             WHERE idMenu = ?
                            """;
        List<String> listaIds = new ArrayList<>();
        try(PreparedStatement ps = connection.prepareStatement(SQL))
        {
            ps.setString(1, idMenu);

            try(ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    listaIds.add(rs.getString("idPlato"));
                }
            }
        }catch (SQLException sqlException)
        {
            throw manageException(sqlException);
        }
        return listaIds;
    }

    @Override
    public List<Plato> buscarPlato(Tipo tipo, List<String> ingredientes) throws TrabajoException
    {
        final String SQL = """
                            SELECT DISTINCT p.id, p.nombre, p.descripcion, p.precio, p.tipo
                            FROM platos p
                            LEFT JOIN compuestos c ON p.id = c.idPlato
                            LEFT JOIN ingredientes i ON c.idIngrediente = i.id
                            WHERE p.tipo = ?
                              AND (i.nombre IS NULL OR i.nombre NOT IN (%s))
                            """;

        // Construir la lista de placeholders para los nombres de ingredientes
        String placeholders = ingredientes.stream()
                                          .map(i -> "?")
                                          .collect(Collectors.joining(", "));
        String query = SQL.formatted(placeholders);

        List<Plato> platos = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query))
        {
            ps.setString(1, tipo.name());
            for (int i = 0; i < ingredientes.size(); i++)
            {
                ps.setString(i + 2, ingredientes.get(i)); // Ingredientes empiezan desde el índice 2
            }

            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    platos.add(Plato.builder()
                                    .withId(rs.getString("id"))
                                    .withNombre(rs.getString("nombre"))
                                    .withDescripcion(rs.getString("descripcion"))
                                    .withPrecio(rs.getDouble("precio"))
                                    .withTipo(Tipo.valueOf(rs.getString("tipo")))
                                    .build());
                }
            }
        } catch (SQLException e)
        {
            throw manageException(e);
        }
        return platos;
    }

    @Override
    public void actualizarPrecioPlato(String nombre, double nuevoPrecio) throws TrabajoException
    {
        final String SQL = "UPDATE platos SET precio = ? WHERE nombre = ?";

        try (PreparedStatement ps = connection.prepareStatement(SQL))
        {
            ps.setDouble(1, nuevoPrecio);
            ps.setString(2, nombre);
            ps.executeUpdate();
        } catch (SQLException e)
        {
            throw manageException(e);
        }
    }

    @Override
    public void actualizarPreciosMenusAfectados(String idPlato) throws TrabajoException
    {
        final String SQL = """
                              UPDATE menus m
                              SET precio = (
                                  SELECT SUM(p.precio) * 0.85
                                  FROM menu_plato mp
                                  JOIN platos p ON mp.idPlato = p.id
                                  WHERE mp.idMenu = m.id
                              )
                              WHERE EXISTS (
                                  SELECT 1
                                  FROM menu_plato mp
                                  WHERE mp.idMenu = m.id AND mp.idPlato = ?
                              );
                              """;

        try (PreparedStatement ps = connection.prepareStatement(SQL))
        {
            ps.setString(1, idPlato);
            ps.executeUpdate();
        } catch (SQLException e)
        {
            throw manageException(e);
        }
    }

    @Override
    public void close() throws Exception
    {
        if(connection != null)
        {
            connection.close();
            connection = null;
        }
    }

    private TrabajoException manageException(SQLException sqlException)
    {
        String message = sqlException.getMessage();

        // 1) Manejo de constraints de PLATOS
        if (message.contains("UK_PLATOS.NOMBRE")) return new DuplicatedPlatoNameException();
        if (message.contains("NN_PLATOS.NOMBRE")) return new NamePlatoRequiredException();
        if (message.contains("NN_PLATOS.DESCRIPCION")) return new DescriptionRequiredException();
        if (message.contains("NN_PLATOS.PRECIO")) return new PriceRequiredException();
        if (message.contains("NN_PLATOS.TIPO")) return new TipoRequiredException();
        if (message.contains("CK_PLATOS.TIPO")) return new TipoInvalidException();

        // 2) Manejo de constraints de MENUS
        if (message.contains("NN_MENUS.NOMBRE")) return new NameMenuRequiredException();
        if (message.contains("NN_MENUS.PRECIO")) return new PriceMenuRequiredException();
        if (message.contains("NN_MENUS.DESDE")) return new DesdeMenuRequiredException();
        if (message.contains("NN_MENUS.HASTA")) return new HastaMenuRequiredException();

        // 3) Manejo de constraints de INGREDIENTES
        if (message.contains("UK_INGREDIENTES.NOMBRE")) return new DuplicatedIngredientNameException();
        if (message.contains("NN_INGREDIENTES.NOMBRE")) return new NameIngredientRequiredException();

        // 4) Manejo de constraints de COMPUESTOS
        if (message.contains("FK_PLATO_INGREDIENTE_PLATOS")) return new PlatoNotFoundException();
        if (message.contains("FK_PLATO_INGREDIENTE_INGREDIENTES")) return new IngredientNotFoundException();
        if (message.contains("CK_PLATO_INGREDIENTE_CANTIDAD")) return new InvalidIngredientQuantityException();
        if (message.contains("CK_PLATO_INGREDIENTE_UNIDAD")) return new InvalidIngredientUnitException();

        // 5) Manejo de constraints de MENU_PLATO
        if (message.contains("FK_MENU_PLATO_MENU")) return new MenuNotFoundException();
        if (message.contains("FK_MENU_PLATO_PLATO")) return new PlatoNotFoundException();
        if (message.contains("PK_MENU_PLATO")) return new DuplicatedMenuPlatoException();

        // 6) Si no coincide con ningún constraint "conocido"
        return new NonControlledException(sqlException);
    }

}
