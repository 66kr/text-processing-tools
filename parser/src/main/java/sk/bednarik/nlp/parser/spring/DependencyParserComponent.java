package sk.bednarik.nlp.parser.spring;

import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.DependencyParseAnnotator;
import java.util.Properties;
import sk.bednarik.nlp.commons.AnnComponent;

public class DependencyParserComponent extends AnnComponent {

  @Override
  protected Annotator prepareAnnotator() {
    Prope