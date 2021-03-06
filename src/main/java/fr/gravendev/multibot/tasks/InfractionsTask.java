package fr.gravendev.multibot.tasks;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.sql.SQLException;
import java.util.List;
import java.util.TimerTask;

public class InfractionsTask extends TimerTask {

    private final Guild guild;
    private final InfractionDAO infractionDAO;

    public InfractionsTask(JDA jda, DAOManager daoManager) {
        this.infractionDAO = daoManager.getInfractionDAO();
        this.guild = jda.getGuildById(Configuration.GUILD.getValue());
    }

    @Override
    public void run() {
        try {
            List<InfractionData> allUnfinished = infractionDAO.getALLUnfinished();
            allUnfinished.forEach(infraction -> {
                switch (infraction.getType()) {
                    case BAN:
                        guild.unban(infraction.getPunishedId()).queue(success -> {}, throwable -> {});
                        Member member = guild.getMemberById(infraction.getPunishedId());
                        if (member == null) {
                            break;
                        }
                        member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Vous avez été débanni du discord GravenDev").queue(), throwable -> {});
                        break;
                    case MUTE:
                        member = guild.getMemberById(infraction.getPunishedId());
                        if (member == null) {
                            break;
                        }
                        GuildUtils.removeRole(member, Configuration.MUTED.getValue()).queue();
                        member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Vous avez été unmute du discord GravenDev").queue(), throwable -> {});
                        break;
                }
                infraction.setFinished(true);
                infractionDAO.save(infraction);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
