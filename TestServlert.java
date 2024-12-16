package com.example.core.servlets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.framework.Constants;

import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Fruit Check Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/bin/checkfruit"
        })
public class FruitCheckServlet extends SlingAllMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            // Get the fruit name from the request
            String fruitToCheck = request.getParameter("fruit");
            if (fruitToCheck == null || fruitToCheck.isEmpty()) {
                response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Please provide a fruit name.");
                return;
            }

            // Access the DAM file
            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource fileResource = resourceResolver.getResource("/content/dam/fruit/fruit.json");
            if (fileResource == null) {
                response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("File not found.");
                return;
            }

            // Read the JSON file
            InputStream inputStream = fileResource.adaptTo(InputStream.class);
            if (inputStream == null) {
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Unable to read the file.");
                return;
            }

            StringBuilder jsonContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonContent.append(line);
                }
            }

            // Parse the JSON content
            JsonObject jsonObject = JsonParser.parseString(jsonContent.toString()).getAsJsonObject();

            // Check if the fruit is present
            boolean isPresent = jsonObject.keySet().stream()
                    .anyMatch(key -> key.contains(fruitToCheck));

            // Return the result
            response.setContentType("application/json");
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("fruit", fruitToCheck);
            jsonResponse.addProperty("isPresent", isPresent);
            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("An error occurred: " + e.getMessage());
            } catch (Exception ignored) {
            }
        }
    }
}
