import java.util.*;
import java.io.*;
import java.lang.*;

//class to represent an edge in the graph from source city to destination
//implements compareTo() for PriorityQueue<Flight> use
class Flight {
  public City city; //destination of flight
  public double cost; //cost of flight

  public Flight(City destination, double price){
    city = destination;
    cost = price;
  }
}

//class to represent a vertex in the graph
//implements compareTo() for PriorityQueue<City> use
class City implements Comparable<City>{

  public String name; // city name
  public double minCost = Double.POSITIVE_INFINITY; //initialize to infinity for comparisons
  public List<Flight> flights; //array of flights departing from city
  public City previousCity; //city whose cheapest flight connects to City

  public City(String line){
    name = line;
    flights = new ArrayList<Flight>();
  }

  //initialize flight with destination city and cost;
  //append flight to flights[], update flight count
  public void addFlight(City destination, double cost) {
    flights.add(new Flight(destination, cost));
  }

  //loop through flights of city; compares destination input to city of flight;
  //if found, returns cost; else, returns infinity
  public double getCost(City destination){
    for (Flight flight : flights) {
      if (flight.city.name.equals(destination.name)) return flight.cost;
    }
    return Double.POSITIVE_INFINITY;
  }

  //necessary for PriorityQueue<City> to get next City in flight path
  public int compareTo(City city) {
    return Double.compare(minCost, city.minCost);
  }

}

//FlightSearch - main class:
//reads in user file inputs and flight requests;
//runs functions to compute shortest weighted path;
//loops back for another flight request
public class FlightSearch {

  public static List<City> cities; //array to store City objects

  //create Comparator override for finding City in alphebtically ordered cities array
  //Comparator within City class compares minCosts for PriorityQueue usage
  public static Comparator<City> byName = new Comparator<City>() {
    public int compare(City city1, City city2) {
      return city1.name.compareTo(city2.name);
    }
  };

  //main function takes in user inputs for files and flight requests;
  //and runs necessary functions to compute shortest weighted path
  public static void main (String args[]){

    Scanner scanner = new Scanner(System.in); //scanner for user prompts

    // prompt user for city file and flight file
    System.out.print("\nEnter input city file : ");
    String inputText = scanner.nextLine();
    String cityFileName = inputText;
    System.out.print("\nEnter input flight file : ");
    inputText = scanner.nextLine();
    String flightFileName = inputText;

    try {
      while (true) {
        //open city file, set City[] cities
        Boolean citiesSet = setCities(cityFileName);
        //open flight file, set Flight[] flights of City[] cities
        Boolean flightsSet = setFlights(flightFileName);

        if (!(citiesSet && flightsSet)) break;

        //read in source, destination
        System.out.print("\nEnter flight request [source city] : ");
        inputText = scanner.nextLine();
        String sourceName = inputText;
        System.out.print("\nEnter flight request [destination city] : ");
        inputText = scanner.nextLine();
        String destinationName = inputText;

        //get index of City in cities from city names
        int sourceIndex = getCity(sourceName); //O(log|V|)
        int destinationIndex = getCity(destinationName); //O(log|V|)

        System.out.println("\nRequest is to fly from " + sourceName + " to " + destinationName);

        //check if sourceName and/or destinationName not found in City[] cities
        if ((sourceIndex == -1) && (destinationIndex == -1))
          System.out.println("Sorry. USAir does not serve " + sourceName + " or " + destinationName);
        else if (sourceIndex == -1)
          System.out.println("Sorry. USAir does not serve " + sourceName);
        else if (destinationIndex == -1)
          System.out.println("Sorry. USAir does not serve " + destinationName);

        else { // source and destination found
          //set cheapest flight paths from source to all other cities
          setFlightPaths(cities.get(sourceIndex));
          //get cheapest flight path from source to destination
          getFlightPath(cities.get(sourceIndex), cities.get(destinationIndex));
        }
      }
    }
    catch (ArrayIndexOutOfBoundsException ex) {
      System.out.println("Please input cityFile and flightFile correctly");
    }
  }

  //method reads in city file, returns false if file unreadable or not found;
  //else, initializes Array of cities and adds cities (vertexes) array
  public static boolean setCities(String cityFileName) {

    try {
      //read in city file
      File cityFile = new File(cityFileName);
      FileReader fileReader = new FileReader(cityFile);
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      cities = new ArrayList<City>();
      String line;
      //effectively creates an ordered array by city name
      while ((line = bufferedReader.readLine()) != null) {
        cities.add(new City(line));
      }
      fileReader.close();
      return true;
    }

    catch(FileNotFoundException ex) {
      System.out.println("Unable to open city file '" + cityFileName + "'");
      return false;
    }

    catch(IOException ex) {
      System.out.println("Error reading city file '" + cityFileName + "'");
      return false;
    }
  }


  //method reads in flight file, returns false if file unreadable or not found;
  //else, adds flights (edges) to source cities (vertexes) and returns true
  public static boolean setFlights(String flightFileName) {

    try {
      //read in flight file
      File flightFile = new File(flightFileName);
      FileReader fileReader = new FileReader(flightFile);
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      String line;
      String[] flightInfo;
      while ((line = bufferedReader.readLine()) != null) {
        flightInfo = line.split(", "); //parse source, destination, cost
        int source = getCity(flightInfo[0]); //find source in cities[] - O(log|V|)
        int destination = getCity(flightInfo[1]); //find destination in cities[] - O(log|V|)
        //add flight to flights of source
        cities.get(source).addFlight(cities.get(destination), Double.parseDouble(flightInfo[2]));
      }
      fileReader.close();
      return true;
    }
    catch(FileNotFoundException ex) {
      System.out.println("Unable to open flight file '" + flightFileName + "'");
      return false;
    }
    catch(IOException ex) {
      System.out.println("Error reading flight file '" + flightFileName + "'");
      return false;
    }
  }

  //Dijkstra's algorithm to find single-source shortest path;
  //method uses a PriorityQueue<City> for sorting the cities by their minCost;
  //method creates cheapest flight paths for all possible destinations from the source city;
  //paths can be traced back through City's previous City
  public static void setFlightPaths(City source) {

    PriorityQueue<City> cityQueue = new PriorityQueue<City>();
    source.minCost = 0;
    cityQueue.add(source); //add to queue of next cities to connect to

    while (!(cityQueue.isEmpty())) {
      City city = cityQueue.poll();
      for (Flight flight : city.flights) {//next connecting flight from city
        City connect = flight.city; //destination/connecting city of next flight
        double cost = flight.cost; //cost of flight
        double minCostThruCity = cost + city.minCost; // minCost to City through connecting flight
        if (connect.minCost > minCostThruCity) { //test against min cost from other flights
          cityQueue.remove(connect); //new minCost found, remove city from queue to update it
          connect.minCost = minCostThruCity; //update new minCost
          connect.previousCity = city; //update city of flight with minCost
          cityQueue.add(connect); //add back to queue
        }
      }
    }
  }

  //method finds flight path to source by looping back through previous cities;
  //if flight path leads to source, flight path exists; else, flight path doesn't exist;
  //outputs city, destination, cost for each flight in path
  public static void getFlightPath(City source, City destination) {

    List<City> flightPath = new ArrayList<City>();
    double totalCost = destination.minCost; //cheapest cost to destination
    //loop back through cities' cheapest connecting flights starting from destination
    for (City city = destination; city != null; city = city.previousCity) {
      flightPath.add(city);
    }
    //check if source does not fly to destination
    if (flightPath.get(flightPath.size() - 1) != source) {
      System.out.println("Sorry. USAir does not fly from " + source.name + " to " + destination.name);
    }
    else {
      //path exists, print out flights and costs from source to destination
      for (int i = flightPath.size() - 1; i > 0 ; i--){ //loop backwards to read flight path (source--->destination)
        int flightCost = (int) (flightPath.get(i)).getCost(flightPath.get(i-1)); //get cost of flight
        System.out.println("Flight from " + flightPath.get(i).name + " to " + flightPath.get(i-1).name + "\t\t Cost: $" + flightCost);
      }
      System.out.println("Total Cost...............$" + (int) totalCost);
    }
  }

  //method calls binarySearch on City[] cities comparing City.name w/ city name string;
  //if found, returns index of City in cities; else, returns -1
  public static int getCity(String cityName) {
    int index = Collections.binarySearch(cities, new City(cityName), byName);
    if (index >= 0) return index;
    else return -1;
  }


}
