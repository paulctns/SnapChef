package com.example.pip_proiect;

import android.util.Base64;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.*;
/**
 * {@code GeminiHelper} este un utilitar care oferă metode pentru a interacționa cu
 * API-ul Gemini, un serviciu de generare de conținut de la Google.
 * <p>
 * Clasa permite trimiterea de prompturi text și imagini pentru generarea de răspunsuri
 * din partea modelului Gemini. Toate cererile sunt efectuate asincron folosind biblioteca
 * OkHttp.
 *
 */
public class GeminiHelper {

    /**
     * Interfață pentru gestionarea răspunsurilor API-ului Gemini.
     * Oferă metode pentru a trata atât rezultatele de succes cât și erorile.
     */

    public interface GeminiCallback {
        void onResponse(String result);
        void onError(String error);
    }

    /**
     * Cheia API folosită pentru autentificarea cererilor la endpoint-ul Gemini.
     */
    private static final String API_KEY = "AIzaSyDWKqGa8Bu_Ncve1GdAS5_m2_Nsv3UH2_U";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    /**
     * Trimite o cerere la API-ul Gemini care conține un prompt text și o imagine (JPEG) codificată Base64.
     * Aceasta combină ambele surse de informații pentru generarea de conținut.
     * <p>
     * Rezultatul sau eroarea sunt transmise asincron prin {@link GeminiCallback}.
     *
     * @param imageBytes  Imaginea în format byte[] (JPEG), codificată automat în Base64.
     * @param prompt      Textul promptului (de exemplu: "Descrie această imagine").
     * @param callback    Callback-ul pentru a primi rezultatul generat sau eroarea.
     */
    public static void sendImageAndTextRequest(byte[] imageBytes, String prompt, GeminiCallback callback) {
        try {
            String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

            JSONObject imagePart = new JSONObject();
            imagePart.put("inlineData", new JSONObject()
                    .put("mimeType", "image/jpeg")
                    .put("data", base64Image));

            JSONObject textPart = new JSONObject();
            textPart.put("text", prompt);

            JSONArray parts = new JSONArray();
            parts.put(textPart);
            parts.put(imagePart);

            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("contents", new JSONArray().put(content));

            RequestBody requestBody = RequestBody.create(
                    requestBodyJson.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(GEMINI_URL)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("Eroare rețea Gemini: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (!response.isSuccessful()) {
                        callback.onError("Cod: " + response.code() + " Răspuns: " + responseBody);
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(responseBody);
                        JSONArray candidates = json.getJSONArray("candidates");
                        JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                        JSONArray parts = content.getJSONArray("parts");
                        String text = parts.getJSONObject(0).getString("text");
                        callback.onResponse(text);
                    } catch (JSONException e) {
                        callback.onError("Eroare parsare JSON: " + e.getMessage());
                    }
                }
            });

        } catch (JSONException e) {
            callback.onError("Eroare JSON: " + e.getMessage());
        }
    }

    /**
     * Trimite o cerere la API-ul Gemini doar cu un prompt text.
     * Metoda poate fi folosită pentru a obține un răspuns textual generat de modelul Gemini.
     * <p>
     * Rezultatul sau eroarea sunt transmise asincron prin {@link GeminiCallback}.
     *
     * @param prompt   Textul promptului pentru generarea răspunsului.
     * @param callback Callback-ul pentru a primi rezultatul generat sau eroarea.
     */
    public static void sendTextRequest(String prompt, GeminiCallback callback) {
        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject textPart = new JSONObject();
            textPart.put("text", prompt);

            JSONObject content = new JSONObject();
            content.put("parts", new JSONArray().put(textPart));

            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", new JSONArray().put(content));

            RequestBody body = RequestBody.create(
                    requestBody.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(GEMINI_URL)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("Eroare rețea Gemini: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseStr = response.body().string();
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(responseStr);
                            JSONArray candidates = json.getJSONArray("candidates");
                            JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                            JSONArray parts = content.getJSONArray("parts");
                            String replyText = parts.getJSONObject(0).getString("text");

                            callback.onResponse(replyText);
                        } catch (JSONException e) {
                            callback.onError("Eroare parsare JSON: " + e.getMessage());
                        }
                    } else {
                        callback.onError("Răspuns invalid Gemini: " + response.code() + "\n" + responseStr);
                    }
                }
            });
        } catch (JSONException e) {
            callback.onError("Eroare JSON: " + e.getMessage());
        }
    }
}
