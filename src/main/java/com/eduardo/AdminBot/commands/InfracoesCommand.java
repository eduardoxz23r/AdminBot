package com.eduardo.AdminBot.commands;

import com.eduardo.AdminBot.service.WarnStorage;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InfracoesCommand extends ListenerAdapter implements SlashCommandHandler {

    @Autowired
    private WarnStorage warnStorage;

    @Override
    public String getCommandName() {
        return "infracoes";
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("infracoes")) return;
        handle(event);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        // 1. Pega o usuário alvo
        User alvo = event.getOption("usuario").getAsUser();
        List<String> lista = warnStorage.listar(alvo.getId());

        // 2. Se não tiver advertências
        if (lista.isEmpty()) {
            event.getHook().sendMessage(
                    "✅ **" + alvo.getName() + "** não possui nenhuma advertência."
            ).queue();
            return;
        }

        // 3. Monta a lista de infrações
        StringBuilder sb = new StringBuilder();
        sb.append("📋 **Infrações de ").append(alvo.getName()).append("** (")
                .append(lista.size()).append(" advertência(s)):\n\n");

        for (int i = 0; i < lista.size(); i++) {
            sb.append("**#").append(i + 1).append("** — ").append(lista.get(i)).append("\n");
        }

        event.getHook().sendMessage(sb.toString()).queue();
    }
}