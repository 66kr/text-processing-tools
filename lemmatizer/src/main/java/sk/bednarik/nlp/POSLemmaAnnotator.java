package sk.bednarik.nlp;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.logging.Redwood;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

public class POSLemmaAnnotator implements Annotator {

  private HashMap<String, String> lemmaMap;
  private final boolean keepOriginal;

  private static Redwood.RedwoodChannels log = Redwood.channels(POSLemmaAnnotator.class);

  public POSLemmaAnnotator(InputStream file) {
    this(file, new Properties());
