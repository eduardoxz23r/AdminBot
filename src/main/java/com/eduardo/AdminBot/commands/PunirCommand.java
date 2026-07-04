package com.eduardo.AdminBot.commands;

import com.eduardo.AdminBot.service.GrokService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class PunirCommand extends ListenerAdapter {

    @Autowired
    private GrokService geminiService; // Atualizado para Gemini

    private static final String SYSTEM_PROMPT = """
            Você é um assistente de moderação de um servidor Discord.
            Com base no contexto fornecido, você deve decidir qual punição aplicar.
            
            Responda APENAS neste formato JSON (sem markdown, sem explicações):
            {
              "acao": "ban" | "kick" | "mute",
              "duracao_minutos": <número>,
              "motivo": "<motivo resumido>",
              "justificativa": "<explicação curta>"
            }
            
            Critérios:
            - ban: infrações graves (raid, pornografia, racismo).
            - kick: infrações moderadas ou comportamento inadequado repetido.
            - mute: infrações leves ou spam (use entre 10 e 60 minutos).
            """;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("punir")) return;

        // Verifica se quem usou o comando tem permissão
        if (!event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("❌ Eduardo, apenas moderadores podem usar este comando!").setEphemeral(true).queue();
            return;
        }

        Member alvo = event.getOption("usuario").getAsMember();
        String contexto = event.getOption("contexto").getAsString();

        if (alvo == null) {
            event.reply("❌ Não encontrei esse usuário no servidor.").setEphemeral(true).queue();
            return;
        }

        // Verifica se o bot tem um cargo maior que o alvo (Hierarquia do Discord)
        if (!event.getGuild().getSelfMember().canInteract(alvo)) {
            event.reply("❌ Não posso punir esse usuário. O cargo dele é maior ou igual ao meu!").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        String mensagem = "Usuário: " + alvo.getUser().getName() + "\nContexto: " + contexto;

        // Chama o Gemini
        String resposta = geminiService.perguntar(SYSTEM_PROMPT, mensagem);

        try {
            // Limpa a resposta caso a IA mande markdown ```json ... ```
            String jsonLimpo = resposta.replace("```json", "").replace("```", "").trim();

            String acao = extrairCampo(jsonLimpo, "acao");
            String motivo = extrairCampo(jsonLimpo, "motivo");
            String justificativa = extrairCampo(jsonLimpo, "justificativa");
            int duracao = Integer.parseInt(extrairCampo(jsonLimpo, "duracao_minutos").replaceAll("[^0-9]", "0"));

            // Aplica a punição de fato
            switch (acao.toLowerCase()) {
                case "ban" -> alvo.ban(0, TimeUnit.DAYS).reason("[IA] " + motivo).queue();
                case "kick" -> alvo.kick().reason("[IA] " + motivo).queue();
                case "mute" -> alvo.timeoutFor(Duration.ofMinutes(duracao > 0 ? duracao : 10)).reason("[IA] " + motivo).queue();
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("🤖 Julgamento da IA")
                    .setColor(new Color(255, 69, 0)) // Laranja avermelhado Neon
                    .addField("👤 Réu", alvo.getAsMention(), true)
                    .addField("⚖️ Veredito", acao.toUpperCase(), true)
                    .addField("⏱️ Tempo", duracao > 0 ? duracao + " min" : "—", true)
                    .addField("📋 Motivo", motivo, false)
                    .addField("🧠 Raciocínio", justificativa, false)
                    .setFooter("O Cérebro do Servidor decidiu.");

            event.getHook().sendMessageEmbeds(embed.build()).queue();

        } catch (Exception e) {
            // Se a IA não responder em JSON ou falhar, mostra o texto bruto
            event.getHook().sendMessage("⚠️ A IA não conseguiu processar a punição automaticamente, mas sugeriu:\n" + resposta).queue();
        }
    }

    // Método auxiliar para pegar valores do JSON sem precisar de bibliotecas pesadas
    private String extrairCampo(String json, String campo) {
        try {
            String chave = "\"" + campo + "\"";
            int inicio = json.indexOf(chave) + chave.length();
            int doisPontos = json.indexOf(":", inicio) + 1;
            int valorInicio = doisPontos;
            while (json.charAt(valorInicio) == ' ' || json.charAt(valorInicio) == '\n') valorInicio++;

            if (json.charAt(valorInicio) == '"') {
                int fim = json.indexOf("\"", valorInicio + 1);
                return json.substring(valorInicio + 1, fim);
            } else {
                int fim = json.indexOf(",", valorInicio);
                if (fim == -1) fim = json.indexOf("}", valorInicio);
                return json.substring(valorInicio, fim).trim();
            }
        } catch (Exception e) { return "0"; }
    }
}