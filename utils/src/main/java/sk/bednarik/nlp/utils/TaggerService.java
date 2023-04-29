package sk.bednarik.nlp.utils;

import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import sk.bednarik.nlp.commons.AnnUtils;
import sk.bednarik.nlp.ssplit.spring.SSplitLinguisticComponent;
import sk.bednarik.nlp.tagger.spring.POSTaggerComponent;
import sk.bednarik.nlp.tokenizer.spring.TokenizerComponent;

@Service
@Import({TokenizerComp