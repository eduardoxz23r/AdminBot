package com.eduardo.AdminBot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class CanalCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("canal")) return;

        Member member = event.getMember(); // 👈 declara aqui

        if (!PermissaoUtil.temPermissao(member)) {
            event.reply("❌ Apenas **Moderadores** e o **Dono** podem usar este comando.")
                    .setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        String nome = event.getOption("canal").getAsString();

        String tipo = event.getOption("tipo") != null
                ? event.getOption("tipo").getAsString().toLowerCase()
                : "texto";

        String nomeCategoria = event.getOption("categoria") != null
                ? event.getOption("categoria").getAsString()
                : null;

        if (nomeCategoria != null) {
            Category encontrada = event.getGuild().getCategories().stream()
                    .filter(c -> c.getName().equalsIgnoreCase(nomeCategoria))
                    .findFirst()
                    .orElse(null);

            if (encontrada != null) {
                criarCanal(event, nome, tipo, encontrada, false);
            } else {
                event.getGuild().createCategory(nomeCategoria).queue(novaCat ->
                        criarCanal(event, nome, tipo, novaCat, true)
                );
            }
        } else {
            criarCanal(event, nome, tipo, null, false);
        }
    }

    private void criarCanal(SlashCommandInteractionEvent event, String nome, String tipo,
                            Category categoria, boolean categoriaCriada) {
        boolean isVoz = tipo.equals("voz");

        var action = isVoz
                ? (categoria != null ? categoria.createVoiceChannel(nome) : event.getGuild().createVoiceChannel(nome))
                : (categoria != null ? categoria.createTextChannel(nome) : event.getGuild().createTextChannel(nome));

        action.queue(c -> {
            StringBuilder msg = new StringBuilder();
            msg.append(isVoz ? "🔊" : "💬");
            msg.append(" Canal de ").append(isVoz ? "voz" : "texto");
            msg.append(" **").append(nome).append("** criado!");

            if (categoriaCriada) {
                msg.append("\n📁 Categoria **").append(categoria.getName()).append("** criada automaticamente!");
            } else if (categoria != null) {
                msg.append("\n📂 Categoria: **").append(categoria.getName()).append("**");
            }

            event.getHook().sendMessage(msg.toString()).queue();
        });
    }
}