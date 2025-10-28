package org.example.jsoncreator;

import org.example.models.HorarioFuncionamento;
import org.example.service.HorarioService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.util.List;

public class HorarioJSONExportar {
    public static void exportarPadrao(String caminhoArquivo) {
        try {
            HorarioService horarioService = new HorarioService();
            List<HorarioFuncionamento> horariosPadrao = horarioService.listarHorariosPadrao();

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(caminhoArquivo), horariosPadrao);
        } catch (Exception e) {
            System.err.println("✗ Erro ao exportar horários padrão: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void exportarEspeciais(String caminhoArquivo) {
        try {
            HorarioService horarioService = new HorarioService();
            List<HorarioFuncionamento> horariosEspeciais = horarioService.listarHorariosEspeciais();

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(caminhoArquivo), horariosEspeciais);
        } catch (Exception e) {
            System.err.println("✗ Erro ao exportar horários especiais: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
