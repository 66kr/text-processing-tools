package sk.bednarik.nlp.utils;

import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import sk.bednarik.nlp.ner.spring.NERHybridPipeline;
import sk.bednarik.nlp.ner.spring.NERLinguisticPipeline;
import sk.bednarik.nlp.spring.FSTLemmaComponent;
import sk.bednarik.nlp.ssplit.spring.SSplitLinguisticComponent;
import sk.bednarik.nlp.stemmer.spring.StemmerComponent;
import sk.bednarik.nlp.tagger.spring.POSTaggerComponent;
import sk.bednarik.nlp.times.spring.SVKNumberComponent;
import sk.bednarik.nlp.tokenizer.spring.TokenizerComponent;

@Service
@Import({TokenizerComponent.class, SSplitLinguisticComponent.class, FSTLemmaComponent.class, POSTaggerComponent.class,
    SVKNumberComponent.class, NERLinguisticPipeline.class, StemmerComponent.class, NERHybridPipeline.class})
public class NERService {

  private final Tokenize