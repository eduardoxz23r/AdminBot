package com.eduardo.AdminBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.awt.Color;

@Component
public class AjudaCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("ajuda")) return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("📋 Comandos do AdminBot");
        embed.setColor(Color.CYAN);
        embed.setFooter("AdminBot • Developed by Eduardo");

        embed.addField("🔨 Moderação", """
                `/ban` — Bane um usuário
                `/kick` — Expulsa um usuário
                `/mute` — Silencia um usuário
                `/warn` — Adverte um usuário
                `/warn-remover` — Remove uma advertência
                `/warn-limpar` — Zera todas as advertências
                `/infracoes` — Lista advertências de um usuário
                `/punir` — Julgamento da IA
                """, false);

        embed.addField("🔒 Canais", """
                `/lock` — Trava o canal atual
                `/unlock` — Destrava o canal atual
                `/nuke` — Apaga tudo do canal
                `/canal` — Cria um novo canal
                `/clear` — Apaga mensagens
                """, false);

        embed.addField("⚙️ Servidor", """
                `/setup` — Configura o servidor automaticamente
                `/cargo` — Dar/remover cargo de um usuário
                `/regra` — Exibe as regras do servidor
                `/scrim` — Abre inscrições para uma scrim 5v5
                """, false);

        embed.addField("🤖 IA", """
                `/resumir` — IA resume o chat
                `/punir` — IA julga um usuário
                `#bot-ia` — Converse com a IA diretamente
                """, false);

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
}