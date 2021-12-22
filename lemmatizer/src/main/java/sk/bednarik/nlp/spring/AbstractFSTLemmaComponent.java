package sk.bednarik.nlp.spring;

import edu.stanford.nlp.pipeline.Annotator;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import sk.bednarik.nlp.LemmaAnnotator;
import sk.bednarik.nlp.commons.AnnComponent;

/**
 * @author Pavol Berta
 * @since 21. 5. 2019
 */
public abstract class AbstractFSTLemmaComponent extends AnnComponent {

  /**
   * @param keepOriginal if l