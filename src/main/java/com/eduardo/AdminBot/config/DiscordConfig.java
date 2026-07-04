package com.eduardo.AdminBot.config;

import com.eduardo.AdminBot.commands.*;
import com.eduardo.AdminBot.listeners.BoasVindasListener;
import com.eduardo.AdminBot.listeners.ChatIAListener;
import com.eduardo.AdminBot.listeners.SaidaListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordConfig {
    
    @Value("${discord.token}")
    private String token;

    @Value("${discord.guild-id}")
    private String guildId;

    @Autowired private AjudaCommand ajudaCommand;
    @Autowired private WarnLimparCommand warnLimparCommand;
    @Autowired private WarnRemoverCommand warnRemoverCommand;
    @Autowired private WarnCommand warnCommand;
    @Autowired private InfracoesCommand infracoesCommand;
    @Autowired private NukeCommand nukeCommand;
    @Autowired private UnlockCommand unlockCommand;
    @Autowired private LockCommand lockCommand;
    @Autowired private ClearCommand clearCommand;
    @Autowired private ScrimCommand scrimCommand;
    @Autowired private SaidaListener saidaListener;
    @Autowired private BoasVindasListener boasVindasListener;
    @Autowired private SetupCommand setupCommand;
    @Autowired private BanCommand banCommand;
    @Autowired private KickCommand kickCommand;
    @Autowired private MuteCommand muteCommand;
    @Autowired private CargoCommand cargoCommand;
    @Autowired private CanalCommand canalCommand;
    @Autowired private RegraCommand regraCommand;
    @Autowired private PunirCommand punirCommand;
    @Autowired private ResumirCommand resumirCommand;
    @Autowired private ChatIAListener chatIAListener;

    @Bean
    public JDA jda() throws InterruptedException {
        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.DIRECT_MESSAGES
                )
                .addEventListeners(
                        ajudaCommand,
                        warnLimparCommand,
                        warnRemoverCommand,
                        warnCommand,
                        infracoesCommand,
                        nukeCommand,
                        unlockCommand,
                        lockCommand,
                        clearCommand,
                        scrimCommand,
                        saidaListener,
                        boasVindasListener,
                        setupCommand,
                        banCommand,
                        kickCommand,
                        muteCommand,
                        cargoCommand,
                        canalCommand,
                        regraCommand,
                        punirCommand,
                        resumirCommand,
                        chatIAListener
                )
                .build()
                .awaitReady();

        if (jda.getGuildById(guildId) != null) {
            jda.getGuildById(guildId).updateCommands().addCommands(

                    Commands.slash("ban", "🔨 Bane um usuário permanentemente")
                            .addOption(OptionType.USER, "usuario", "Usuário a ser banido", true)
                            .addOption(OptionType.STRING, "motivo", "O porquê do banimento", false),

                    Commands.slash("kick", "👢 Expulsa um usuário do servidor")
                            .addOption(OptionType.USER, "usuario", "Usuário a ser expulso", true)
                            .addOption(OptionType.STRING, "motivo", "O porquê da expulsão", false),

                    Commands.slash("mute", "🔇 Silencia um usuário (Timeout)")
                            .addOption(OptionType.USER, "usuario", "Usuário a ser silenciado", true)
                            .addOption(OptionType.INTEGER, "minutos", "Duração (padrão: 10 min)", false)
                            .addOption(OptionType.STRING, "motivo", "O porquê do silenciamento", false),

                    Commands.slash("cargo", "🏷️ Gerencia cargos (dar/remover)")
                            .addOption(OptionType.STRING, "acao", "Use 'dar' ou 'remover'", true)
                            .addOption(OptionType.USER, "usuario", "Alvo da ação", true)
                            .addOption(OptionType.ROLE, "cargo", "O cargo em questão", true),

                    Commands.slash("canal", "📂 Cria um novo canal rapidamente")
                            .addOption(OptionType.STRING, "canal", "Nome do novo canal", true)
                            .addOption(OptionType.STRING, "tipo", "texto ou voz", false)
                            .addOption(OptionType.STRING, "categoria", "Nome da categoria", false),

                    Commands.slash("regra", "📜 Exibe as regras neon do servidor")
                            .addOption(OptionType.STRING, "conteudo", "Texto personalizado das regras", false),

                    Commands.slash("punir", "⚖️ Julgamento da IA")
                            .addOption(OptionType.USER, "usuario", "Quem será julgado", true)
                            .addOption(OptionType.STRING, "contexto", "O que o meliante fez?", true),

                    Commands.slash("resumir", "📝 IA resume o que rolou no chat")
                            .addOption(OptionType.INTEGER, "quantidade", "Quantas mensagens ler (max: 50)", false),

                    Commands.slash("setup", "⚙️ Configura o servidor automaticamente"),


                    Commands.slash("scrim", "🏆 Abre inscrições para uma scrim 5v5"),


                    Commands.slash("lock", "Trava o canal atual, impedindo envio de mensagens"),


                    Commands.slash("unlock", "🔓 Destrava o canal atual, liberando envio de mensagens")
                            .addOption(OptionType.STRING, "motivo", "Motivo do destravamento", false),

                    Commands.slash("nuke", "💥 Apaga todas as mensagens do canal"),


                    Commands.slash("warn", "⚠️ Advertir um usuário")
                            .addOption(OptionType.USER, "usuario", "Usuário a ser advertido", true)
                            .addOption(OptionType.STRING, "motivo", "Motivo da advertência", false),

                    Commands.slash("infracoes", "📋 Lista as advertências de um usuário")
                            .addOption(OptionType.USER, "usuario", "Usuário a consultar", true),


                    Commands.slash("warn-limpar", "🧹 Remove todas as advertências de um usuário")
                            .addOption(OptionType.USER, "usuario", "Usuário a ter warns limpos", true),

                    Commands.slash("warn-remover", "✂️ Remove uma advertência específica")
                            .addOption(OptionType.USER, "usuario", "Usuário alvo", true)
                            .addOption(OptionType.INTEGER, "numero", "Número da advertência (use /infracoes para ver)", true),

                    Commands.slash("ajuda", "📋 Lista todos os comandos do bot"),

                    Commands.slash("clear", "🗑️ Apaga mensagens do canal")
                            .addOption(OptionType.INTEGER, "quantidade", "Quantidade de mensagens (max: 100)", true)

            ).queue();
            System.out.println(">> [AdminBot] Comandos Slash registrados com sucesso no servidor!");
        } else {
            System.out.println(">> [AdminBot] ERRO: Não encontrei o servidor. Verifique o Guild ID no application.properties.");
        }

        return jda;
    }
}