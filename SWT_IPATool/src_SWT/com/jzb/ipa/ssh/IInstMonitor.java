/**
 * 
 */
package com.jzb.ipa.ssh;

/**
 * @author n000013
 * 
 */
public interface IInstMonitor {

    public static IInstMonitor nullMonitor = new IInstMonitor() {

                                               public void installingBundle() {
                                               }

                                               public void processBegin() {
                                               }

                                               public void processEnd(final boolean failed) {
                                               }

                                               public void sendFileBegin() {
                                               }

                                               public void sendFileEnd() {
                                               }

                                               public void sendFileProgress(final int percentage) {
                                               }
                                           };

    public void installingBundle();

    public void processBegin();

    public void processEnd(final boolean failed);

    public void sendFileBegin();

    public void sendFileEnd();

    public void sendFileProgress(final int percentage);
}
