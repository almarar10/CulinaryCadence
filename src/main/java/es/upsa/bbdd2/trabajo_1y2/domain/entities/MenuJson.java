package es.upsa.bbdd2.trabajo_1y2.domain.entities;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MenuJson
{
    private String nombre;
    private String desde;
    private String hasta;
    private List<String> platos;
}
