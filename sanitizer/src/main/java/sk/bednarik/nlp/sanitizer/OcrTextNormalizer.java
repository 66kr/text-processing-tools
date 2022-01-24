package sk.bednarik.nlp.sanitizer;

/**
 *
 * Basic normalizer for slovak diacritics in OCRed text
 */
public class OcrTextNormalizer {

  public String sanitize(String input) {
    input = input.replace("l'", "ľ");
    input = input.replace("d'", "ď");
    input 