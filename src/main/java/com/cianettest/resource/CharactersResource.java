package com.cianettest.resource;

import com.cianettest.model.Character;
import com.cianettest.model.Comic;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.*;

@RestController
public class CharactersResource {

    private static final String API_PUBLIC_KEY = "bce7b1e856554ab646ff3497dfed63bf";
    private static final String API_PRIVATE_KEY = "2a3fc59492db34bc5f6ce9813da14be6e463924a";

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(value = "/characters", method = RequestMethod.GET)
    public ResponseEntity<List<Character>> listCharacters() {
        try {
            String authentication = authenticate();
            final String uri = "https://gateway.marvel.com/v1/public/characters?" + authentication;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode results = root.path("data").path("results");
            List<Character> characters = Arrays.asList(mapper.readValue(results.toString(), Character[].class));
            return ResponseEntity.status(HttpStatus.OK).body(characters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(value = "/characters/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<Comic>> listComics(@PathVariable String id) {
        try {
            String authentication = authenticate();
            final String uri = "https://gateway.marvel.com/v1/public/characters/"+ id + "/comics?" + authentication;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode results = root.path("data").path("results");
            List<Comic> characters = Arrays.asList(mapper.readValue(results.toString(), Comic[].class));
            return ResponseEntity.status(HttpStatus.OK).body(characters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String authenticate() throws Exception {
        long ts = new Date().getTime();
        String stringToHash = ts + API_PRIVATE_KEY + API_PUBLIC_KEY;
        String hash = getMD5Hash(stringToHash);
        return "ts=" + ts + "&apikey=" + API_PUBLIC_KEY + "&hash=" + hash;
    }

    private String getMD5Hash(String stringToHash) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(stringToHash.getBytes());
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toLowerCase();
    }
}
