package com.eduardo.AdminBot.commands;

import com.eduardo.AdminBot.service.WarnStorage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WarnRemoverCommand extends ListenerAdapter implements SlashCommandHandler {

    @Autowired
    private WarnStorage warnStorage;

    @Override
    public String getCommandName() {
        return "warn-remover";
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("warn-remover")) return;
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
        int numero = event.getOption("numero").getAsInt();

        boolean removido = warnStorage.remover(alvo.getId(), numero - 1);

        if (!removido) {
            int total = warnStorage.contar(alvo.getId());
            event.getHook().sendMessage(
                    "❌ Advertência **#" + numero + "** não encontrada. **" + alvo.getName() + "** tem **" + total + "** advertência(s)."
            ).queue();
            return;
        }

        event.getHook().sendMessage(
                "✅ Advertência **#" + numero + "** de **" + alvo.getName() + "** removida por " + member.getAsMention() + ".\n" +
                        "🔢 Total restante: **" + warnStorage.contar(alvo.getId()) + "**"
        ).setEphemeral(false).queue();
    }
}