DROP TABLE if exists menu_plato;
DROP TABLE if exists compuestos;
DROP TABLE if exists ingredientes;
DROP TABLE if exists platos;
DROP TABLE if exists menus;

DROP SEQUENCE seq_menus;
DROP SEQUENCE seq_platos;
DROP SEQUENCE seq_ingredientes;

-- 1.Tabla Menús
CREATE TABLE menus
(
    id VARCHAR(5),
    nombre VARCHAR(100),
    precio NUMERIC(5,2),
    desde DATE,
    hasta DATE,

    CONSTRAINT "PK_MENUS" PRIMARY KEY (id),
    CONSTRAINT "NN_MENUS.NOMBRE" CHECK ( nombre IS NOT NULL),
    CONSTRAINT "NN_MENUS.PRECIO" CHECK ( precio IS NOT NULL ),
    CONSTRAINT "NN_MENUS.DESDE" CHECK ( desde IS NOT NULL),
    CONSTRAINT "NN_MENUS.HASTA" CHECK ( hasta IS NOT NULL)

);

-- 2. Tabla Platos
CREATE TABLE platos
(
    id VARCHAR(5),
    nombre VARCHAR(150),
    descripcion VARCHAR(450),
    precio NUMERIC(5,2),
    tipo VARCHAR(30),

    CONSTRAINT "PK_PLATOS" PRIMARY KEY (id),

    CONSTRAINT "UK_PLATOS.NOMBRE" UNIQUE (nombre),
    CONSTRAINT "NN_PLATOS.NOMBRE" CHECK ( nombre IS NOT NULL ),
    CONSTRAINT "NN_PLATOS.DESCRIPCION" CHECK ( descripcion IS NOT NULL ),
    CONSTRAINT "NN_PLATOS.PRECIO" CHECK ( precio IS NOT NULL ),
    CONSTRAINT "NN_PLATOS.TIPO" CHECK ( tipo IS NOT NULL ),
    CONSTRAINT "CK_PLATOS.TIPO" CHECK (tipo IN('ENTRANTE', 'PRINCIPAL', 'POSTRE', 'INFANTIL'))

);

-- 3. Tabla Ingredientes (solo ID y nombre)
CREATE TABLE ingredientes
(
    id VARCHAR(5),
    nombre VARCHAR(150),
    CONSTRAINT "PK_INGREDIENTES" PRIMARY KEY (id),
    CONSTRAINT "UK_INGREDIENTES.NOMBRE" UNIQUE (nombre),
    CONSTRAINT "NN_INGREDIENTES.NOMBRE" CHECK ( nombre IS NOT NULL )
);

-- 4. Tabla Compuestos (relación plato <-> ingrediente)
CREATE TABLE compuestos
(
   idPlato VARCHAR(5),
   idIngrediente VARCHAR(5),
   cantidad NUMERIC,
   unidad VARCHAR(20),
   CONSTRAINT FK_PLATO_INGREDIENTE_PLATOS FOREIGN KEY (idPlato)
                                          REFERENCES platos(id),
   CONSTRAINT FK_PLATO_INGREDIENTE_INGREDIENTES FOREIGN KEY (idIngrediente)
                                                REFERENCES ingredientes(id),
   CONSTRAINT CK_PLATO_INGREDIENTE_CANTIDAD CHECK (cantidad > 0 AND cantidad <= 9999),
   CONSTRAINT CK_PLATO_INGREDIENTE_UNIDAD CHECK (unidad IN ('GRAMOS','UNIDADES','CENTILITROS')),

   CONSTRAINT PK_COMPUESTOS PRIMARY KEY (idPlato, idIngrediente)
);

-- 5. Tabla Menu_plato (relación plato <-> menú)
CREATE TABLE menu_plato
(
   idMenu VARCHAR(5),
   idPlato VARCHAR(5),
   CONSTRAINT PK_MENU_PLATO PRIMARY KEY (idMenu, idPlato),
   CONSTRAINT FK_MENU_PLATO_MENU FOREIGN KEY (idMenu) REFERENCES menus(id),
   CONSTRAINT FK_MENU_PLATO_PLATO FOREIGN KEY (idPlato) REFERENCES platos(id)
);


-- 5. Secuencias
CREATE  SEQUENCE seq_menus
    MINVALUE 0
    MAXVALUE 99999
    START WITH 0
    INCREMENT BY 1;

CREATE SEQUENCE seq_platos
    MINVALUE 0
    MAXVALUE 99999
    START WITH 0
    INCREMENT BY 1;

CREATE SEQUENCE seq_ingredientes
    MINVALUE 0
    MAXVALUE 99999
    START WITH 0
    INCREMENT BY 1;

