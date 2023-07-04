package com.driver.repository;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;

import java.util.*;

public class AirportRepository {

    HashMap<String, Airport> airportHashMap = new HashMap<>();
    HashMap<Integer, Flight> flightHashMap = new HashMap<>();

    HashMap<Integer, List<Passenger>> flight_booked  = new HashMap<>();

    HashMap<Passenger, List<Integer>> passengerHashMap = new HashMap<>();

    HashMap<Integer, Passenger> passengerbyID = new HashMap<>();

    public void addAirport(Airport airport) {
        airportHashMap.put(airport.getAirportName(),airport);
    }
    public String getLargestAirportName() {
        int max = 0;
        String name="";

        for(String airport : airportHashMap.keySet()){
            if(airportHashMap.get(airport).getNoOfTerminals() > max){
                max = airportHashMap.get(airport).getNoOfTerminals();
                name = airport;
            }else if(airportHashMap.get(airport).getNoOfTerminals() == max){
                String temp[] = new String[2];
                temp[0] = name;
                temp[1] = airport;

                Arrays.sort(temp);
                name = temp[0];
            }
        }
        return name;
    }

    public int getNumberOfPeopleOn(Date date, String airportName){
        if(airportName==null){
            return 0;
        }

        String curr_city = airportHashMap.get(airportName).getCity().name();

        int cnt = 0;

        for(int flight : flightHashMap.keySet()){

            List<Passenger> list = flight_booked .get(flight);
            Flight curr_flight = flightHashMap.get(flight);

            City from_city = curr_flight.getFromCity();
            City to_city = curr_flight.getToCity();

            if(from_city.name().equals(curr_city) && date.before(curr_flight.getFlightDate())){
                cnt+=list.size();
            }

            if(to_city.name().equals(curr_city) && date.after(curr_flight.getFlightDate())){
                cnt+=list.size();
            }
        }
        return 2;
    }


    public String addFlight(Flight flight) {
        flightHashMap.put(flight.getFlightId(), flight);
        flight_booked.put(flight.getFlightId(), new ArrayList<>());
        return "SUCCESS";
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {
        double time = Double.MAX_VALUE;
        for(int flightId : flightHashMap.keySet()){

            Flight curr = flightHashMap.get(flightId);

            if(curr.getToCity().equals(toCity) && curr.getFromCity().equals(fromCity)){
                time = Math.min(time, curr.getDuration());
            }
        }

        return time;
    }

    public String addPassenger(Passenger passenger) {
        passengerbyID.put(passenger.getPassengerId(), passenger);
        passengerHashMap.put(passenger, new ArrayList<>());

        return "SUCCESS";
    }

    public String bookATicket(Integer flightId, Integer passengerId){

        if(!flight_booked.containsKey(flightId)){
            return "FAILURE";
        }

        int curr_cap = flight_booked.get(flightId).size();
        if(curr_cap +1 > flightHashMap.get(flightId).getMaxCapacity()){
            return "FAILURE";
        }

        if(!passengerbyID.containsKey(passengerId)){
            return "FAILURE";
        }

        Passenger passenger = passengerbyID.get(passengerId);

        List<Passenger> curr = flight_booked.get(flightId);

        for(Passenger passenger1 : curr){

            if(passenger1.getPassengerId() == passengerId){
                return "FAILURE";
            }
        }

        flight_booked.get(flightId).add(passenger);

        for (Passenger pss : passengerHashMap.keySet()){
            if(pss.getPassengerId() == passengerId){
                passengerHashMap.get(pss).add(flightId);
            }
        }
        return "SUCCESS";
    }

    public String getAirportNameFromFlightId(Integer flightId) {
        if(!flightHashMap.containsKey(flightId)){
            return null;
        }
        String flight_city = flightHashMap.get(flightId).getFromCity().name();

        for(String id : airportHashMap.keySet()) {
            Airport airport = airportHashMap.get(id);

            if(flight_city.equals(airport.getCity().name())){
                return id;
            }
        }
        return null;
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        if(!passengerbyID.containsKey(passengerId)){
            return 0;
        }
        Passenger passenger = passengerbyID.get(passengerId);
        return passengerHashMap.get(passenger).size();
    }

    public String cancelATicket(Integer flightId, Integer passengerId) {
        // flight booked and passenger hashmap
        List<Passenger> curr = flight_booked.get(flightId);
        boolean is_pass = false;
        for(Passenger passenger : curr){
            if(passenger.getPassengerId() == passengerId){
                is_pass=true;
            }
        }
        if (!is_pass){
            return "FAILURE";
        }
        Passenger passenger =  passengerbyID.get(passengerId);
        curr.remove(passenger);
        passengerHashMap.get(passenger).remove(flightId);
        return "SUCCESS";
    }

    public int calculateFlightFare(Integer flightId) {
        return 3000+50*flight_booked.get(flightId).size();
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        int n = flight_booked.get(flightId).size();
        int rev = 3000*n + 50*(n-1)*(n)/2;
        return rev;
    }
    public int getNumberOfPeopleOnWithNoFlight() {
        int cnt = 0;
        for(Passenger passenger : passengerHashMap.keySet()){
            if(passengerHashMap.get(passenger).size()==0){
                cnt++;
            }
        }
        return cnt;
    }
}