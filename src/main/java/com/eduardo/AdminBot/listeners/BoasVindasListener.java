package com.eduardo.AdminBot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.Color;

@Component
public class BoasVindasListener extends ListenerAdapter {

    @Value("${discord.canal-boas-vindas-id}")
    private String canalBoasVindasId;

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        var canal = event.getGuild().getTextChannelById(canalBoasVindasId);
        if (canal == null) return;

        String nome = event.getMember().getUser().getName();
        int membros = event.getGuild().getMemberCount();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Bem-vindo ao servidor!")
                .setDescription("Olá, **" + nome + "**! Seja bem-vindo!\nVocê é o membro **#" + membros + "**!")
                .setColor(Color.GREEN)
                .setThumbnail(event.getMember().getUser().getAvatarUrl())
                .setFooter("Leia as #regras para começar!");

        canal.sendMessageEmbeds(embed.build()).queue();
    }
}