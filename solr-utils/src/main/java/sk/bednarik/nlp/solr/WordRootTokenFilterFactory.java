package sk.bednarik.nlp.solr;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.store.InputStreamDataInput;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.fst.CharSequenceOutputs;
import org.apache.lucene.util.fst.FST;

public class WordRootTokenFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {
    private boolean useStemmer;
    private FST<CharsRef> fst;

    public static final String PARAM_DICTIONARY = "fst";
    public static final String PA