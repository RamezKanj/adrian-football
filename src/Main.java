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

        // Initialize scanners for reading player and manager data from CSV files.
        Scanner scannerPlayers = null;
        Scanner scannerManagers = null;

        try {

            // Setting up scanners to read CSV files for players and managers.
            scannerPlayers = new Scanner(new File("C:/Users/ramez/Developer/adrian/adrian-football/Players.csv"));
            scannerManagers = new Scanner(new File("C:/Users/ramez/Developer/adrian/adrian-football/Managers.csv"));

            scannerPlayers.nextLine(); // Skips header in Players.csv
            scannerManagers.nextLine(); // Skips header in Managers.csv

            // Read and process manager data from the CSV file
            while (scannerManagers.hasNextLine()) {
                String[] managerData = scannerManagers.nextLine().split(",");

                Manager manager = new Manager(managerData[0], managerData[1], managerData[2], managerData[3],
                        Double.parseDouble(managerData[4]), Double.parseDouble(managerData[5]),
                        Double.parseDouble(managerData[6]), Double.parseDouble(managerData[7]));


                // Assign managers to their respective squads based on team (country).
                Squad squad = squadMap.computeIfAbsent(manager.getTeam(), k -> new Squad(manager.getTeam(), manager));

            }

            // Read and process player data from the CSV file.
            while (scannerPlayers.hasNextLine()) {
                String[] playerData = scannerPlayers.nextLine().split(",");

                Player player = new Player(playerData[0], playerData[1], playerData[2], playerData[3],
                        Double.parseDouble(playerData[4]), Double.parseDouble(playerData[5]),
                        Double.parseDouble(playerData[6]), Double.parseDouble(playerData[7]),
                        Double.parseDouble(playerData[8]), Double.parseDouble(playerData[9]),
                        Double.parseDouble(playerData[10]), Double.parseDouble(playerData[11]),
                        Double.parseDouble(playerData[12]), Double.parseDouble(playerData[13]));

                // Add players to their respective squads.
                Squad squad = squadMap.get(player.getTeam());
                if (squad != null) {
                    squad.addPlayer(player);
                }
            }

            // Convert the squadMap values to an array for easier processing.
            squadMap.values().toArray(squads);

        } catch (FileNotFoundException e) {
            e.printStackTrace(); // Handle file not found exception.

        } finally {

            // Close scanners to prevent resource leaks.
            if (scannerPlayers != null) {
                scannerPlayers.close();
            }
            if (scannerManagers != null) {
                scannerManagers.close();
            }
        }

        // Start the tournament.
        runTournament();

        // Print details of a specific team (for testing or demonstration).
        System.out.println();
        System.out.println(getTeam(squads[9]));
    }

    // Method to generate a Team object from a Squad.
    public static Team getTeam(Squad s){

        // Extract manager's preferred formation and split it into parts.
        String formation = s.getManager().getFavouredFormation();
        String[] positions = formation.split("-");

        // Get the best players for each position based on the formation.
        List<Player> defenders = getBestPlayersByPosition(s.getPlayers(), " Defender", Integer.parseInt(positions[0]));
        List<Player> midfielders = getBestPlayersByPosition(s.getPlayers(), " Midfielder", Integer.parseInt(positions[1]));
        List<Player> forwards = getBestPlayersByPosition(s.getPlayers(), " Forward", Integer.parseInt(positions[2]));

        // Find the best goalkeeper.
        Player goalkeeper = s.getPlayers().stream()
                .filter(p -> p.getPosition().equalsIgnoreCase(" Goal Keeper"))
                .max(Comparator.comparingDouble(Player::getOverallSkill))
                .orElse(null);

        // Combine all selected players to form the team.
        List<Player> teamPlayers = new ArrayList<>();
        teamPlayers.addAll(defenders);
        teamPlayers.addAll(midfielders);
        teamPlayers.addAll(forwards);
        if (goalkeeper != null) {
            teamPlayers.add(goalkeeper);
        }

        // Return the newly formed team.
        return new Team(s.getTeamName(), s.getManager(), teamPlayers);
    }


    // Method to select the best players for a specific position.
    public static List<Player> getBestPlayersByPosition(List<Player> players, String position, int number) {

        List<Player> filteredPlayers = new ArrayList<>();
        for (Player p : players) {
            if (p.getPosition().equalsIgnoreCase(position)) {
                filteredPlayers.add(p);
            }
        }


        // Sort players by overall skill and select the top players as per the required number.
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



    // Method to run the entire tournament.
    public static void runTournament(){

        // Create groups for the group stage.
        List<List<Team>> groups = createGroups();

        // Run the group stage and get the teams qualifying for the knockout stage.
        List<Team> knockoutTeams = runGroupStage(groups);

        // Run the knockout stage and determine the champion.
        Team champion = runKnockoutStage(knockoutTeams);

    }


    // Method to create groups for the tournament.
    public static List<List<Team>> createGroups() {
        // Initialize groups.
        List<List<Team>> groups = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            groups.add(new ArrayList<>());
        }

        // Distribute teams into groups.
        int groupIndex = 0;
        for (Squad squad : squads) {
            Team team = getTeam(squad);
            groups.get(groupIndex).add(team);
            if ((groups.get(groupIndex).size() % 4) == 0) {
                groupIndex++; // Move to the next group after filling one.
            }
        }

        // Print the groups for visual representation.
        printGroups(groups);

        return groups;
    }

    // Helper method to print the groups.
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

    // Method to run the group stage of the tournament
    public static List<Team> runGroupStage(List<List<Team>> groups) {

        // Map to keep track of the number of wins for each team
        Map<Team, Integer> teamWins = new HashMap<>();

        // Run round-robin within each group where each team plays against every other team
        for (List<Team> group : groups) {
            System.out.println("\nGroup Stage Matches:");
            for (int i = 0; i < group.size(); i++) {
                for (int j = i + 1; j < group.size(); j++) {
                    Team team1 = group.get(i);
                    Team team2 = group.get(j);
                    // Simulate a match between team1 and team2
                    Team winner = simulateMatch(team1, team2);
                    // Update the winner's win count
                    teamWins.put(winner, teamWins.getOrDefault(winner, 0) + 1);

                    // Log the result of the match with the teams' overall skills
                    System.out.printf("%s (%.2f%%) vs %s (%.2f%%) - Winner: %s\n",
                            team1.getTeamName(), team1.teamOverallSkill() * 100,
                            team2.getTeamName(), team2.teamOverallSkill() * 100,
                            winner.getTeamName());
                }
            }
        }

        // List to store the teams that advance to the knockout stage
        List<Team> knockoutTeams = new ArrayList<>();
        // Determine the top two teams from each group based on the number of wins
        for (List<Team> group : groups) {
            // Sort teams within each group based on their win count
            group.sort(Comparator.comparingInt(team -> teamWins.getOrDefault(team, 0)).reversed());
            System.out.println("\nTop Teams from Group:");
            // Add the top two teams from each group to the knockout stage
            knockoutTeams.add(group.get(0));
            knockoutTeams.add(group.get(1));
            // Log the top two teams
            System.out.println("1. " + group.get(0).getTeamName());
            System.out.println("2. " + group.get(1).getTeamName());
        }

        return knockoutTeams;
    }

    // Method to run the knockout stage of the tournament
    public static Team runKnockoutStage(List<Team> knockoutTeams) {
        System.out.println("\nKnockout Stage Matches:");

        // Continue until there is only one team left (the champion)
        while (knockoutTeams.size() > 1) {
            // List to store the winners of the current round
            List<Team> nextRoundTeams = new ArrayList<>();

            // Simulate matches for the current round
            for (int i = 0; i < knockoutTeams.size(); i += 2) {
                Team team1 = knockoutTeams.get(i);
                Team team2 = knockoutTeams.get(i + 1);
                Team winner = simulateMatch(team1, team2);

                // Log the result of the match with each team's overall skill percentage
                System.out.printf("Match: %s (%.2f%%) vs %s (%.2f%%) - Winner: %s\n",
                        team1.getTeamName(), team1.teamOverallSkill() * 100,
                        team2.getTeamName(), team2.teamOverallSkill() * 100,
                        winner.getTeamName());

                // Add the winner to the next round
                nextRoundTeams.add(winner);
            }

            // Update the list of teams for the next round
            knockoutTeams = nextRoundTeams;
        }

        // The last remaining team is the champion
        Team champion = knockoutTeams.get(0);
        System.out.println("\nWorld Cup Champion: " + champion.getTeamName());
        // Return the champion
        return champion;
    }

    // Helper method to simulate a match between two teams
    private static Team simulateMatch(Team team1, Team team2) {
        // Calculate each team's overall skill
        double skillTeam1 = team1.teamOverallSkill();
        double skillTeam2 = team2.teamOverallSkill();
        // The team with the higher skill wins the match
        return skillTeam1 > skillTeam2 ? team1 : team2;
    }


}