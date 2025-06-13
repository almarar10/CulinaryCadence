package es.upsa.bbdd2.trabajo_1y2.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class Plato
{
    private String id;
    private String idMenu;
    private String nombre;
    private String descripcion;
    private double precio;
    private Tipo tipo;
    private List<Compuesto> compuestos;
}
