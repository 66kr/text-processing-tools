package sk.bednarik.nlp.utils;

import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import sk.bednarik.nlp.ner.spring.NERHybridPipeline;
import sk.bednarik.nlp.ner.spring.NERLinguisticPipeline;
import sk.bednarik.nlp.spring.FSTLemmaC