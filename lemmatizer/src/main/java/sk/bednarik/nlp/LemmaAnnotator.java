package sk.bednarik.nlp;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.logging.Redwood;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.lucene.store.InputStreamDataInput;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Char