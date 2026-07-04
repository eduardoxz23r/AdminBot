package com.eduardo.AdminBot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class BanCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("ban")) return;

        Member member = event.getMember(); // 👈 declara aqui

        if (!PermissaoUtil.temPermissao(member)) {
            event.reply("❌ Apenas **Moderadores** e o **Dono** podem usar este comando.")
                    .setEphemeral(true).queue();
            return;
        }

        Member alvo = event.getOption("usuario").getAsMember();
        String motivo = event.getOption("motivo") != null
                ? event.getOption("motivo").getAsString()
                : "Motivo não especificado pela moderação.";

        if (alvo == null) {
            event.reply("❌ Usuário não encontrado no servidor.").setEphemeral(true).queue();
            return;
        }

        if (!event.getGuild().getSelfMember().canInteract(alvo)) {
            event.reply("❌ Erro de Hierarquia: O cargo desse usuário é maior ou igual ao meu!").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        alvo.ban(0, TimeUnit.DAYS)
                .reason(motivo)
                .queue(
                        success -> event.getHook().sendMessage(
                                "🔨 **USUÁRIO BANIDO**\n" +
                                        "👤 **Usuário:** " + alvo.getUser().getName() + "\n" +
                                        "📋 **Motivo:** " + motivo
                        ).queue(),
                        error -> event.getHook().sendMessage("❌ Falha técnica ao banir: " + error.getMessage()).queue()
                );
    }
}