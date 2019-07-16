package fr.gravendev.multibot.events.listeners.emoteaddedexecutors;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.events.listeners.EmoteAddedExecutor;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.sql.SQLException;

public class CandidsExecutor implements EmoteAddedExecutor {

    private final DatabaseConnection databaseConnection;

    public CandidsExecutor(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getSaloon() {
        return "candids";
    }

    @Override
    public void execute(MessageReactionAddEvent event) {

        try {

            GuildIdDAO guildIdDAO = new GuildIdDAO(this.databaseConnection.getConnection());
            long memberRoleId = guildIdDAO.get("member").id;

            event.getChannel().getMessageById(event.getMessageIdLong()).queue(message -> {

                if (message.getMentionedMembers().size() != 1) return;

                String validationMessage;
                Member member = message.getMentionedMembers().get(0);

                if (event.getReactionEmote().getName().equals("\u2705")) {
                    validationMessage = "accepté ";
                    Guild guild = message.getGuild();
                    guild.getController().addRolesToMember(member, guild.getRoleById(memberRoleId)).queue();
                } else {
                    validationMessage = "refusé ";
                }

                message.editMessage(new MessageBuilder(message)
                        .setContent(member.getAsMention() + "\n\n" + validationMessage + "par " + event.getMember().getAsMention())
                        .build()).queue();

            });

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
