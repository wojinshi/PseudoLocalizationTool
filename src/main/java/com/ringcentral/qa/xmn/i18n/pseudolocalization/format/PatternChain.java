/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ringcentral.qa.xmn.i18n.pseudolocalization.format;

import com.google.i18n.pseudolocalization.message.MessageFragment;
import com.google.i18n.pseudolocalization.message.SimpleNonlocalizableTextFragment;
import com.google.i18n.pseudolocalization.message.SimpleTextFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author julia.lin
 */
public class PatternChain {

    List<Pattern> patternList = new ArrayList<Pattern>();

    public void setPatterns(Pattern... patterns) {
        if (patterns == null || patterns.length == 0) {
            return;
        }
        patternList.addAll(Arrays.asList(patterns));
    }

    public void process(List<MessageFragment> result, String text) {
        if (patternList.isEmpty()) {
            return;
        }
        Iterator<Pattern> it = patternList.iterator();
        process(it, result, text);
    }

    private void process(Iterator<Pattern> it, List<MessageFragment> result, String text) {
        if (it.hasNext()) {
            Pattern pattern = it.next();
            Matcher m = pattern.matcher(text);
            int start = 0;
            while (m.find()) {
                String plainText = text.substring(start, m.start());
                start = m.end();
                String entity = m.group();
                if (plainText.length() > 0) {
                    //result.add(new SimpleTextFragment(plainText));
                    process(it, result, plainText);
                }
                result.add(new SimpleNonlocalizableTextFragment(entity));
            }
            String plainText = text.substring(start);
            if (plainText.length() > 0) {
                //result.add(new SimpleTextFragment(plainText));
                process(it, result, plainText);
            }
        } else {
            result.add(new SimpleTextFragment(text));
        }
    }
}
