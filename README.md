# 🚀AdminBot

Bot de moderação para Discord com sistema de advertências e chat com IA integrado, desenvolvido em Java com Spring Boot e JDA. Está em produção 24/7, hospedado em uma VM na Oracle Cloud.

## Funcionalidades

- **Moderação**: `/ban`, `/kick`, `/mute`, `/lock`, `/unlock`, `/clear`, `/cargo`
- **Sistema de advertências**: `/warn` com escalada automática (3 advertências = timeout de 1h, 5 = kick automático), além de `/warn-remover`, `/warn-limpar` e `/infracoes`
- **Automação de servidor**: mensagens automáticas de boas-vindas e despedida, `/setup`, `/canal`, `/regra`, `/scrim`
- **Chat com IA**: canal `#ia` com respostas via API da Groq, e `/resumir` para resumir o histórico da conversa
- **Health check**: endpoint `/health` usado por um serviço externo de monitoramento

## Arquitetura

```
com.eduardo.AdminBot
├── commands/     → um comando slash por classe (interface SlashCommandHandler)
├── config/       → configuração do Spring Boot e do bot (JDA)
├── controller/   → endpoint de health check
├── listeners/    → eventos de entrada/saída de membros e chat com IA
└── service/      → integração com a API da Groq e persistência das advertências
```

Cada comando é implementado como uma classe própria, ao invés de um bloco único de `if/else`, o que facilita adicionar novos comandos sem alterar os existentes. As advertências são persistidas em um arquivo JSON, suficiente para o volume de dados do projeto.

## Tecnologias

- Java 21
- Spring Boot 3.3.0
- [JDA](https://github.com/discord-jda/JDA) 5.2.1
- API da [Groq](https://groq.com) (modelo `openai/gpt-oss-120b`)
- Maven
- Oracle Cloud Infrastructure (deploy)

## Como rodar localmente

```bash
git clone https://github.com/eduardoxz23r/AdminBot.git
cd AdminBot
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Preencha o `application.properties` com suas credenciais:

```properties
discord.token=SEU_TOKEN_AQUI
discord.guild-id=SEU_GUILD_ID_AQUI
discord.canal-ia-id=SEU_CANAL_IA_AQUI
discord.canal-boas-vindas-id=SEU_CANAL_BOASVINDAS_AQUI
discord.canal-scrim-id=SEU_CANAL_SCRIMS_AQUI
groq.api-key=SUA_CHAVE_AQUI
```

Depois, rode:

```bash
./mvnw spring-boot:run
```

> `application.properties` está no `.gitignore` e nunca deve ser commitado — ele contém credenciais reais.

## Deploy

Rodando em uma instância Ubuntu na Oracle Cloud (Always Free Tier), gerenciada via `systemd` para reiniciar automaticamente em caso de falha. A JVM roda com heap limitado (`-Xmx400m`) devido aos recursos reduzidos da instância gratuita, com swap configurado como reforço.

---

Desenvolvido por Carlos Eduardo.
