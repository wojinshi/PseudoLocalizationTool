/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ringcentral.qa.xmn.i18n.pseudolocalization.methods;

import com.google.i18n.pseudolocalization.PseudolocalizationMethod;
import com.google.i18n.pseudolocalization.PseudolocalizationPipeline;
import com.google.i18n.pseudolocalization.message.DefaultVisitor;
import com.google.i18n.pseudolocalization.message.NonlocalizableTextFragment;
import com.google.i18n.pseudolocalization.message.TextFragment;
import com.google.i18n.pseudolocalization.message.VisitorContext;

/**
 *
 * @author julia.lin
 */
/**
 * A {@link PseudolocalizationMethod} that adds brackets around the
 * message that needs to be localiz, to help identify where the application is concatenating separate
 * messages (which is bad because some locales might need to change the two
 * messages when concatenated, such as rearranging the order).  Generally, this
 * should be the last method applied.
 */
public class BracketAdder extends DefaultVisitor implements PseudolocalizationMethod {

    static {
        PseudolocalizationPipeline.registerMethodClass("brackets", BracketAdder.class);
    }

    @Override
    public void visitTextFragment(VisitorContext ctx, TextFragment fragment) {
        NonlocalizableTextFragment prefix = ctx.createNonlocalizableTextFragment("[");
        ctx.insertBefore(fragment, prefix);
        NonlocalizableTextFragment suffix = ctx.createNonlocalizableTextFragment("]");
        ctx.insertAfter(fragment, suffix);
    }
//  @Override
//  public MessageFragmentVisitor visitMessage(VisitorContext ctx, TextFragment textFragment) {
//    
//      NonlocalizableTextFragment prefix = ctx.createNonlocalizableTextFragment("[");
//    ctx.insertBefore(null, prefix);
//    // no need to visit all of the message
//    return null;
//  }
}
