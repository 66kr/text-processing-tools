package sk.bednarik.nlp.synonyms;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.logging.Redwood;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sk.bednarik.nlp.commons.AsurAnnotations;

public class SynonymsAnnotator implements Annotator {

  private HashSet<String> stopWords = new HashSet<>();
  private HashMap<String, List<List<String>>> slovnik = new HashMap<>();
  private HashMap<String, Set<String>> index = new HashMap<>();

  private HashMap<String, String[]> interchangeableWords = new HashMap<String, String[]>() {{
    put("sa", new String[]{"si"});
    put("si", new String[]{"sa"});
  }};

  private static Redwood.RedwoodChannels log = Redwood.channels(SynonymsAnnotator.class);

  //TODO: Improve performance
  public SynonymsAnnotator(InputStream synonymsFile, InputStream stopWordsFile) {
    prepareStopWords(stopWordsFile);
    prepareSynonyms(synonymsFile);
  }

  private void prepareStopWords(InputStream stopWordsFile) {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(stopWordsFile, "UTF-8"))) {
      br.lines()
          .forEach(word -> stopWords.add(word));
    } catch (IOException e) {
      log.error(e);
    }
  }

  private void prepareSynonyms(InputStream s