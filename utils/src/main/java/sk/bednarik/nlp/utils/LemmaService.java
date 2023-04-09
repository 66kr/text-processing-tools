package sk.bednarik.nlp.utils;

import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import sk.bednarik.nlp.spring.FSTLemmaComponent;
import sk.bednarik.nlp.spring.FSTLemmaKeepNotLemmatizedComponent;
import sk.bednarik.nlp.spring.POSLemmaComponent;
import sk.bednarik.nlp.ssplit.spring.SSplitLinguisticComponent;
import sk.bednarik.nlp.tagger.spring.POSTaggerComponent;
import sk.bednarik.nlp.tokenizer.spring.TokenizerComponent;

@Service
@Import({TokenizerComponent.class, SSplitLinguisticComponent.class, FSTLemmaComponent.class, FSTLemmaKeepNotLemmatizedComponent.class, POSLemmaComponent.class,
    POSTaggerComponent.class})
public class LemmaService {

  private final TokenizerComponent tokenizerComponent;
  private final SSplitLinguisticComponent splitLinguisticComponent;
  private final FSTLemmaComponent fstLemmaComponent;
  private final FSTLemmaKeepNotLemmatizedComponent fstLemmaKeepNotLemmatizedComponent;
  private final POSLemmaComponent posLemmaComponent;
  private f