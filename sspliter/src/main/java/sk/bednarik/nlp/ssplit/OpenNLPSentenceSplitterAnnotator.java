package sk.bednarik.nlp.ssplit;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.ChunkAnnotationUtils;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.logging.Redwood;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import one.util.streamex.StreamEx;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

/**
 * This class assumes that there is a {@code List<CoreLabel>} under the {@code TokensAnnotation} field, and runs it
 * through {@link opennlp.tools.sentdetect.SentenceDetectorME} and puts the new {@code List<Annotation>} under the
 * {@code SentencesAnnotation} field.
 */
public class OpenNLPSentenceSplitterAnnotator implements Annotator {

  /**
   * A logger for this class
   */
  private static Redwood.RedwoodChannels log = Redwood.channels(OpenNLPSentenceSplitterAnnotator.class);

  private final boolean VERBOSE;

  private final boolean countLineNumbers;

  private SentenceDetectorME sentenceDetector;

  public OpenNLPSentenceSplitterAnnotator(boolean verbose, boolean countLineNumbers,
      InputStream modelFile) throws IOException {
    VERBOSE = verbose;
    this.countLineNumbers = countLineNumbers;
    SentenceModel model = new SentenceModel(modelFile);
    sentenceDetector = new SentenceDetectorME(model);
  }

  public OpenNLPSentenceSplitterAnnotator(boolean verbose, boolean countLineNumbers,
      String modelFile) throws IOException {
    this(verbose, countLineNumbers, new FileInputStream(modelFile));
  }


  /**
   * If setCountLineNumbers is set to true, we count line numbers by telling the underlying splitter to return empty
   * lists of tokens and then treating those empty lists as empty lines.  We don't actually include empty sentences in
   * the annotation, though.
   **/
  @Override
  public void annotate(Annotation annotation) {
    if (VERBOSE) {
      log.info("Sentence splitting ...");
    }
    if (!annotation.containsKey(CoreAnnotations.TokensAnnotation.class)) {
      throw new IllegalArgumentException("WordsToSentencesAnnotator: unable to find words/tokens in: " + annotation);
    }

    // get text and tokens from the document
    String text = annotation.get(CoreAnnotations.TextAnnotation.class);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    String docID = annotation.get(CoreAnnotations.DocIDAnnotation.class);
    // log.info("Tokens are: " + tokens);

    // assemble the sentence annotations
    int tokenOffset = 0;
    int lineNumber = 0;
    // section annotations to mark sentences with
    CoreMap sectionAnnotations = null;
    List<CoreMap> sentences = new ArrayList<>();
    int i = 0;
    int startToken = 0;
    for (Span sentenceSpans : sentenceDetector.sentPosDetect(text)) {
      if (countLineNumbers) {
        ++lineNumber;
      }

      // get the sentence text from the first and last character offsets
      int begin = sentenceSpans.getStart();
      int end = sentenceSpans.getEnd();
      String sentenceText = text.substring(begin, end);

      List<CoreLabel> sentenceTokens = StreamEx.of(tokens.stream())
          .skip(startToken)
          .takeWhile(token -> token.endPosition() <= end)
          .collect(Collectors.toList());
      startTok