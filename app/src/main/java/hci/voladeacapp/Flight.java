package hci.voladeacapp;

import java.text.DateFormat;

import java.io.Serializable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Flight implements Serializable {
    private String number;
    private String airline;
    private String state;
    private double price;

    private FlightSchedule departureSchedule = new FlightSchedule(); // Para que no tire NPE
    private FlightSchedule arrivalSchedule = new FlightSchedule();

    private String baggageClaim;

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
    }

    /**
     * Agrupa la información que tanto la salida como la llegada poseen.
     */
    public static class FlightSchedule implements Serializable {
        public String airport;
        public String airportId;
        public String city;
        public String gate;
        public String terminal;
        public FlightDate flightDate;

        public FlightSchedule(){
            flightDate = new FlightDate();
        }

        public FlightSchedule(FlightStatusGson.Schedule schedule) {
            airport = schedule.airport.description;
            airportId = schedule.airport.id;
            city = schedule.airport.city.name;
            gate = schedule.airport.gate;
            terminal = schedule.airport.terminal;
            flightDate = new FlightDate(schedule.actual_time == null ? schedule.scheduled_time : schedule.actual_time);
        }

        @Override
        public String toString() {
            return airport + " " + airportId + " " + city + " " + gate + " " + terminal + " " + flightDate.toString();
        }

        public String getDateInFormat(String format) {
            return new SimpleDateFormat(format, Locale.ENGLISH).format(flightDate.date);
        }

        public String getBoardingTime() {
            return flightDate.timestamp;
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
        setNumber("" + seed.number);
        setAirline(seed.airline.id);
        setState(seed.status);

        departureSchedule = new FlightSchedule(seed.departure);
        arrivalSchedule = new FlightSchedule(seed.arrival);
    }


    public Flight(){} //TODO:Sacar


    public boolean update(FlightStatusGson newStatus){
        return true;
    }

    private String imageURL;


    public FlightSchedule getDepartureSchedule() {
        return departureSchedule;
    }

    public FlightSchedule getArrivalSchedule() {
        return arrivalSchedule;
    }

    public String getAerolinea() {
        return airline;
    }

    public void setAerolinea(String aerolinea) {
        this.airline = aerolinea;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNumber() {
        return number;
    }


    public void setNumber(String number) {
        this.number = number;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
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
        return arrivalSchedule.flightDate.timestamp;
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

        if (!number.equals(flight.number)) return false;
        return airline.equals(flight.airline);
    }

    @Override
    public int hashCode() {
        int result = number.hashCode();
        result = 31 * result + airline.hashCode();
        return result;
    }
}
