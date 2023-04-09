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
import sk.bednarik.nl