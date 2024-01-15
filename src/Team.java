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
        sb.append("Manager: ").append(getManager().getFirstName()); // Assuming Manager class has getFirstName method
        sb.append("\nPlayers:\n");
        for (Player player : getBestTeamPlayers()) {
            sb.append(player.getFirstName()).append("\n"); // Assuming Player class has getFirstName method
        }
        return sb.toString();
    }
}
