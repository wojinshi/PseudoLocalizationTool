/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ringcentral.qa.xmn.i18n.pseudolocalization.methods;

import com.google.i18n.pseudolocalization.PseudolocalizationMethod;
import com.google.i18n.pseudolocalization.PseudolocalizationPipeline;
import com.google.i18n.pseudolocalization.message.DefaultVisitor;
import com.google.i18n.pseudolocalization.message.MessageFragment;
import com.google.i18n.pseudolocalization.message.TextFragment;
import com.google.i18n.pseudolocalization.message.VisitorContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author julia.lin
 */
public class JsPreserver extends DefaultVisitor implements PseudolocalizationMethod {
    private static final Pattern REFERENCE_STRING_PATTERN = Pattern.compile(
            "^@string/.*");
    private static final Pattern TAG_PATTERN = Pattern.compile(
            "(<(\\S+)[^>]*>)[\\s\\S]*(</\\2>)");

    static {
        PseudolocalizationPipeline.registerMethodClass("js", XmlPreserver.class);
    }

    @Override
    public void visitTextFragment(final VisitorContext ctx, TextFragment text) {
        final List<MessageFragment> result = new ArrayList<MessageFragment>();
        try {
            String xml = text.getText();
            if (xml.startsWith("<?xml")) {
                int end = xml.indexOf("?>");
                String encoding = xml.substring(0, end + 2) + "\n";
                result.add(ctx.createNonlocalizableTextFragment(encoding));
            }            
            Document document = DocumentHelper.parseText(text.getText());            
            visitNodes(ctx, text, result, document);
            ctx.replaceFragment(text, result);
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
    }

    private void visitNodes(final VisitorContext ctx, TextFragment text, List<MessageFragment> result, Branch root) {
        Iterator<Node> iterator = root.nodeIterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            String xml = node.asXML();
            if (node.getNodeType() == Node.COMMENT_NODE) {
                result.add(ctx.createNonlocalizableTextFragment(xml));
            } else if (node.getNodeType() == Node.TEXT_NODE) {
                result.add(ctx.createNonlocalizableTextFragment(xml));
            } else if (node.getNodeType() == Node.ELEMENT_NODE) {
                String startTag = xml.trim().replaceAll(TAG_PATTERN.pattern(), "$1");
                String endTag = xml.trim().replaceAll(TAG_PATTERN.pattern(), "$3");
                result.add(ctx.createNonlocalizableTextFragment(startTag));
                if (((Element) node).isTextOnly()) {
                    if (!node.getText().matches(REFERENCE_STRING_PATTERN.pattern())) {
                        result.add(ctx.createTextFragment(node.getText()));
                    } else {
                        result.add(ctx.createNonlocalizableTextFragment(node.getText()));
                    }
                } else {
                    visitNodes(ctx, text, result, (Element) node);
                }
                result.add(ctx.createNonlocalizableTextFragment(endTag));
            } else if (node.getNodeType() == Node.NAMESPACE_NODE) {
                // do nothing
            } else {
                result.add(ctx.createNonlocalizableTextFragment(xml));
            }
        }
    }
}
