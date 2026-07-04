package com.eduardo.AdminBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScrimCommand extends ListenerAdapter {

    @Value("${discord.canal-scrim-id}")
    private String canalScrimId;

    private final Map<String, Set<String>> inscritosPorMensagem = new ConcurrentHashMap<>();
    private static final int LIMITE = 10;

    private static final List<String> LANES = List.of(
            "<:Top_icon:1502709242010800199> Top",
            "<:Jungle_icon:1502709395958792242> Jungle",
            "<:Middle_icon:1502709163644424273> Mid",
            "<:Bottom_icon:1502709414627643413> ADC",
            "<:Support_icon:1502709433036312628> Support"
    );

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("scrim")) return;

        if (!event.getChannel().getId().equals(canalScrimId)) {
            event.reply("❌ Use este comando no canal de scrim!").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🏆 SCRIM — Inscrições Abertas!")
                .setDescription("Clique no botão abaixo para participar!\n\n**Inscritos: 0/" + LIMITE + "**")
                .setColor(Color.MAGENTA)
                .setFooter("Powered by AdminBot");

        event.replyEmbeds(embed.build())
                .addActionRow(Button.primary("scrim_participar", "✅ Participar"))
                .queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String messageId = event.getMessageId();

        if (event.getComponentId().equals("scrim_participar")) {
            inscritosPorMensagem.putIfAbsent(messageId, new LinkedHashSet<>());
            Set<String> inscritos = inscritosPorMensagem.get(messageId);
            String userId = event.getUser().getId();

            if (!inscritos.add(userId)) {
                event.reply("❌ Você já está inscrito!").setEphemeral(true).queue();
                return;
            }

            int count = inscritos.size();

            if (count < LIMITE) {
                StringBuilder lista = new StringBuilder();
                for (String id : inscritos) {
                    lista.append("<@").append(id).append(">\n");
                }

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("🏆 SCRIM — Inscrições Abertas!")
                        .setDescription("Clique no botão abaixo para participar!\n\n**Inscritos: " + count + "/" + LIMITE + "**\n\n" + lista)
                        .setColor(Color.MAGENTA)
                        .setFooter("Powered by AdminBot");

                event.editMessageEmbeds(embed.build()).queue();

            } else {
                gerarTimes(event, inscritos);
            }
        }
    }

    private void gerarTimes(ButtonInteractionEvent event, Set<String> inscritos) {
        List<String> jogadores = new ArrayList<>(inscritos);
        Collections.shuffle(jogadores);

        int metade = jogadores.size() / 2;
        List<String> time1 = jogadores.subList(0, metade);
        List<String> time2 = jogadores.subList(metade, jogadores.size());

        StringBuilder t1 = new StringBuilder();
        for (int i = 0; i < time1.size(); i++) {
            String lane = i < LANES.size() ? LANES.get(i) : "🎮 Flex";
            t1.append(lane).append(" → <@").append(time1.get(i)).append(">\n");
        }

        StringBuilder t2 = new StringBuilder();
        for (int i = 0; i < time2.size(); i++) {
            String lane = i < LANES.size() ? LANES.get(i) : "🎮 Flex";
            t2.append(lane).append(" → <@").append(time2.get(i)).append(">\n");
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("⚔️ TIMES FORMADOS!")
                .addField("🔵 Time 1", t1.toString(), true)
                .addField("🔴 Time 2", t2.toString(), true)
                .setColor(Color.CYAN)
                .setFooter("Boa sorte a todos! 🎮");

        event.editMessageEmbeds(embed.build())
                .setComponents()
                .queue();
    }
}