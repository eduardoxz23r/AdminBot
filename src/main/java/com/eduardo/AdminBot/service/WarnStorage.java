package com.eduardo.AdminBot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class WarnStorage {

    private static final String ARQUIVO = "warns.json";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ObjectMapper mapper = new ObjectMapper();

    // Map: userId -> lista de advertências
    private Map<String, List<String>> advertencias = new HashMap<>();

    public WarnStorage() {
        carregar();
    }

    /**
     * Adiciona uma advertência e salva no arquivo
     */
    public void adicionar(String userId, String motivo, String moderador) {
        advertencias.computeIfAbsent(userId, k -> new ArrayList<>());
        String registro = "⚠️ " + LocalDateTime.now().format(FORMATTER) +
                " | Mod: " + moderador +
                " | Motivo: " + motivo;
        advertencias.get(userId).add(registro);
        salvar();
    }

    /**
     * Retorna todas as advertências do usuário
     */
    public List<String> listar(String userId) {
        return advertencias.getOrDefault(userId, Collections.emptyList());
    }

    /**
     * Retorna a quantidade de advertências
     */
    public int contar(String userId) {
        return listar(userId).size();
    }

    /**
     * Limpa todas as advertências do usuário
     */
    public void limpar(String userId) {
        advertencias.remove(userId);
        salvar();
    }

    /**
     * Remove uma advertência específica pelo índice (começa em 0)
     */
    public boolean remover(String userId, int indice) {
        List<String> lista = advertencias.get(userId);
        if (lista == null || indice < 0 || indice >= lista.size()) return false;
        lista.remove(indice);
        if (lista.isEmpty()) advertencias.remove(userId);
        salvar();
        return true;
    }

    /**
     * Salva o mapa no arquivo JSON
     */
    private void salvar() {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(ARQUIVO), advertencias);
        } catch (IOException e) {
            System.err.println(">> [AdminBot] Erro ao salvar warns: " + e.getMessage());
        }
    }

    /**
     * Carrega o arquivo JSON ao iniciar
     */
    private void carregar() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return;

        try {
            advertencias = mapper.readValue(
                    arquivo,
                    new TypeReference<Map<String, List<String>>>() {}
            );
            System.out.println(">> [AdminBot] Warns carregados: " + advertencias.size() + " usuário(s).");
        } catch (IOException e) {
            System.err.println(">> [AdminBot] Erro ao carregar warns: " + e.getMessage());
        }
    }
}