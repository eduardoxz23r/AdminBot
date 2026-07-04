package com.eduardo.AdminBot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.Color;

@Component
public class SaidaListener extends ListenerAdapter {

    @Value("${discord.canal-boas-vindas-id}")
    private String canalBoasVindasId;

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        var canal = event.getGuild().getTextChannelById(canalBoasVindasId);
        if (canal == null) return;

        String nome = event.getUser().getName();
        int membros = event.getGuild().getMemberCount();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("👋 Até logo!")
                .setDescription("**" + nome + "** saiu do servidor.\nAgora somos **" + membros + "** membros.")
                .setColor(Color.RED)
                .setThumbnail(event.getUser().getAvatarUrl())
                .setFooter("Esperamos te ver de volta!");

        canal.sendMessageEmbeds(embed.build()).queue();
    }
}