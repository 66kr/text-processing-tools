package sk.bednarik.nlp.statistics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Pavol Berta
 * @since 27. 2. 2019
 */
public class MapCounter {


  private final Map<String, Integer> tokens = new TreeMap<>();

  public void add(String token) {
    this.toke