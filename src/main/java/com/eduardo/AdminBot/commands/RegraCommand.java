package com.eduardo.AdminBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class RegraCommand extends ListenerAdapter {

    private static final String REGRAS_PADRAO = """
            ╔═════════════════════════════════════════╗
               **DIRETRIZES DO SERVIDOR**
            ╚═════════════════════════════════════════╝
            
            🔹 **1. Respeito Mútuo:** Trate todos com cordialidade.
            🔹 **2. Spam/Flood:** Proibido o envio repetitivo de mensagens.
            🔹 **3. Conteúdo NSFW:** Estritamente proibido em canais públicos.
            🔹 **4. Divulgação:** Não poste links de outros servidores sem permissão.
            🔹 **5. Discurso de Ódio:** Tolerância zero para preconceito.
            🔹 **6. Hierarquia:** Respeite as decisões da moderação.
            
            ⚠️ *O descumprimento das regras pode gerar banimento imediato via IA.*
            """;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("regra")) return;

        // Verifica se é administrador ou tem permissão de gerenciar o servidor
        if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            event.reply("❌ Eduardo, apenas administradores podem postar as regras oficiais!").setEphemeral(true).queue();
            return;
        }

        // Pega o conteúdo personalizado se o usuário digitar algo no comando
        String conteudo = event.getOption("conteudo") != null
                ? event.getOption("conteudo").getAsString()
                : REGRAS_PADRAO;

        event.deferReply().queue();

        // Estilo Roxo Neon para combinar com o setup
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📜 REGRAS E DIRETRIZES")
                .setDescription(conteudo)
                .setColor(new Color(148, 0, 211)) // DarkViolet
                .setThumbnail(event.getGuild().getIconUrl()) // Puxa a foto do servidor
                .setFooter("Atualizado por " + event.getUser().getName(), event.getUser().getEffectiveAvatarUrl());

        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }
}