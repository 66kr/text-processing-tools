package sk.bednarik.nlp.parser;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class ConlluAnnotator implements Annotator {

  @Override
  public void annotate(Annotation annotation) {
    annotation.get(CoreAnnotations.SentencesAnnotation.class).stream()
        .map(sentence -> sentence
            .get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class))
        .forEach(sentenceGraph -> {
          sentenceGraph.edgeIterable().forEach(edge -> {
            edge.getTarget()
                .set(CoreAnnotations.CoNLLDepParentIndexAnnotation.class, edge.getSource().index());
            edge.getTarg