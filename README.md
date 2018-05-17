# FlightSearch
FlightSearch Program to find minimum weighted path from source to destination

FlightSearch uses an array of arrays to represent an adjacency list of vertexes (cities) with edges (flights).
FlightSearch uses Dijkstra’s Algorithm and a priority queue to find the cheapest flight paths from the source to all destinations.

Files:
-README.md

-FlightSearch.java
    Classes:
	- FlightSearch
	- City (vertex)
	- Flight (weighted edge)
    Main Functions:
	- main()
	- setCities() - 	O( |V| )
	- setFlights() - 	O( |E|log|V| )
	- setFlightPaths() - 	O( |V|log|V| + |E|log|V| )
	- getFlightPath() - 	O( |V||E| )
	- getCity() - 		O( log|V| )

-cityFile1.txt  ( |V| = 4 )
-cityFile2.txt  ( |V| = 10 )
-cityFile3.txt  ( |V| = 26 )
-flightFile1.txt  ( |E| = 5 ) 
-flightFile2.txt  ( |E| = 19 )
-flightFile3.txt  ( |E| = 61 )

Implementation:

FlightSearch uses an ArrayList to hold the City objects (in alphabetical order, as inputted) and an explicitly declared Comparator to get the index of a City in cities[ ] with binarySearch() by City name. Each City has an ArrayList of Flights, each Flight has a destination City and cost. FlightSearch method setFlightPaths() uses a PriorityQueue to hold City objects [in order of minCost (from source to City)] and a Comparator override within class City to compare minCosts. 


Program Functionality:

FlightSearch.java runs the FlightSearch program; prompts the user for a cityFile and flightFile input; exits with error messages if either file cannot be read or found; else, prompts the user for the source and destination of a flight request. If one or both cities are not in the set of cities, or if a flight path from the source to destination does not exist, respective error messages are thrown and the user is re-prompted for another flight request input.  If the path does exist, each flight and cost from the source city to the destination is printed and the user is prompted for another flight request. 

Upon receiving all inputs for a flight request:
-FlightSearch calls setCities() to initialize and set array of cities - O( |V| )
-FlightSearch calls setFlights() to initialize and set array of flights within each city
	- setFlights() calls getCity() to get index of City from string input
	- O ( |E|log|V| )
-FlightSearch calls setFlightPaths() which uses Dijkstra’s Algorithm to find the single-source shortest weighted path from source to all destinations. Method loops while the cityQueue is not empty; polls the City w/ minCost from the queue (initially the source); checks if flight from City to destinations costs less than current minCost of each destination; if new min is found, destination city is added to queue and the loop continues.  This effectively creates the cheapest flight paths from the source city to every other reachable city and saves the path by setting the previousCity of the updated city to hold the City from which the cheapest flight reaches the updated City. 
	- O( |V|log|V| + |E|log|V| )
-FlightSearch calls getFlightPath() to loop back through previousCitys from the destination to produce the flight path from source to destination. If flight path doesn’t exist from source to destination, looping back through previousCitys will not end at the source and the user is informed the flight path does not exist.  If flight path does exist, each flight and cost, and total cost is printed
	- O( |V||E| )
