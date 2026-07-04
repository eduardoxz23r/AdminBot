package com.eduardo.AdminBot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.awt.Color;

@Component
public class SetupCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("setup")) return;

        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ Apenas administradores podem usar este comando.").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();
        Guild guild = event.getGuild();

        // CARGOS — só cria se não existir
        criarCargoSeNaoExistir(guild, "👑 Dono", Color.RED, Permission.ADMINISTRATOR);
        criarCargoSeNaoExistir(guild, "🛡️ Admin", Color.ORANGE, Permission.ADMINISTRATOR);
        criarCargoSeNaoExistirMod(guild);
        criarCargoSeNaoExistir(guild, "✅ Membro", Color.GREEN);
        criarCargoSeNaoExistir(guild, "🆕 Novato", Color.GRAY);

        // CATEGORIAS — só cria se não existir
        criarCategoriaSeNaoExistir(guild, "📋 INFORMAÇÕES", new String[]{"regras", "anúncios", "boas-vindas"}, new String[]{});
        criarCategoriaSeNaoExistir(guild, "💬 GERAL", new String[]{"geral", "off-topic"}, new String[]{"🔊 Geral"});
        criarCategoriaSeNaoExistir(guild, "🤖 BOT", new String[]{"bot-ia", "comandos"}, new String[]{});
        criarCategoriaSeNaoExistir(guild, "🛠️ MODERAÇÃO", new String[]{"logs", "mod-chat"}, new String[]{});

        event.getHook().sendMessage("✅ Setup concluído! Itens já existentes foram mantidos.").queue();
    }

    private void criarCargoSeNaoExistir(Guild guild, String nome, Color cor, Permission... permissoes) {
        boolean existe = guild.getRoles().stream().anyMatch(r -> r.getName().equals(nome));
        if (existe) return;

        guild.createRole().setName(nome).setColor(cor).queue(role -> {
            if (permissoes.length > 0) {
                role.getManager().givePermissions(permissoes).queue();
            }
        });
    }

    private void criarCargoSeNaoExistirMod(Guild guild) {
        boolean existe = guild.getRoles().stream().anyMatch(r -> r.getName().equals("🔨 Moderador"));
        if (existe) return;

        guild.createRole().setName("🔨 Moderador").setColor(Color.BLUE).queue(role -> {
            role.getManager().givePermissions(
                    Permission.MESSAGE_MANAGE,
                    Permission.KICK_MEMBERS,
                    Permission.BAN_MEMBERS
            ).queue();
        });
    }

    private void criarCategoriaSeNaoExistir(Guild guild, String nomeCategoria, String[] canaisTexto, String[] canaisVoz) {
        boolean existe = guild.getCategories().stream().anyMatch(c -> c.getName().equals(nomeCategoria));
        if (existe) return;

        guild.createCategory(nomeCategoria).queue(cat -> {
            for (String canal : canaisTexto) {
                cat.createTextChannel(canal).queue();
            }
            for (String canal : canaisVoz) {
                cat.createVoiceChannel(canal).queue();
            }
        });
    }
}