/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ringcentral.qa.xmn.i18n.pseudolocalization.exception;

/**
 *
 * @author julia.lin
 */
public class CollectFileException extends RuntimeException {

    public CollectFileException() {
        super();
    }

    public CollectFileException(String message) {
        super(message);
    }

    public CollectFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CollectFileException(Throwable cause) {
        super(cause);
    }
}
