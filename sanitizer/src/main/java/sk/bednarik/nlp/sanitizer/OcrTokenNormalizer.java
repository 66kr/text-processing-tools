package sk.bednarik.nlp.sanitizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 *
 * Simple heuristic normalization of OCRed text based on wordlist
 */
public class OcrTokenNormalizer {

  private HashSet<String> slovnik = new HashSet<>();

  public void init(InputStream slovnikFile) throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(slovnikFile, "UTF-8"))) {
      br.lines()
          .map(line -> line.split("\t"))
          .forEach(parts -> slovnik.add(parts[1]));
    }
  }

  public String sanitize(String token) {
    if (token.matches("\\p{L}*6\\p{L}")) {
      String newToken = token.replace("6", "á");
      if (slovnik.contains(newToken)) {
        token = newToken;
      }
      newToken = token.replace("6"