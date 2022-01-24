package sk.bednarik.nlp.sanitizer;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.util.ArraySet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class SpaceHighlightingSanitizerAnnotator implements Annotator {

  @Override
  public void annotate(Annotation annotation) {
    annotation.set(CoreAnnotations.OriginalTextAnnotation.class,
        annotation.get(CoreAnnotations.TextAnnotation.class));
    annotation.set(CoreAnnotations.TextAnnotation.class,
        SpaceHighlightingSanitizer.sanitize(annotation.get(CoreAnnotations.TextAnnotation.class)));
  }

  @Override
  public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() {
    r