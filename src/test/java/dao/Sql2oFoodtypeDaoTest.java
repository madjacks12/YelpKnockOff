package dao;

import models.Foodtype;
import models.Restaurant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import static org.junit.Assert.*;

import static org.junit.Assert.*;

import static org.junit.Assert.*;

/**
 * Created by Guest on 1/22/18.
 */
public class Sql2oFoodtypeDaoTest {

    private Connection conn;
    private Sql2oFoodtypeDao foodtypeDao;
    private Sql2oRestaurantDao restaurantDao;


    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void add() throws Exception {
        Foodtype testFoodtype = setUpFoodtype();
        foodtypeDao.add(testFoodtype);
        assertEquals(1, testFoodtype.getId());

    }

    @Test
    public void getAll() throws Exception {
        Foodtype testFoodtype = setUpFoodtype();
        foodtypeDao.add(testFoodtype);
        assertEquals(1, foodtypeDao.getAll().size());
    }

    @Test
    public void deleteById() throws Exception {
        Foodtype testFoodtype = setUpFoodtype();
        foodtypeDao.add(testFoodtype);
        foodtypeDao.deleteById(testFoodtype.getId());
        assertEquals(0, foodtypeDao.getAll().size());
    }

    @Test
    public void addFoodTypeToRestaurantAddsTypeCorrectly() throws Exception {

        Restaurant testRestaurant = setupRestaurant();
        Restaurant altRestaurant = setupAltRestaurant();

        restaurantDao.add(testRestaurant);
        restaurantDao.add(altRestaurant);

        Foodtype testFoodtype = setUpFoodtype();

        foodtypeDao.add(testFoodtype);

        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, testRestaurant);
        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, altRestaurant);

        assertEquals(2, foodtypeDao.getAllRestaurantsForAFoodtype(testFoodtype.getId()).size());
    }

    @Test
    public void deleteingRestaurantAlsoUpdatesJoinTable() throws Exception {
        Restaurant testRestaurant = setupRestaurant();

        restaurantDao.add(testRestaurant);

        Foodtype testFoodtype = setUpFoodtype();
        Foodtype otherFoodType = new Foodtype("Japanese");

        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, testRestaurant);
        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, testRestaurant);

        restaurantDao.deleteById(testRestaurant.getId());
        assertEquals(0, foodtypeDao.getAllRestaurantsForAFoodtype(testFoodtype.getId()).size());
    }

    public Restaurant setupRestaurant() {
        return new Restaurant("Fish Witch", "214 NE Broadway", "97232", "503-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
    }

    public Restaurant setupAltRestaurant() {
        return new Restaurant("Bunk", "500 SW 5th", "97202", "505", "http://bunk.com", "hellobunk@bunk.com");
    }

    public Foodtype setUpFoodtype() {
        return new Foodtype("American");
    }
}