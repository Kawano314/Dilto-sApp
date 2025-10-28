package org.example.jsoncreator;

import org.example.service.ReservaSinucaService;
import org.example.models.ReservaSinuca;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.util.List;

public class ReservaSinucaJSONExportar {
    public static void exportarParaJSON(String caminhoArquivo) {
        try {
            ReservaSinucaService reservaService = new ReservaSinucaService();
            List<ReservaSinuca> reservas = reservaService.listarTodasReservasAtivas();

            ObjectMapper mapper = new ObjectMapper();

            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(caminhoArquivo), reservas);
        } catch (Exception e) {
            System.err.println("✗ Erro ao exportar JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}