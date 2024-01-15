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
                // Add logic to determine which Squad object this manager belongs to
                // For example:
                // squads[index].setManager(manager);

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

        System.out.println(getTeam(squads[0]));
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

    }
}