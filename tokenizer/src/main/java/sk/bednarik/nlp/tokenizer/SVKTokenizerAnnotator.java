package sk.bednarik.nlp.tokenizer;

import edu.stanford.nlp.international.french.process.FrenchTokenizer;
import edu.stanford.nlp.international.spanish.process.SpanishTokenizer;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.ArabicSegmenterAnnotator;
import edu.stanford.nlp.pipeline.ChineseSegmenterAnnotator;
import edu.stanford.nlp.pipeline.LanguageInfo;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.AbstractTokenizer;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.WhitespaceTokenizer;
import edu.stanford.nlp.process.WordToSentenceProcessor;
import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.PropertiesUtils;
import edu.stanford.nlp.util.logging.Redwood;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * This class will PTB tokenize the input.  It assumes that the original String is under the
 * CoreAnnotations.TextAnnotation field and it will add the output from the InvertiblePTBTokenizer ({@code
 * List<CoreLabel>}) under CoreAnnotation.TokensAnnotation.
 *
 * @author Jenny Finkel
 * @author Christopher Manning
 * @author Ishita Prasad
 */
public class SVKTokenizerAnnotator implements Annotator {

  /**
   * A logger for this class
   */
  private static final Redwood.RedwoodChannels log = Redwood.channels(SVKTokenizerAnnotator.class);

  /**
   * Enum to identify the different TokenizerTypes. To add a new TokenizerType, add it to the list with a default
   * options string and add a clause in getTokenizerType to identify it.
   */
  public enum TokenizerType {
    Unspecified(null, null, "invertible,ptb3Escaping=true"),
    Arabic("ar", null, ""),
    Chinese("zh", null, ""),
    Spanish("es", "SpanishTokenizer", "invertible,ptb3Escaping=true,splitAll=true"),
    English("en", "PTBTokenizer", "invertible,ptb3Escaping=true"),
    Slovak("sk", "SVKPTBTokenizer", "invertible,ptb3Escaping=true"),
    German("de", null, "invertible,ptb3Escaping=true"),
    French("fr", "FrenchTokenizer", ""),
    Whitespace(null, "WhitespaceTokenizer", "");

    private final String abbreviation;
    private final String className;
    private final String defaultOptions;

    TokenizerType(String abbreviation, String className, String defaultOptions) {
      this.abbreviation = abbreviation;
      this.className = className;
      this.defaultOptions = defaultOptions;
    }

    public String getDefaultOptions() {
      return defaultOptions;
    }

    private static final Map<String, TokenizerType> nameToTokenizerMap = initializeNameMap();

    private static Map<String, TokenizerType> initializeNameMap() {
      Map<String, TokenizerType> map = Generics.newHashMap();
      for (TokenizerType type : TokenizerType.values()) {
        if (type.abbreviation != null) {
          map.put(type.abbreviation.toUpperCase(), type);
        }
        map.put(type.toString().toUpperCase(), type);
      }
      return Collections.unmodifiableMap(map);
    }

    private static final Map<String, TokenizerType> classToTokenizerMap = initializeClassMap();

    private static Map<String, TokenizerType> initializeClassMap() {
      Map<String, TokenizerType> map = Generics.newHashMap();
      for (TokenizerType type : TokenizerType.values()) {
        if (type.className != null) {
          map.put(type.className.toUpperCase(), type);
        }
      }
      return Collections.unmodifiableMap(map);
    }

    /**
     * Get TokenizerType based on what's in the properties.
     *
     * @param props Properties to find tokenizer options in
     * @return An element of the TokenizerType enum indicating the tokenizer to use
     */
    public static TokenizerType getTokenizerType(Properties props) {
      String tokClass = props.getProperty("tokenize.class", null);
      boolean whitespace = Boolean.valueOf(props.getProperty("tokenize.whitespace", "false"));
      String language = props.getProperty("tokenize.language", "en");

      if (whitespace) {
        return Whitespace;
      }

      if (tokClass != null) {
        TokenizerType type = classToTokenizerMap.get(tokClass.toUpperCase());
        if (type == null) {
          throw new IllegalArgumentException("SVKTokenizerAnnotator: unknown tokenize.class property " + tokClass);
        }
        return type;
      }

      if (language != null) {
        TokenizerType type = nameToTokenizerMap.get(language.toUpperCase());
        if (type == null) {
          throw new IllegalArgumentException("SVKTokenizerAnnotator: unknown tokenize.language property " + language);
        }
        return type;
      }

      return Unspecified;
    }
  } // end enum TokenizerType


  public static final String EOL_PROPERTY = "tokenize.keepeol";

  private final boolean VERBOSE;
  private final TokenizerFactory<CoreLabel> factory;

  /**
   * new segmenter properties
   **/
  private final boolean useSegmenter;
  private final Annotator segmenterAnnotator;

  // CONSTRUCTORS

  /**
   * Gives a non-verbose, English tokenizer.
   */
  public SVKTokenizerAnnotator() {
    this(false);
  }


  private static String computeExtraOptions(Properties properties) {
    String extraOptions = null;
    boolean keepNewline = Boolean
        .valueOf(properties.getProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "fals