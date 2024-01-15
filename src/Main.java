import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static Squad[] squads = new Squad[32];

    public static void main(String[] args){

        //Initialising the scanners
        Scanner scannerPlayers = null;
        Scanner scannerManagers = null;

        try {

            scannerPlayers = new Scanner(new File("../Players.csv"));
            scannerManagers = new Scanner(new File("../Managers.csv"));

            scannerPlayers.nextLine(); // Skips header in Players.csv
            scannerManagers.nextLine(); // Skips header in Managers.csv
            

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

    }

    public static Team getTeam(Squad s){
        Team t = new Team(s.getTeamName(), s.getManager());

        return t;
    }

    public static void runTournament(){

    }
}