package hci.voladeacapp;

import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Flight implements Serializable {
    private String number;
    private String airline;
    private String state;
    private double price;
    private String departureAirport;
    private String departureCity;
    private String arrivalAirport;
    private String arrivalCity;
    private String baggageClaim;

    private Date departureDate;
    private Date arrivalDate;
    private int duration;

    public Flight(FlightStatusGson seed){
        setArrivalCity(seed.arrival.airport.city.name);
        setNumber("" + seed.number);
        setDepartureCity(seed.departure.airport.city.name);
        setState(seed.status);
        setAirline(seed.airline.id);
    }


    public Flight(){} //TODO:Sacar


    public boolean update(FlightStatusGson newStatus){
        return true;
    }

    private String imageURL;


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
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getArrivalDateInFormat(String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(arrivalDate);
    }

    public String getDepartureDateInFormat(String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(departureDate);
    }

    public String getBaggageClaim() {
        return baggageClaim;
    }

    public void setBaggageClaim(String baggageClaim) {
        this.baggageClaim = baggageClaim;
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
