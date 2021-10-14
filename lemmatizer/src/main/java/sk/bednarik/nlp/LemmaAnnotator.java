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
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.fst.CharSequenceOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Util;

import java.io.*;
import java.util.*;


public class LemmaAnnotator implements Annotator {

  private static Redwood.RedwoodChannels log = Redwood.channels(LemmaAnnotator.class);

  private final boolean keepOriginal;

  public LemmaAnnotator(String name, Properties properties) {
    String fstLoc = properties.getProperty(name + ".data");
    this.keepOriginal = Boolean.valueOf(properties.getProperty("keepO