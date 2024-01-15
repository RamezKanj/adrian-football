import java.util.*;

public class Team extends Squad{

    private List<Player> teamPlayers;

    Team(String teamName, Manager manager, List<Player> teamPlayers) {
        super(teamName, manager);
        this.teamPlayers = teamPlayers;
    }


    public List<Player> getBestTeamPlayers() {
        return teamPlayers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Team: ").append(getTeamName()).append("\n");
        sb.append("Manager: ").append(getManager().getFirstName()).append("\n"); // Assuming Manager class has getFirstName method
        sb.append("Manager formation: ").append(getManager().getFavouredFormation()).append("\n");
        sb.append("Players:\n");
        for (Player player : getBestTeamPlayers()) {
            sb.append(player.getFirstName())
                    .append(" - ")
                    .append(player.getPosition())
                    .append(" - Skill: ")
                    .append(String.format("%.2f", player.getOverallSkill())) // Format skill level to 2 decimal places
                    .append("\n");

        }
        sb.append("Team Overall Skill: ").append(teamOverallSkill()).append("\n");
        return sb.toString();
    }

    public double teamOverallSkill() {
        double teamSkill = 0;
        List<Player> players = getBestTeamPlayers();
        if (players.isEmpty()) {
            return 0; // Avoid division by zero if there are no players
        }

        for (Player player : players) {
            teamSkill += player.getOverallSkill();
        }

        return teamSkill / players.size(); // Calculate average based on the actual number of players
    }

}
