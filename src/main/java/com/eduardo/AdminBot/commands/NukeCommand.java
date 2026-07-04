package com.eduardo.AdminBot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class NukeCommand extends ListenerAdapter implements SlashCommandHandler {

    @Override
    public String getCommandName() {
        return "nuke";
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("nuke")) return;
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

        String nomeCanal = channel.getName();
        String topicoCanal = channel.getTopic();
        int posicaoCanal = channel.getPosition();
        var categoriaCanal = channel.getParentCategory();
        var permissoes = channel.getPermissionOverrides();

        var createAction = categoriaCanal != null
                ? categoriaCanal.createTextChannel(nomeCanal)
                : event.getGuild().createTextChannel(nomeCanal);

        createAction
                .setPosition(posicaoCanal)
                .setTopic(topicoCanal)
                .queue(novoCanal -> {
                    for (var override : permissoes) {
                        if (override.isRoleOverride()) {
                            novoCanal.getManager()
                                    .putPermissionOverride(
                                            override.getRole(),
                                            override.getAllowed(),
                                            override.getDenied()
                                    ).queue();
                        } else if (override.isMemberOverride()) {
                            novoCanal.getManager()
                                    .putPermissionOverride(
                                            override.getMember(),
                                            override.getAllowed(),
                                            override.getDenied()
                                    ).queue();
                        }
                    }

                    novoCanal.sendMessage(
                            "💥 **Canal nukado por " + member.getAsMention() + "!**\n" +
                                    "🧹 Todas as mensagens foram apagadas."
                    ).queue();

                    channel.delete().queue();
                });
    }
}