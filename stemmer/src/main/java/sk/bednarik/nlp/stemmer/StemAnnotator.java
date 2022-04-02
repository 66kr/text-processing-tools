package sk.bednarik.nlp.stemmer;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.logging.Redwood;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;


public class StemAnnotator implements Annotator {

  private static Redwood.RedwoodChannels log = Redwood.channels(StemAnnotator.class);

  public StemAnnotator(boolean enhanceWithPOS) throws IOException {
    slovakStemmer = new SlovakStemmer();
  }

  public void annotate(Annotation annotation) {
    if (annotation.containsKey(CoreAnnotations.SentencesAnnotation.class)) {
      annotation.get(CoreAnnotations.SentencesAnnotation.class)
          .forEach(sentence -> sentence.get(CoreAnnotations.TokensAnnotation.class)
              .forEach(token -> {
                token.set(CoreAnnotations.StemAnnotation.class, stem(token.get(CoreAnnotations.TextAnnotation.class)));
              }));
    } else {
      throw new RuntimeException("Unable to find words/tokens in: " +
          annotation);
    }
  }

  private SlovakStemmer slovakStemmer;

  public String ste