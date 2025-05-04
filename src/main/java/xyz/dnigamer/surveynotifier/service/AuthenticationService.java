package xyz.dnigamer.surveynotifier.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class AuthenticationService {

    private static final String REDIS_AUTH_TOKEN_KEY = "authToken";

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final Logger logger = Logger.getLogger(AuthenticationService.class.getName());

    // Login to the website and fetch a new token
    public void login() throws IOException {
        try {
            Dotenv dotenv = Dotenv.load();
            String email = dotenv.get("SURVEY_EMAIL");
            String password = dotenv.get("SURVEY_PASSWORD");
            if (email == null || password == null) {
                throw new IllegalArgumentException("Email or password not set in environment variables");
            }

            Connection.Response response = Jsoup.connect("https://app.synopanel.com/login")
                    .method(Connection.Method.POST)
                    .data("email", email)
                    .data("password", password)
                    .execute();

            String authToken = response.cookie("SFSESSID");
            if (authToken == null) {
                throw new IOException("Failed to retrieve auth token");
            }

            redisTemplate.opsForValue().set(REDIS_AUTH_TOKEN_KEY, authToken, 1, TimeUnit.HOURS);
        } catch (IOException e) {
            throw new IOException("Login failed: " + e.getMessage(), e);
        }
    }

    // Get the current token, logging in if necessary
    public String getAuthToken() throws IOException {
        String authToken = redisTemplate.opsForValue().get(REDIS_AUTH_TOKEN_KEY);

        if (authToken == null || isTokenExpired(authToken)) {
            login();
            authToken = redisTemplate.opsForValue().get(REDIS_AUTH_TOKEN_KEY);
        }

        return authToken;
    }

    // Check if the token is expired (implement your logic here)
    private boolean isTokenExpired(String authToken) {
        try {
            Connection.Response response = Jsoup.connect("https://app.synopanel.com/surveys/list")
                    .cookie("SFSESSID", authToken)
                    .execute();
            return response.url().toString().contains("login");
        } catch (IOException e) {
            logger.warning("Error checking token expiration: " + e.getMessage());
            return true;
        }
    }
}
