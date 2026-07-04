package com.eduardo.AdminBot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UnlockCommand extends ListenerAdapter implements SlashCommandHandler {

    @Override
    public String getCommandName() {
        return "unlock";
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("unlock")) return;
        handle(event);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        Member member = event.getMember();

        if (event.getGuild() == null) {
            event.getHook().sendMessage("❌ Este comando só pode ser usado em um servidor.").queue();
            return;
        }

        if (!PermissaoUtil.temPermissao(member)) {
            event.getHook().sendMessage("❌ Apenas **Moderadores** e o **Dono** podem usar este comando.").queue();
            return;
        }

        if (!(event.getChannel() instanceof TextChannel channel)) {
            event.getHook().sendMessage("❌ Este comando só funciona em canais de texto.").queue();
            return;
        }

        Member selfMember = event.getGuild().getSelfMember();
        if (!selfMember.hasPermission(channel, Permission.MANAGE_CHANNEL)) {
            event.getHook().sendMessage("❌ Não tenho permissão para gerenciar este canal.").queue();
            return;
        }

        String motivo = event.getOption("motivo") != null
                ? event.getOption("motivo").getAsString()
                : "Nenhum motivo informado";

        Role everyoneRole = event.getGuild().getPublicRole();

        var permOverride = channel.getPermissionOverride(everyoneRole);
        if (permOverride == null || !permOverride.getDenied().contains(Permission.MESSAGE_SEND)) {
            event.getHook().sendMessage("⚠️ Este canal já está destravado.").queue();
            return;
        }

        channel.getManager()
                .putPermissionOverride(
                        everyoneRole,
                        List.of(Permission.MESSAGE_SEND),
                        Collections.emptyList()
                )
                .queue(
                        success -> {
                            channel.sendMessage(
                                    "🔓 **Canal destravado!**\n" +
                                            "👮 Moderador: " + member.getAsMention() + "\n" +
                                            "📋 Motivo: " + motivo
                            ).queue();
                            event.getHook().sendMessage("✅ Canal **#" + channel.getName() + "** destravado com sucesso!").queue();
                        },
                        error -> event.getHook().sendMessage("❌ Erro ao destravar: " + error.getMessage()).queue()
                );
    }
}