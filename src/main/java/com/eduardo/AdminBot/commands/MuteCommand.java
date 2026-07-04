package com.eduardo.AdminBot.commands;

import com.eduardo.AdminBot.commands.PermissaoUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class MuteCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("mute")) return;

        Member member = event.getMember(); // 👈 declara a variável primeiro

        if (!PermissaoUtil.temPermissao(member)) {
            event.reply("❌ Apenas **Moderadores** e o **Dono** podem usar este comando.")
                    .setEphemeral(true).queue();
            return;
        }

        Member alvo = event.getOption("usuario").getAsMember();

        long minutos = event.getOption("minutos") != null
                ? event.getOption("minutos").getAsLong()
                : 10L;

        String motivo = event.getOption("motivo") != null
                ? event.getOption("motivo").getAsString()
                : "Não informado pela moderação.";

        if (!event.getGuild().getSelfMember().canInteract(alvo)) {
            event.reply("❌ Não consigo silenciar este usuário devido à hierarquia de cargos.").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        alvo.timeoutFor(Duration.ofMinutes(minutos))
                .reason(motivo)
                .queue(
                        success -> event.getHook().sendMessage(
                                "🔇 **CASTIGO APLICADO**\n" +
                                        "👤 **Usuário:** " + alvo.getAsMention() + "\n" +
                                        "⏱️ **Duração:** " + minutos + " minutos\n" +
                                        "📋 **Motivo:** " + motivo
                        ).queue(),
                        error -> event.getHook().sendMessage("❌ Erro técnico ao aplicar timeout: " + error.getMessage()).queue()
                );
    }
}