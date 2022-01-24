package sk.bednarik.nlp.sanitizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 *
 * Simple heuristic normalization of OCRed text based on wordlist
 */
public class OcrTokenNormalizer {