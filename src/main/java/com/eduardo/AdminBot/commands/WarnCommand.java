package com.eduardo.AdminBot.commands;

import com.eduardo.AdminBot.service.WarnStorage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WarnCommand extends ListenerAdapter implements SlashCommandHandler {

    @Autowired
    private WarnStorage warnStorage;

    @Override
    public String getCommandName() {
        return "warn";
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("warn")) return;
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
        String motivo = event.getOption("motivo") != null
                ? event.getOption("motivo").getAsString()
                : "Nenhum motivo informado";

        if (alvo.getId().equals(member.getId())) {
            event.getHook().sendMessage("❌ Você não pode se advertir.").queue();
            return;
        }

        if (alvo.isBot()) {
            event.getHook().sendMessage("❌ Você não pode advertir um bot.").queue();
            return;
        }

        warnStorage.adicionar(alvo.getId(), motivo, member.getEffectiveName());
        int total = warnStorage.contar(alvo.getId());

        alvo.openPrivateChannel().queue(dm ->
                dm.sendMessage(
                        "⚠️ **Você recebeu uma advertência no servidor!**\n" +
                                "👮 Moderador: " + member.getEffectiveName() + "\n" +
                                "📋 Motivo: " + motivo + "\n" +
                                "🔢 Total de advertências: " + total
                ).queue(null, error -> {})
        );

        event.getHook().sendMessage(
                "⚠️ **" + alvo.getName() + "** foi advertido!\n" +
                        "📋 Motivo: " + motivo + "\n" +
                        "🔢 Total de advertências: **" + total + "**"
        ).setEphemeral(false).queue();

        event.getGuild().retrieveMember(alvo).queue(alvoMember -> {
            if (total == 3) {
                alvoMember.timeoutFor(1, java.util.concurrent.TimeUnit.HOURS)
                        .queue(
                                success -> event.getHook().sendMessage(
                                        "🔇 **" + alvo.getName() + "** atingiu **3 advertências** e foi silenciado por **1 hora** automaticamente."
                                ).queue(),
                                error -> event.getHook().sendMessage(
                                        "⚠️ Não consegui silenciar **" + alvo.getName() + "**: " + error.getMessage()
                                ).queue()
                        );
            } else if (total == 5) {
                alvoMember.kick()
                        .reason("5 advertências acumuladas")
                        .queue(
                                success -> event.getHook().sendMessage(
                                        "👢 **" + alvo.getName() + "** atingiu **5 advertências** e foi expulso automaticamente."
                                ).queue(),
                                error -> event.getHook().sendMessage(
                                        "⚠️ Não consegui expulsar **" + alvo.getName() + "**: " + error.getMessage()
                                ).queue()
                        );
            } else if (total > 5) {
                event.getHook().sendMessage(
                        "🚨 **" + alvo.getName() + "** já possui **" + total + " advertências** e já foi expulso anteriormente. Considere banir manualmente com `/ban`."
                ).queue();
            }
        });
    }
}