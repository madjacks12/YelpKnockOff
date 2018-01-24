package dao;

import models.Foodtype;
import models.Restaurant;

import java.util.List;

/**
 * Created by Guest on 1/22/18.
 */
public interface FoodtypeDao {

    //create
    void add(Foodtype foodtype); // N
    void addFoodtypeToRestaurant(Foodtype foodtype, Restaurant restaurant); // D

    //read
    List<Foodtype> getAll(); // we may need this in the future. We can use it to retrieve all Foodtypes.
    List<Restaurant> getAllRestaurantsForAFoodtype(int id); //E we will implement this soon.

    //update
    //omit for now

    //delete
    void deleteById(int id); //see above
}