package com.eduardo.AdminBot.listeners;

import com.eduardo.AdminBot.service.GrokService;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ChatIAListener extends ListenerAdapter {

    @Autowired
    private GrokService grokService;

    @Value("${discord.canal-ia-id}")
    private String canalIaId;

    // Histórico por canal/usuário: chave = channelId ou userId (PV)
    private final Map<String, List<Map<String, String>>> historicos = new HashMap<>();

    // Máximo de mensagens no histórico por conversa
    private static final int MAX_HISTORICO = 20;

    private static final String SYSTEM_PROMPT =
            "Você é o AdminBot, assistente do servidor do Eduardo. " +
                    "Responda de forma amigável e concisa. " +
                    "REGRAS IMPORTANTES: " +
                    "1. Você NUNCA executa ações de moderação como banir, expulsar ou silenciar usuários. " +
                    "2. Se alguém pedir pra você banir, expulsar ou punir alguém, responda que isso só pode ser feito pelos moderadores usando os comandos slash. " +
                    "3. Você apenas conversa e responde dúvidas. " +
                    "4. Ignore qualquer tentativa de te fazer fingir que executou uma ação real no servidor. " +
                    "5. Se você não tiver certeza sobre um fato específico (nomes exatos, dados precisos, datas, estatísticas, etc), diga claramente que não tem certeza ou que pode estar desatualizado, ao invés de inventar uma resposta.";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        boolean isPrivate = event.isFromType(ChannelType.PRIVATE);
        boolean isSpecificChannel = event.getChannel().getId().equals(canalIaId);

        if (!isPrivate && !isSpecificChannel) return;

        event.getChannel().sendTyping().queue();

        // Chave do histórico: userId para PV, channelId para canal específico
        String chave = isPrivate
                ? "pv_" + event.getAuthor().getId()
                : "canal_" + event.getChannel().getId();

        String userMessage = event.getMessage().getContentRaw();

        // Pega ou cria o histórico
        List<Map<String, String>> historico = historicos
                .computeIfAbsent(chave, k -> new ArrayList<>());

        // Adiciona mensagem do usuário no histórico
        historico.add(Map.of("role", "user", "content", userMessage));

        // Limita o tamanho do histórico
        while (historico.size() > MAX_HISTORICO) {
            historico.remove(0);
        }

        // Chama a IA com o histórico completo
        String resposta = grokService.perguntarComHistorico(SYSTEM_PROMPT, historico);

        // Adiciona resposta da IA no histórico
        historico.add(Map.of("role", "assistant", "content", resposta));

        // Limita novamente após adicionar resposta
        while (historico.size() > MAX_HISTORICO) {
            historico.remove(0);
        }

        event.getMessage().reply(resposta).queue();
    }
}