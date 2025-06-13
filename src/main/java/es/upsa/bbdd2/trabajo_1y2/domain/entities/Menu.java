package es.upsa.bbdd2.trabajo_1y2.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
public class Menu
{
    private String id;
    private String nombre;
    private Double precio;
    private LocalDate desde;
    private LocalDate hasta;

    private List<String> platos;
    /**
     * Agrupa los platos por tipo.
     * clave = Tipo (ENTRANTE, PRINCIPAL, POSTRE, INFANTIL)
     * valor = Lista de platos de ese tipo
     */
    private Map<Tipo, List<Plato>> platosByTipo;
}
