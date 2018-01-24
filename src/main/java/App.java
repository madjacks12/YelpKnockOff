import com.google.gson.Gson;
import dao.Sql2oFoodtypeDao;
import dao.Sql2oRestaurantDao;
import dao.Sql2oReviewDao;
import exceptions.ApiException;
import models.Foodtype;
import models.Restaurant;
import models.Review;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class App {

    public static void main(String[] args) {
        Sql2oFoodtypeDao foodtypeDao;
        Sql2oRestaurantDao restaurantDao;
        Sql2oReviewDao reviewDao;
        Connection conn;
        Gson gson = new Gson();

        String connectionString = "jdbc:h2:~/jadle.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open();

        post("/restaurants/new", "application/json", (req, res) -> { //accept a request in format JSON
            Restaurant restaurant = gson.fromJson(req.body(), Restaurant.class);//make with GSON
            restaurantDao.add(restaurant);//Do our thing with our DAO
            res.status(201);//everything went well - update the response status code
            return gson.toJson(restaurant);//send it back to be displayed
        });

        get("/restaurants", "application/json", (req, res) -> { //accept a request in format JSON from an app
            return gson.toJson(restaurantDao.getAll());//send it back to be displayed
        });

        get("/restaurants/:id", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("id"));

            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);

            if (restaurantToFind == null) {
                throw new ApiException(404, String.format("No restaurant with id: \"%s\" exists", req.params("id")));
            }
            return gson.toJson(restaurantToFind);
        });

        post("/foodtypes/new", "application/json", (req, res) -> {
            Foodtype foodtype = gson.fromJson(req.body(), Foodtype.class);
            foodtypeDao.add(foodtype);
            res.status(201);
            return gson.toJson(foodtype);
        });

        get("/foodtypes", "application/json", (req, res) -> {
            return gson.toJson(foodtypeDao.getAll());
        });

        get("/restaurants/:id/reviews", "application/json", (request, response) -> {
            int restaurantId = Integer.parseInt(request.params("id"));
            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
            List<Review> allReviews;
            allReviews = reviewDao.getAllReviewsByRestaurant(restaurantId);
            return gson.toJson(allReviews);
        });

        post("/restauraunts/:restaurantID/reviews/new", "application/json", (request, response) -> {
            int restaurantId = Integer.parseInt(request.params("restaurantId"));
            Review review = gson.fromJson(request.body(), Review.class);
            review.setRestaurantId(restaurantId);
            reviewDao.add(review);
            response.status(201);
            return gson.toJson(review);
        });

        //FILTERS
        exception(ApiException.class, (exception, request, response) -> {
            ApiException err = (ApiException) exception;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", err.getStatusCode());
            jsonMap.put("errorMessage", err.getMessage());
            response.type("application/json");
            response.status(err.getStatusCode());
            response.body(gson.toJson(jsonMap));
        });


        after((req, res) -> {
            res.type("application/json");
        });
    }
}
