package sk.bednarik.nlp.sanitizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrdinalSlashToBracketSanitizer {

  private static final Pattern ordinalWithSlash = Pattern.compile("[.\\s\\p{Z}][A-Za-z]/");

  public static String sanitize(String in