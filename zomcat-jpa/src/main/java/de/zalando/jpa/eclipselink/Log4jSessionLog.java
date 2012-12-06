/**
 * Marcel Wieczorek
 * Zalando GmbH
 * Nov 9, 2012 3:16:06 PM
 */
package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.internal.localization.LoggingLocalization;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author  <a href="mailto:marcel.wieczorek@zalando.de" title="Marcel Wieczorek">mwieczorek</a>
 */
public class Log4jSessionLog extends AbstractSessionLog {

    private static final Logger LOG = LoggerFactory.getLogger("org.eclipse.persistence");

    public Log4jSessionLog() {
        setShouldPrintThread(true);
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.logging.AbstractSessionLog#log(org.eclipse.persistence.logging.SessionLogEntry)
     */
    @Override
    public void log(final SessionLogEntry sessionLogEntry) {
        if (shouldLog(sessionLogEntry.getLevel(), sessionLogEntry.getNameSpace())) {
            synchronized (this) {
                final String category = sessionLogEntry.getNameSpace();

                final StringBuilder sb = new StringBuilder();

                switch (level) {

                    case SEVERE :
                        if (SEVERE_PREFIX == null) {
                            SEVERE_PREFIX = LoggingLocalization.buildMessage("toplink_severe");
                        }

                        sb.append(SEVERE_PREFIX);
                        break;

                    case WARNING :
                        if (WARNING_PREFIX == null) {
                            WARNING_PREFIX = LoggingLocalization.buildMessage("toplink_warning");
                        }

                        sb.append(WARNING_PREFIX);
                        break;

                    case INFO :
                        if (INFO_PREFIX == null) {
                            INFO_PREFIX = LoggingLocalization.buildMessage("toplink_info");
                        }

                        sb.append(INFO_PREFIX);
                        break;

                    case CONFIG :
                        if (CONFIG_PREFIX == null) {
                            CONFIG_PREFIX = LoggingLocalization.buildMessage("toplink_config");
                        }

                        sb.append(CONFIG_PREFIX);
                        break;

                    case FINE :
                        if (FINE_PREFIX == null) {
                            FINE_PREFIX = LoggingLocalization.buildMessage("toplink_fine");
                        }

                        sb.append(FINE_PREFIX);
                        break;

                    case FINER :
                        if (FINER_PREFIX == null) {
                            FINER_PREFIX = LoggingLocalization.buildMessage("toplink_finer");
                        }

                        sb.append(FINER_PREFIX);
                        break;

                    case FINEST :
                        if (FINEST_PREFIX == null) {
                            FINEST_PREFIX = LoggingLocalization.buildMessage("toplink_finest");
                        }

                        sb.append(FINEST_PREFIX);
                        break;

                    default :
                        if (TOPLINK_PREFIX == null) {
                            TOPLINK_PREFIX = LoggingLocalization.buildMessage("toplink");
                        }

                        sb.append(TOPLINK_PREFIX);
                }

                if (category != null) {
                    sb.append("[").append(category).append("]: ");
                }

                sb.append(getSupplementDetailString(sessionLogEntry));

                if (sessionLogEntry.hasMessage()) {
                    sb.append(formatMessage(sessionLogEntry));
                }

                if (sessionLogEntry.hasException()) {
                    final Throwable t = sessionLogEntry.getException();
                    sb.append(t.toString());

                    if (shouldLogExceptionStackTrace()) {
                        LOG.error(sb.toString(), t);
                    } else {
                        LOG.error(sb.toString());
                    }
                } else {
                    switch (level) {

                        case SEVERE :
                            LOG.error(sb.toString());
                            break;

                        case WARNING :
                            LOG.warn(sb.toString());
                            break;

                        default :
                            LOG.info(sb.toString());
                    }
                }
            }
        }
    }

}
