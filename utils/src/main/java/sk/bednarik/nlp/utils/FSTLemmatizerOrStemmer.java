package sk.bednarik.nlp.utils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.fst.CharSequenceOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Util;
import sk.bednarik.nlp.stemmer.SlovakStemmer;
import sk.bednarik.nlp.tokenizer.model.Token;
import sk.bednarik.nlp.utils.model.Lemma;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Option