package com.eduardo.AdminBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashCommandHandler {
    String getCommandName();
    void handle(SlashCommandInteractionEvent event);
}