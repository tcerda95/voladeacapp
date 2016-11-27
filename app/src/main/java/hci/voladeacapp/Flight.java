package hci.voladeacapp;

import java.text.DateFormat;

import java.io.Serializable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Flight implements Serializable {
    private static final long serialVersiouUID = 1L;

    private FlightIdentifier identifier;

    private String fullAirlineName;
    private String state;
    private double price; // in USD

    private FlightSchedule departureSchedule = new FlightSchedule(); // Para que no tire NPE
    private FlightSchedule arrivalSchedule = new FlightSchedule();

    private String baggageClaim;


    private boolean confirmedDeparture = false;
    private boolean confirmedArrival = false;

    private int duration;

    public static class FlightDate implements Serializable {
        public Date date;
        public String timestamp;

        public FlightDate(){
            date = new Date();
        }

        public FlightDate(String time) {
            date = new Date();
            if (time != null) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String[] split = time.split(" ");
                String[] timeSplit = split[1].split(":");
                try {
                    date = dateFormat.parse(split[0]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                timestamp = timeSplit[0] + ":" + timeSplit[1];
            }
        }

        public String toString() {
            return date.toString() + " " + timestamp;
        }

        public boolean equals(Object o){
            if(o == null || !(o instanceof FlightDate))
                return false;

            FlightDate other = (FlightDate)o;
            return other.toString().equals(toString());
        }
    }

    /**
     * Agrupa la información que tanto la salida como la llegada poseen.
     */
    public static class FlightSchedule implements Serializable {
        public boolean gate_delayed;
        public boolean runway_delayed;
        public String airport;
        public String airportId;
        public String timezone;
        public String city;
        public String gate;
        public String terminal;
        public FlightDate flightDate;
        public FlightDate scheduledDate;


        public int delay;

        public FlightSchedule(){
            flightDate = new FlightDate();
        }

        public FlightSchedule(FlightStatusGson.Schedule schedule) {
            String[] splittedAirport = schedule.airport.description.split(",");
            airport = splittedAirport[0];
            airportId = schedule.airport.id;
            city = schedule.airport.city.name;
            gate = schedule.airport.gate;
            terminal = schedule.airport.terminal;
            flightDate = schedule.actual_time == null ? null : new FlightDate(schedule.actual_time);
            scheduledDate = new FlightDate(schedule.scheduled_time);
            timezone = schedule.airport.time_zone;

            delay = 0;

            if(schedule.gate_delay != null){
                delay += schedule.gate_delay;
                gate_delayed = true;
            }

            if(schedule.runway_delay != null){
                delay += schedule.runway_delay;
                runway_delayed = true;
            }

        }

        @Override
        public String toString() {
            return airport + " " + airportId + " " + city + " " + gate + " " + terminal + " " + flightDate.toString();
        }

        public String getDateInFormat(String format) {
            return new SimpleDateFormat(format, Locale.ENGLISH).format( flightDate != null ? flightDate.date : scheduledDate.date );
        }

        public String getBoardingTime() {
            return flightDate == null ? scheduledDate.timestamp : flightDate.timestamp;
        }

        public String getAirport() {
            return airport;
        }

        public String getAirportId() {
            return airportId;
        }

        public String getTerminal() {
            return terminal;
        }

        public String getGate() {
            return gate;
        }
    }

    public Flight(FlightStatusGson seed) {
        identifier = new FlightIdentifier();

        setNumber("" + seed.number);
        setAirlineID(seed.airline.id);
        setState(seed.status);
        setBaggageClaim(seed.arrival.airport.baggage);
        setAirlineName(seed.airline.name);

        departureSchedule = new FlightSchedule(seed.departure);
        arrivalSchedule = new FlightSchedule(seed.arrival);
    }


    public NotificationCategory _update(FlightStatusGson newStatus){
        List<NotificationCategory> changes = new ArrayList<>();
        NotificationCategory change = null;


        boolean delayCheck = false;
        if(!arrivalSchedule.runway_delayed && newStatus.arrival.runway_delay != null){
            arrivalSchedule.runway_delayed = true;
            arrivalSchedule.delay += newStatus.arrival.runway_delay;
            delayCheck = true;
        }

        if(!arrivalSchedule.gate_delayed && newStatus.arrival.gate_delay != null){
            arrivalSchedule.runway_delayed = true;
            arrivalSchedule.delay += newStatus.arrival.gate_delay;
            delayCheck = true;
        }

        if(delayCheck){
            setState("D");
            change = NotificationCategory.DELAY_LANDING;
        }


        delayCheck = false;

        if(!departureSchedule.runway_delayed && newStatus.departure.runway_delay != null){
            departureSchedule.runway_delayed = true;
            departureSchedule.delay += newStatus.departure.runway_delay;
            delayCheck = true;
        }

        if(!departureSchedule.gate_delayed && newStatus.departure.gate_delay != null){
            departureSchedule.gate_delayed = true;
            departureSchedule.delay += newStatus.departure.gate_delay;
            delayCheck = true;
        }

        if(delayCheck){
            setState("D");
            change = NotificationCategory.DELAY_TAKEOFF;
        }

        setBaggageClaim(newStatus.arrival.airport.baggage);


        if(newStatus.departure.actual_time != null) {
            if(!confirmedDeparture){
                confirmedDeparture = true;

                FlightDate newDepartureFlightDate = new FlightDate(newStatus.departure.actual_time);
                departureSchedule.flightDate = newDepartureFlightDate;
            }
        }

        if(newStatus.arrival.scheduled_time != null){
            FlightDate newDepartureScheduledDate = new FlightDate(newStatus.arrival.scheduled_time);
            arrivalSchedule.scheduledDate = newDepartureScheduledDate;
        }

        if(newStatus.arrival.actual_time != null){
            if(!confirmedArrival){
                confirmedArrival = true;
                FlightDate newDepartureFlightDate = new FlightDate(newStatus.arrival.actual_time);
                arrivalSchedule.flightDate = newDepartureFlightDate;
            }
        }


        if(!state.equals(newStatus.status)){
            switch(newStatus.status) {
                case "A":
                    change = NotificationCategory.TAKEOFF;
                    break;
                case "R":
                    change = NotificationCategory.DEVIATION;
                    break;
                case "L":
                    change = NotificationCategory.LANDING;
                    break;
                case "C":
                    change = NotificationCategory.CANCELATION;
                    break;
            }
        }

        setState(newStatus.status);


        return change;
    }


    public Flight(){
        this.identifier = new FlightIdentifier();
    } //TODO:Sacar

    private String imageURL;

    public void setAirlineName(String name) { fullAirlineName = name; }

    public String getFullAirlineName() {
        return fullAirlineName;
    }

    public FlightSchedule getDepartureSchedule() {
        return departureSchedule;
    }

    public FlightSchedule getArrivalSchedule() {
        return arrivalSchedule;
    }

    public String getAerolinea() {
        return identifier.getAirline();
    }

    public void setAerolinea(String aerolinea) {
        identifier.setAirline(aerolinea);
    }

    public String getState() {
        if (state.equals("S") && departureSchedule.delay != 0){
            return "D";
        }
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public FlightIdentifier getIdentifier(){ return identifier; }

    public void setIdentifier(FlightIdentifier i){ this.identifier = i; }

    public String getNumber() {
        return identifier.getNumber();
    }


    public void setNumber(String number) {
        this.identifier.setNumber(number);
    }

    public String getAirlineID() {
        return identifier.getAirline();
    }

    public void setAirlineID(String airline) {
        identifier.setAirline(airline);
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDepartureAirport() {
        return departureSchedule.airport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureSchedule.airport = departureAirport;
    }

    public String getDepartureCity() {
        return departureSchedule.city;
    }

    public void setDepartureCity(String departureCity) {
        this.departureSchedule.city = departureCity;
    }

    public String getArrivalAirport() {
        return arrivalSchedule.airport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalSchedule.airport = arrivalAirport;
    }

    public String getArrivalCity() {
        return arrivalSchedule.city;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalSchedule.city = arrivalCity;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getArrivalDateInFormat(String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(arrivalSchedule.flightDate.date);
    }

    public String getDepartureDateInFormat(String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(departureSchedule.flightDate.date);
    }

    public String getBaggageClaim() {
        return baggageClaim;
    }

    public void setBaggageClaim(String baggageClaim) {
        this.baggageClaim = baggageClaim;
    }

    public String getArrivalAirportId() {
        return arrivalSchedule.airportId;
    }

    public String getDepartureAirportId() {
        return departureSchedule.airportId;
    }

    public String getDepartureBoardingTime() {
        return departureSchedule.flightDate.timestamp;
    }

    public String getArrivalBoardingTime() {
        if(arrivalSchedule.flightDate.timestamp != null) {
            return arrivalSchedule.flightDate.timestamp;
        } else {
            return arrivalSchedule.scheduledDate.timestamp;
        }
    }

    public String getDepartureGate() {
        return departureSchedule.gate;
    }

    public String getDepartureTerminal() {
        return departureSchedule.terminal;
    }

    public String getArrivalGate() {
        return arrivalSchedule.gate;
    }

    public String getArrivalTerminal() {
        return arrivalSchedule.terminal;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureSchedule.flightDate.date = departureDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalSchedule.flightDate.date = arrivalDate;
    }

    public Date getDepartureDate() {
        return departureSchedule.flightDate.date;
    }

    public Date getArrivalDate() {
        return arrivalSchedule.flightDate.date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flight flight = (Flight) o;

        return this.identifier.equals(flight.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
