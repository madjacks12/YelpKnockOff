package dao;

import models.Foodtype;
import models.Restaurant;
import models.Review;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.Arrays;

import static org.junit.Assert.*;

import static org.junit.Assert.*;

/**
 * Created by Guest on 1/22/18.
 */
public class Sql2oRestaurantDaoTest {

    private Connection conn;
    private Sql2oRestaurantDao restaurantDao;
    private Sql2oFoodtypeDao foodtypeDao;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingRestaurantSetsId() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        int originalRestaurantId = testRestaurant.getId();
        restaurantDao.add(testRestaurant);
        assertEquals(1, testRestaurant.getId());
    }

    @Test
    public void getAll() throws Exception {
        Restaurant testRestaurantOne = setupRestaurant();
        Restaurant testRestaurantTwo = setupRestaurant();
        restaurantDao.add(testRestaurantOne);
        restaurantDao.add(testRestaurantTwo);
        assertEquals(2, restaurantDao.getAll().size());
    }

    @Test
    public void findById() throws Exception {
        Restaurant testRestaurantOne = setupRestaurant();
        Restaurant testRestaurantTwo = setupRestaurant();
        restaurantDao.add(testRestaurantOne);
        restaurantDao.add(testRestaurantTwo);
        assertEquals(2, restaurantDao.findById(2).getId());
    }

    @Test
    public void update() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        restaurantDao.update(testRestaurant.getId(),"Sandiwch", "111", "97202", "555", "www.com", "sup@gmail.com");
        assertNotEquals(testRestaurant.getName(), restaurantDao.findById(testRestaurant.getId()).getName());
    }

    @Test
    public void deleteById() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        restaurantDao.deleteById(testRestaurant.getId());
        assertEquals(0, restaurantDao.getAll().size());
    }

    @Test
    public void getAllFoodtypesForARestaurantReturnsFoodtypesCorrectly() throws Exception {
        Foodtype testFoodtype  = new Foodtype("Seafood");
        foodtypeDao.add(testFoodtype);

        Foodtype otherFoodtype  = new Foodtype("Bar Food");
        foodtypeDao.add(otherFoodtype);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        restaurantDao.addRestaurantToFoodtype(testRestaurant,testFoodtype);
        restaurantDao.addRestaurantToFoodtype(testRestaurant,otherFoodtype);

        Foodtype[] foodtypes = {testFoodtype, otherFoodtype}; //oh hi what is this?

        assertEquals(restaurantDao.getAllFoodtypesForARestaurant(testRestaurant.getId()), Arrays.asList(foodtypes));
    }

    @Test
    public void deletingFoodtypeAlsoUpdatesJoinTable() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        Foodtype testFoodtype  = new Foodtype("Seafood");
        foodtypeDao.add(testFoodtype);
        Foodtype otherFoodtype  = new Foodtype("Bar Food");
        foodtypeDao.add(otherFoodtype);
        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, testRestaurant);
        foodtypeDao.addFoodtypeToRestaurant(otherFoodtype, testRestaurant);
        restaurantDao.deleteById(testRestaurant.getId());
        assertEquals(0, foodtypeDao.getAllRestaurantsForAFoodtype(testRestaurant.getId()).size());
    }


    public Restaurant setupRestaurant() {
        return new Restaurant("Fish Witch", "214 NE Broadway", "97232", "503-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
    }
}