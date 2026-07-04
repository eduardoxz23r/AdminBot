package com.eduardo.AdminBot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClearCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("clear")) return;

        Member member = event.getMember(); // 👈 declara aqui

        if (!PermissaoUtil.temPermissao(member)) {
            event.reply("❌ Apenas **Moderadores** e o **Dono** podem usar este comando.")
                    .setEphemeral(true).queue();
            return;
        }

        int quantidade = event.getOption("quantidade").getAsInt();

        if (quantidade < 1 || quantidade > 100) {
            event.reply("❌ Digite um número entre 1 e 100.").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        event.getChannel().getIterableHistory()
                .takeAsync(quantidade)
                .thenAccept(messages -> {
                    List<Message> validas = messages.stream()
                            .filter(m -> m.getTimeCreated().isAfter(OffsetDateTime.now().minusDays(14)))
                            .collect(Collectors.toList());

                    if (validas.isEmpty()) {
                        event.getHook().sendMessage("❌ Nenhuma mensagem recente para apagar.").setEphemeral(true).queue();
                        return;
                    }

                    if (validas.size() == 1) {
                        validas.get(0).delete().queue(
                                s -> event.getHook().sendMessage("🗑️ **1** mensagem apagada!").setEphemeral(true).queue(),
                                e -> event.getHook().sendMessage("❌ Erro: " + e.getMessage()).setEphemeral(true).queue()
                        );
                    } else {
                        event.getChannel().asTextChannel().deleteMessages(validas).queue(
                                s -> event.getHook().sendMessage("🗑️ **" + validas.size() + "** mensagens apagadas!").setEphemeral(true).queue(),
                                e -> event.getHook().sendMessage("❌ Erro: " + e.getMessage()).setEphemeral(true).queue()
                        );
                    }
                });
    }
}