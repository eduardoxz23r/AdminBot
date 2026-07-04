package com.eduardo.AdminBot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class CargoCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("cargo")) return;

        // Verifica permissão do Eduardo ou do moderador que usar
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("❌ Você não tem permissão para gerenciar cargos neste servidor.").setEphemeral(true).queue();
            return;
        }

        String acao = event.getOption("acao").getAsString().toLowerCase();
        Member alvo = event.getOption("usuario").getAsMember();
        Role cargo = event.getOption("cargo").getAsRole();

        if (alvo == null) {
            event.reply("❌ Usuário não encontrado no servidor.").setEphemeral(true).queue();
            return;
        }

        // Checagem de Hierarquia: O bot não pode dar cargos acima do dele
        if (!event.getGuild().getSelfMember().canInteract(cargo)) {
            event.reply("❌ Erro de Hierarquia: O cargo **" + cargo.getName() + "** é superior ao meu cargo de Bot!").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        if (acao.equals("dar")) {
            event.getGuild().addRoleToMember(alvo, cargo).queue(
                    success -> event.getHook().sendMessage(
                            "✅ Sucesso! O cargo **" + cargo.getName() + "** foi atribuído a **" + alvo.getUser().getName() + "**."
                    ).queue(),
                    error -> event.getHook().sendMessage("❌ Falha ao atribuir cargo: " + error.getMessage()).queue()
            );
        } else if (acao.equals("remover")) {
            event.getGuild().removeRoleFromMember(alvo, cargo).queue(
                    success -> event.getHook().sendMessage(
                            "✅ Sucesso! O cargo **" + cargo.getName() + "** foi removido de **" + alvo.getUser().getName() + "**."
                    ).queue(),
                    error -> event.getHook().sendMessage("❌ Falha ao remover cargo: " + error.getMessage()).queue()
            );
        } else {
            event.getHook().sendMessage("❌ Ação inválida! Utilize apenas `dar` ou `remover`.").queue();
        }
    }
}