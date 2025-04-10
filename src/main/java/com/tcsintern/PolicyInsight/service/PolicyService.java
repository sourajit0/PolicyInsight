package com.tcsintern.PolicyInsight.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tcsintern.PolicyInsight.config.ApiConfig;
import com.tcsintern.PolicyInsight.model.PolicyResponse;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class PolicyService {

    @Autowired
    private ApiConfig apiConfig;

    public PolicyResponse processPolicyDocument(MultipartFile pdfFile) {
        try {
            // Convert PDF to Base64
            String base64Pdf = Base64.getEncoder().encodeToString(pdfFile.getBytes());

            // Construct request body
            JsonObject requestBody = createRequestBody(base64Pdf);

            // Call Gemini API
            String apiResponse = callGeminiApi(requestBody.toString());

            // Extract HTML table from the response
            String htmlTable = extractHtmlTable(apiResponse);

            return new PolicyResponse(htmlTable);
        } catch (Exception e) {
            e.printStackTrace();
            return new PolicyResponse(false, "Error processing the PDF: " + e.getMessage());
        }
    }

    private JsonObject createRequestBody(String base64Pdf) {
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();

        // Add text instruction part
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", "Extract the key details from this insurance policy PDF and return the result as an HTML table. The table should contain fields like:\n\n" +
                "- Product Name\n" +
                "- Insurance Company Name\n" +
                "- Type of Plan\n" +
                "- Eligibility\n" +
                "- Sum Insured\n" +
                "- Coverages\n" +
                "- Exclusions\n" +
                "- Policy Tenure\n" +
                "- Claim Process\n" +
                "- Premium Info\n\n" +
                "Ensure the output is in proper HTML <table> format with headings in <th> and data in <td>. Return only the HTML table, nothing else.");
        parts.add(textPart);

        // Add PDF part
        JsonObject pdfPart = new JsonObject();
        JsonObject inlineData = new JsonObject();
        inlineData.addProperty("mime_type", "application/pdf");
        inlineData.addProperty("data", base64Pdf);
        pdfPart.add("inline_data", inlineData);
        parts.add(pdfPart);

        content.add("parts", parts);
        contents.add(content);
        requestBody.add("contents", contents);

        return requestBody;
    }

    private String callGeminiApi(String requestBody) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(apiConfig.getGeminiApiFullUrl());
        httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
        httpPost.setHeader("Content-Type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    return EntityUtils.toString(entity, "UTF-8");
                } catch (org.apache.hc.core5.http.ParseException e) {
                    throw new IOException("Error parsing HTTP response: " + e.getMessage(), e);
                }
            } else {
                throw new IOException("Empty response from API");
            }
        }
    }

    private String extractHtmlTable(String apiResponse) {
        try {
            JsonObject jsonResponse = JsonParser.parseString(apiResponse).getAsJsonObject();

            JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
            if (candidates != null && candidates.size() > 0) {
                JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                JsonObject content = firstCandidate.getAsJsonObject("content");
                JsonArray parts = content.getAsJsonArray("parts");

                for (JsonElement part : parts) {
                    if (part.isJsonObject() && part.getAsJsonObject().has("text")) {
                        String text = part.getAsJsonObject().get("text").getAsString();
                        // Extract the HTML table from the response
                        // Remove the markdown code block markers
                        text = text.replaceAll("```html\\s*", "").replaceAll("\\s*```", "");
                        return text;
                    }
                }
            }

            return "<p>Unable to extract table from API response. Please try again.</p>";
        } catch (Exception e) {
            e.printStackTrace();
            return "<p>Error parsing API response: " + e.getMessage() + "</p>";
        }
    }
}