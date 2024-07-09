package org.test.log;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;

import java.util.concurrent.atomic.AtomicInteger;

public class LimitCauseThrowableProxyConverter extends ThrowableProxyConverter {

    private static final int MAX_FIRST_CAUSES = 1;
    private static final int MAX_LAST_CAUSES = 2;

    @Override
    protected String throwableProxyToString(IThrowableProxy tp) {
        StringBuilder sb = new StringBuilder(2048);
        int causeCount = 0;
        StringBuilder lastCauses = new StringBuilder(2048);
        this.recursiveAppend(sb, (String)null, 1, tp, causeCount, new AtomicInteger(0), lastCauses);
        sb.append("... Hidden causes ...");
        sb.append(CoreConstants.LINE_SEPARATOR);
        sb.append(lastCauses);
        return sb.toString();
    }

    private void recursiveAppend(StringBuilder sb, String prefix, int indent, IThrowableProxy tp, int causeCount, AtomicInteger lastCausesCount, StringBuilder lastCauses) {
        if (causeCount > MAX_FIRST_CAUSES && MAX_LAST_CAUSES <= 0) {
            return;
        }
        if (tp != null) {
            if (causeCount <= MAX_FIRST_CAUSES) {
                readException(sb, prefix, indent, tp, causeCount, lastCausesCount, lastCauses);
                this.recursiveAppend(sb, "Caused by: ", indent, tp.getCause(), ++causeCount, lastCausesCount, lastCauses);
            } else {
                this.recursiveAppend(sb, "Caused by: ", indent, tp.getCause(), ++causeCount, lastCausesCount, lastCauses);
                StringBuilder newCause = new StringBuilder(2048);
                lastCausesCount.incrementAndGet();
                readException(newCause, prefix, indent, tp, 0, lastCausesCount, lastCauses);
                if (lastCausesCount.get() > MAX_LAST_CAUSES) {
                    return;
                } else {
                    lastCauses.insert(0, newCause);
                }
            }
        }
    }

    private void readException(StringBuilder sb, String prefix, int indent, IThrowableProxy tp, int causeCount, AtomicInteger lastCausesCount, StringBuilder lastCauses) {
        this.subjoinFirstLine(sb, prefix, indent, tp);
        sb.append(CoreConstants.LINE_SEPARATOR);
        this.subjoinSTEPArray(sb, indent, tp);
        IThrowableProxy[] suppressed = tp.getSuppressed();
        if (suppressed != null) {
            IThrowableProxy[] arr$ = suppressed;
            int len$ = suppressed.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                IThrowableProxy current = arr$[i$];
                this.recursiveAppend(sb, "Suppressed: ", indent + 1, current, causeCount, lastCausesCount, lastCauses);
            }
        }
    }

    private void subjoinFirstLine(StringBuilder buf, String prefix, int indent, IThrowableProxy tp) {
        ThrowableProxyUtil.indent(buf, indent - 1);
        if (prefix != null) {
            buf.append(prefix);
        }
        this.subjoinExceptionMessage(buf, tp);
    }

    private void subjoinExceptionMessage(StringBuilder buf, IThrowableProxy tp) {
        buf.append(tp.getClassName()).append(": ").append(tp.getMessage());
    }
}
