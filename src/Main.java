import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    //HashMap which holds Team Names (countries) as keys and squad objects as values.
    //Using a Map will simplify adding correct Managers and Players to their respective squads,
    //By using the unique key (team name) to do so.
    public static Map<String, Squad> squadMap = new HashMap<>();
    public static Squad[] squads = new Squad[32];

    public static void main(String[] args){

        //Initialising the scanners
        Scanner scannerPlayers = null;
        Scanner scannerManagers = null;

        try {

            scannerPlayers = new Scanner(new File("C:/Users/ramez/Developer/adrian/adrian-football/Players.csv"));
            scannerManagers = new Scanner(new File("C:/Users/ramez/Developer/adrian/adrian-football/Managers.csv"));

            scannerPlayers.nextLine(); // Skips header in Players.csv
            scannerManagers.nextLine(); // Skips header in Managers.csv

            // Read and process Manager data
            while (scannerManagers.hasNextLine()) {
                String[] managerData = scannerManagers.nextLine().split(",");

                Manager manager = new Manager(managerData[0], managerData[1], managerData[2], managerData[3],
                        Double.parseDouble(managerData[4]), Double.parseDouble(managerData[5]),
                        Double.parseDouble(managerData[6]), Double.parseDouble(managerData[7]));


                // Determine squad based on team (country)
                Squad squad = squadMap.computeIfAbsent(manager.getTeam(), k -> new Squad(manager.getTeam(), manager));

            }

            // Read and process Player data
            while (scannerPlayers.hasNextLine()) {
                String[] playerData = scannerPlayers.nextLine().split(",");

                Player player = new Player(playerData[0], playerData[1], playerData[2], playerData[3],
                        Double.parseDouble(playerData[4]), Double.parseDouble(playerData[5]),
                        Double.parseDouble(playerData[6]), Double.parseDouble(playerData[7]),
                        Double.parseDouble(playerData[8]), Double.parseDouble(playerData[9]),
                        Double.parseDouble(playerData[10]), Double.parseDouble(playerData[11]),
                        Double.parseDouble(playerData[12]), Double.parseDouble(playerData[13]));

                Squad squad = squadMap.get(player.getTeam());
                if (squad != null) {
                    squad.addPlayer(player);
                }
            }

            squadMap.values().toArray(squads);

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } finally {

            if (scannerPlayers != null) {
                scannerPlayers.close();
            }
            if (scannerManagers != null) {
                scannerManagers.close();
            }
        }

        runTournament();

        System.out.println();
        System.out.println(getTeam(squads[9]));
    }

    public static Team getTeam(Squad s){

        String formation = s.getManager().getFavouredFormation();
        String[] positions = formation.split("-");

        List<Player> defenders = getBestPlayersByPosition(s.getPlayers(), " Defender", Integer.parseInt(positions[0]));
        List<Player> midfielders = getBestPlayersByPosition(s.getPlayers(), " Midfielder", Integer.parseInt(positions[1]));
        List<Player> forwards = getBestPlayersByPosition(s.getPlayers(), " Forward", Integer.parseInt(positions[2]));

        Player goalkeeper = s.getPlayers().stream()
                .filter(p -> p.getPosition().equalsIgnoreCase(" Goal Keeper"))
                .max(Comparator.comparingDouble(Player::getOverallSkill))
                .orElse(null);

        List<Player> teamPlayers = new ArrayList<>();
        teamPlayers.addAll(defenders);
        teamPlayers.addAll(midfielders);
        teamPlayers.addAll(forwards);
        if (goalkeeper != null) {
            teamPlayers.add(goalkeeper);
        }

        return new Team(s.getTeamName(), s.getManager(), teamPlayers);
    }

    public static List<Player> getBestPlayersByPosition(List<Player> players, String position, int number) {

        List<Player> filteredPlayers = new ArrayList<>();
        for (Player p : players) {
            if (p.getPosition().equalsIgnoreCase(position)) {
                filteredPlayers.add(p);
            }
        }


        filteredPlayers.sort(Comparator.comparingDouble(Player::getOverallSkill).reversed());


        List<Player> topPlayers = new ArrayList<>();
        int count = 0;
        for (Player p : filteredPlayers) {
            if (count < number) {
                topPlayers.add(p);
                count++;
            } else {
                break;
            }
        }

        return topPlayers;
    }



    public static void runTournament(){

        List<List<Team>> groups = createGroups();
        List<Team> knockoutTeams = runGroupStage(groups);
        Team champion = runKnockoutStage(knockoutTeams);

    }


    public static List<List<Team>> createGroups() {
        // Create groups
        List<List<Team>> groups = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            groups.add(new ArrayList<>());
        }

        // Distribute teams into groups
        int groupIndex = 0;
        for (Squad squad : squads) {
            Team team = getTeam(squad);
            groups.get(groupIndex).add(team);
            if ((groups.get(groupIndex).size() % 4) == 0) {
                groupIndex++; // Move to next group after every 4 teams
            }
        }

        // Print groups
        printGroups(groups);

        return groups;
    }

    private static void printGroups(List<List<Team>> groups) {
        int groupNumber = 1;
        for (List<Team> group : groups) {
            System.out.println("Group " + groupNumber + ":");
            for (Team team : group) {
                System.out.println(" - " + team.getTeamName());
            }
            groupNumber++;
            System.out.println();
        }
    }

    public static List<Team> runGroupStage(List<List<Team>> groups) {
        Map<Team, Integer> teamWins = new HashMap<>(); // Records number of wins for each team

        // Simulate matches within each group
        for (List<Team> group : groups) {
            System.out.println("\nGroup Stage Matches:");
            for (int i = 0; i < group.size(); i++) {
                for (int j = i + 1; j < group.size(); j++) {
                    Team team1 = group.get(i);
                    Team team2 = group.get(j);
                    Team winner = simulateMatch(team1, team2);
                    teamWins.put(winner, teamWins.getOrDefault(winner, 0) + 1);

                    // Log the match result
                    System.out.printf("%s (%.2f%%) vs %s (%.2f%%) - Winner: %s\n",
                            team1.getTeamName(), team1.teamOverallSkill() * 100,
                            team2.getTeamName(), team2.teamOverallSkill() * 100,
                            winner.getTeamName());
                }
            }
        }

        // Determine top two teams from each group
        List<Team> knockoutTeams = new ArrayList<>();
        for (List<Team> group : groups) {
            group.sort(Comparator.comparingInt(team -> teamWins.getOrDefault(team, 0)).reversed());
            System.out.println("\nTop Teams from Group:");
            knockoutTeams.add(group.get(0));
            knockoutTeams.add(group.get(1));
            System.out.println("1. " + group.get(0).getTeamName());
            System.out.println("2. " + group.get(1).getTeamName());
        }

        return knockoutTeams;
    }

    public static Team runKnockoutStage(List<Team> knockoutTeams) {
        System.out.println("\nKnockout Stage Matches:");

        while (knockoutTeams.size() > 1) {
            List<Team> nextRoundTeams = new ArrayList<>();

            // Simulate matches for the current round
            for (int i = 0; i < knockoutTeams.size(); i += 2) {
                Team team1 = knockoutTeams.get(i);
                Team team2 = knockoutTeams.get(i + 1);
                Team winner = simulateMatch(team1, team2);

                // Log the match result with team skills
                System.out.printf("Match: %s (%.2f%%) vs %s (%.2f%%) - Winner: %s\n",
                        team1.getTeamName(), team1.teamOverallSkill() * 100,
                        team2.getTeamName(), team2.teamOverallSkill() * 100,
                        winner.getTeamName());

                nextRoundTeams.add(winner);
            }

            // Prepare for the next round
            knockoutTeams = nextRoundTeams;
        }

        // The last team remaining is the champion
        Team champion = knockoutTeams.get(0);
        System.out.println("\nWorld Cup Champion: " + champion.getTeamName());
        return champion;
    }

    private static Team simulateMatch(Team team1, Team team2) {
        // Compare team overall skill to determine the winner
        double skillTeam1 = team1.teamOverallSkill();
        double skillTeam2 = team2.teamOverallSkill();
        return skillTeam1 > skillTeam2 ? team1 : team2;
    }


}