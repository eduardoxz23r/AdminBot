package com.eduardo.AdminBot.commands;

import com.eduardo.AdminBot.service.WarnStorage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WarnLimparCommand extends ListenerAdapter implements SlashCommandHandler {

    @Autowired
    private WarnStorage warnStorage;

    @Override
    public String getCommandName() {
        return "warn-limpar";
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("warn-limpar")) return;
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

        User alvo = event.getOption("usuario").getAsUser();

        int total = warnStorage.contar(alvo.getId());
        if (total == 0) {
            event.getHook().sendMessage("✅ **" + alvo.getName() + "** não possui nenhuma advertência.").queue();
            return;
        }

        warnStorage.limpar(alvo.getId());

        event.getHook().sendMessage(
                "🧹 Todas as **" + total + " advertência(s)** de **" + alvo.getName() + "** foram removidas por " + member.getAsMention() + "."
        ).setEphemeral(false).queue();
    }
}