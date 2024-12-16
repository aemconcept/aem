package com.example.core.servlets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Fruit Management Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET + "," + HttpConstants.METHOD_POST,
                "sling.servlet.paths=" + "/bin/fruitmanager"
        })
public class FruitManagementServlet extends SlingAllMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        handleGetRequest(request, response);
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        handlePostRequest(request, response);
    }

    private void handleGetRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            String fruitToCheck = request.getParameter("fruit");
            if (fruitToCheck == null || fruitToCheck.isEmpty()) {
                response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Please provide a fruit name.");
                return;
            }

            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource fileResource = resourceResolver.getResource("/content/dam/fruit/fruit.json");

            if (fileResource == null) {
                response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("File not found.");
                return;
            }

            InputStream inputStream = fileResource.adaptTo(InputStream.class);
            if (inputStream == null) {
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Unable to read the file.");
                return;
            }

            String jsonContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();

            boolean isPresent = jsonObject.keySet().stream()
                    .anyMatch(key -> key.contains(fruitToCheck));

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

    private void handlePostRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            String key = request.getParameter("key");
            String value = request.getParameter("value");

            if (key == null || value == null || key.isEmpty() || value.isEmpty()) {
                response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Both 'key' and 'value' parameters are required.");
                return;
            }

            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource fileResource = resourceResolver.getResource("/content/dam/fruit/fruit.json");

            if (fileResource == null) {
                response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("File not found.");
                return;
            }

            InputStream inputStream = fileResource.adaptTo(InputStream.class);
            if (inputStream == null) {
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Unable to read the file.");
                return;
            }

            String jsonContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();

            // Add or update the key-value pair
            jsonObject.addProperty(key, value);

            // Save the updated JSON back to the DAM
            String updatedJson = jsonObject.toString();
            InputStream updatedInputStream = new ByteArrayInputStream(updatedJson.getBytes(StandardCharsets.UTF_8));
            ModifiableValueMap properties = fileResource.adaptTo(ModifiableValueMap.class);

            if (properties != null) {
                properties.put("jcr:data", updatedInputStream);
                resourceResolver.commit();

                response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"success\",\"message\":\"File updated successfully.\"}");
            } else {
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Unable to update the file.");
            }

        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("An error occurred: " + e.getMessage());
            } catch (Exception ignored) {
            }
        }
    }
}
