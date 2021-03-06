package fr.gravendev.multibot.quiz.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.QuizMessageDAO;
import fr.gravendev.multibot.database.data.MessageData;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;

public class SetCommand implements CommandExecutor {

    private final QuizMessageDAO quizMessageDAO;
    private WelcomeMessagesSetManager welcomeMessagesSetManager;

    SetCommand(DAOManager daoManager, WelcomeMessagesSetManager welcomeMessagesSetManager) {
        quizMessageDAO = daoManager.getQuizMessageDAO();
        this.welcomeMessagesSetManager = welcomeMessagesSetManager;
    }

    @Override
    public String getCommand() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Permet de changer les questions du quizz et le message de bienvenue.";
    }

    @Override
    public void execute(Message message, String[] args) {

        if (args.length == 0) {
            message.getChannel().sendMessage("Erreur. "+getCharacter()+"quiz set question/message").queue();
            return;
        }

        if (args[0].equalsIgnoreCase("question")) {

            setQuestion(message, Arrays.copyOfRange(args, 1, args.length));

        } else if (args[0].equalsIgnoreCase("message")) {

            this.welcomeMessagesSetManager.onCommand(message);

        } else {
            message.getChannel().sendMessage("Erreur. "+getCharacter()+"quiz set question/message").queue();
        }

    }

    private void setQuestion(Message message, String[] args) {

        if (args.length < 1 || !args[0].matches("[0-9]+")) {
            message.getChannel().sendMessage("Erreur. "+getCharacter()+"quiz set question <numéro de la question> <texte ou vide>").queue();
            return;
        }

        MessageData messageData = this.quizMessageDAO.get(args[0]);

        if (messageData == null) {

            int i = 1;

            while (this.quizMessageDAO.get(String.valueOf(i)) != null) {
                ++i;
            }

            messageData = new MessageData(String.valueOf(i), "");

        }

        args = Arrays.copyOfRange(args, 1, args.length);
        String question = String.join(" ", args);

        messageData = new MessageData(messageData.getId(), question);

        if (!messageData.getMessage().equalsIgnoreCase("")) {
            quizMessageDAO.save(messageData);
            message.getChannel().sendMessage("La question numéro " + messageData.getId() + " a bien été changée en :\n" + messageData.getMessage()).queue();
        } else {
            quizMessageDAO.delete(messageData);
            message.getChannel().sendMessage("La question numéro " + messageData.getId() + " a bien été supprimée").queue();
        }

    }

}
