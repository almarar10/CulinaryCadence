package es.upsa.bbdd2.trabajo_1y2.domain.entities;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class Compuesto
{
    private Ingrediente ingrediente;
    private int cantidad;
    private UnidadMedida unidadMedida;
}
