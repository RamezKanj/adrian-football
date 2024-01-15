import java.util.ArrayList;

public class Squad {
    private String teamName;
    private ArrayList<Player> players;
    private Manager manager;

    Squad(String teamName, Manager manager){
        this.teamName = teamName;
        this.manager = manager;
        players = new ArrayList<>();
    }

    public void addPlayer(Player p){
        players.add(p);
    }

    //get a player object by surname
    public Player getPlayer(String surname){
        for(Player p: players){
            if(p.getSurname().equals(surname)){
                return p;
            }
        }
        return null;
    }

    //get a player object by index
    public Player getPlayer(int n){
        return players.get(n);
    }

    public String getTeamName() {
        return teamName;
    }

    public Manager getManager() {
        return manager;
    }
}
