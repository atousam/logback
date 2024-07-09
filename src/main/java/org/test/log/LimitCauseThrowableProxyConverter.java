package org.test.log;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;

public class LimitCauseThrowableProxyConverter extends ThrowableProxyConverter {

    private static final int MAX_CAUSES = 2;

    @Override
    protected String throwableProxyToString(IThrowableProxy tp) {
        StringBuilder sb = new StringBuilder(2048);
        int causeCount = 0;
        this.recursiveAppend(sb, (String)null, 1, tp, causeCount);
        return sb.toString();
    }

    private void recursiveAppend(StringBuilder sb, String prefix, int indent, IThrowableProxy tp, int causeCount) {
        if (causeCount > MAX_CAUSES)
            return;
        if (tp != null) {
            this.subjoinFirstLine(sb, prefix, indent, tp);
            sb.append(CoreConstants.LINE_SEPARATOR);
            this.subjoinSTEPArray(sb, indent, tp);
            IThrowableProxy[] suppressed = tp.getSuppressed();
            if (suppressed != null) {
                IThrowableProxy[] arr$ = suppressed;
                int len$ = suppressed.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    IThrowableProxy current = arr$[i$];
                    this.recursiveAppend(sb, "Suppressed: ", indent + 1, current, causeCount);
                }
            }
            this.recursiveAppend(sb, "Caused by: ", indent, tp.getCause(), ++causeCount);
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
