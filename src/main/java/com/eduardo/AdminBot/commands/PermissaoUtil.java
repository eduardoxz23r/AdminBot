package com.eduardo.AdminBot.commands;

import net.dv8tion.jda.api.entities.Member;

import java.util.Set;


public class PermissaoUtil {

    private static final Set<String> CARGOS_PERMITIDOS = Set.of(
            "👑 Dono",
            "🔨 Moderador"
    );

    public static boolean temPermissao(Member member) {
        if (member == null) return false;

        if (member.isOwner()) return true;

        return member.getRoles().stream()
                .anyMatch(role -> CARGOS_PERMITIDOS.contains(role.getName()));
    }
}