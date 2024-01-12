package com.distribuida;

import com.distribuida.rest.BookRest;
import com.distribuida.servicios.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import static spark.Spark.*;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Principal {
    static SeContainer container;

    public static void main(String[] args) {

        container = SeContainerInitializer
                .newInstance()
                .initialize();

        port(8081);

        BookService servicio = container.select(BookService.class)
                .get();

        BookRest bookRest = new BookRest(servicio);

        ObjectMapper mapper = new ObjectMapper();

        get("/books", bookRest::findAll, mapper::writeValueAsString);
        get("/books/:id", bookRest::findById, mapper::writeValueAsString);
        post("/books", bookRest::insert, mapper::writeValueAsString);
        put("/books/:id", bookRest::update, mapper::writeValueAsString);
        delete("/books/:id", bookRest::remove, mapper::writeValueAsString);

        // Configuración CORS
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "*");
            response.type("application/json");
        });
    }
}
