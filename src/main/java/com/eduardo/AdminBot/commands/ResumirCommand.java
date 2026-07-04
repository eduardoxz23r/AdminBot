package com.eduardo.AdminBot.commands;

import com.eduardo.AdminBot.service.GrokService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.stream.Collectors;

@Component
public class ResumirCommand extends ListenerAdapter {

    @Autowired
    private GrokService geminiService; // Trocado de Claude para Gemini

    private static final String SYSTEM_PROMPT = """
            Você é um assistente de servidor Discord.
            Receberá um conjunto de mensagens de um canal e deve gerar um resumo claro e objetivo.
            Destaque os principais tópicos discutidos, decisões tomadas e pontos de atenção.
            Use tópicos (bullet points) para facilitar a leitura.
            Responda em português de forma concisa.
            """;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("resumir")) return;

        // Pega a quantidade do comando ou usa 20 como padrão
        int quantidade = event.getOption("quantidade") != null
                ? (int) event.getOption("quantidade").getAsLong()
                : 20;

        // Limite para não estourar o contexto da IA ou o tempo de resposta
        if (quantidade > 50) quantidade = 50;

        // Avisa ao Discord que o bot está processando (IA demora um pouco)
        event.deferReply().queue();

        int qtdFinal = quantidade;
        event.getChannel().asTextChannel().getHistory().retrievePast(quantidade).queue(mensagens -> {
            if (mensagens.isEmpty()) {
                event.getHook().sendMessage("❌ Nenhuma mensagem recente encontrada para resumir.").queue();
                return;
            }

            // Formata o histórico: "Nome: Mensagem"
            String historico = mensagens.stream()
                    .filter(m -> !m.getAuthor().isBot()) // Ignora mensagens de outros bots
                    .map(m -> m.getAuthor().getName() + ": " + m.getContentDisplay())
                    .collect(Collectors.joining("\n"));

            if (historico.isBlank()) {
                event.getHook().sendMessage("❌ Só encontrei mensagens de bots, não tenho o que resumir.").queue();
                return;
            }

            // Envia para o Gemini
            String resumo = geminiService.perguntar(SYSTEM_PROMPT,
                    "Resumo das últimas " + qtdFinal + " mensagens:\n\n" + historico);

            // Cria um Embed bonitão (Roxo/Neon como você gosta)
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("📝 Resumo da Conversa")
                    .setDescription(resumo)
                    .setColor(new Color(138, 43, 226)) // Roxo "BlueViolet"
                    .setFooter("Analisadas " + qtdFinal + " mensagens a pedido de " + event.getUser().getName());

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        });
    }
}