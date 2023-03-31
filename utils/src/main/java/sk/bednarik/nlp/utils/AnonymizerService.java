package sk.bednarik.nlp.utils;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import sk.bednarik.nlp.anonymizer.spring.AnonymizerPipeline;
import sk.bednarik.nlp.commons.AnnUtils;
import sk.bednarik.nlp.commons.AsurAnnotations;
import sk.bednarik.nlp.ner.spring.NERHybridPipeline;
import sk.bednarik.nlp.spring.FSTLemmaComponent;
import sk.bednarik.nlp.ssplit.spring.SSplitLinguisticComponent;
import sk.bednarik.nlp.tagger.spring.POSTaggerComponent;
import sk.bednarik.nlp.times.spring.SVKNumberComponent;
import sk