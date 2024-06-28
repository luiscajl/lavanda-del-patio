package es.lavanda.qbittorrent.client.controller;

import es.lavanda.qbittorrent.client.model.MagnetIDTO;
import es.lavanda.lib.common.model.bt4g.Bt4gDTO;
import lombok.RequiredArgsConstructor;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import okhttp3.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/qbittorrent")
@RequiredArgsConstructor
@CrossOrigin(allowedHeaders = "*", origins = {"http://localhost:4200", "https://lavandadelpatio.es",
        "https://pre.lavandadelpatio.es"}, allowCredentials = "true", exposedHeaders = "*", methods = {
        RequestMethod.OPTIONS, RequestMethod.DELETE, RequestMethod.GET, RequestMethod.PATCH, RequestMethod.POST,
        RequestMethod.PUT}, originPatterns = {"*"})
public class TorrentController {

    private final OkHttpClient client = new OkHttpClient();

    @Value("${qbittorrent.url}")
    private String QBITTORRENT_URL;


    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addTorrent(@org.springframework.web.bind.annotation.RequestBody MagnetIDTO magnet) {


        try {
//            String cookie = qbittorrentLogin();

            RequestBody formBody = new FormBody.Builder()
                    .add("urls", magnet.getUrl())
                    .build();

            Request qbRequest = new Request.Builder()
                    .url(QBITTORRENT_URL + "/torrents/add")
                    .post(formBody)
//                    .addHeader("Cookie", cookie)
                    .build();

            try (Response response = client.newCall(qbRequest).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("message", "Torrent added successfully");
                return ResponseEntity.ok(responseBody);
            }
        } catch (IOException e) {
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("error", e.getMessage());
            return ResponseEntity.status(400).body(responseBody);
        }
    }


}
