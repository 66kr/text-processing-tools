package sk.bednarik.nlp.solr;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class SlovakStemFilterFactory extends TokenFilterFactory {
  
  /** Creates a new SlovakStemFilterFa